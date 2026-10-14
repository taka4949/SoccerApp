# SoccerApp

Kotlinで個人開発している、**サッカーの試合情報と試合別のコメント掲示板を組み合わせたAndroidアプリ**です。

大会・試合情報の取得とローカルキャッシュ、試合別コメントの投稿・閲覧、一定件数に達したスレッドの自動切り替えと過去ログ閲覧を実装しています。Androidアプリだけでなく、KtorによるAPIとPostgreSQLによる永続化も開発しています。

> **開発状況（2026年10月14日）**：個人開発中のプロトタイプです。AndroidエミュレーターとローカルのKtorサーバーを使用する開発構成が中心で、ログイン認証・インターネット公開されたAPI・署名済みAPKの配布はまだ完了していません。以下では実装済みと予定を分けて記載しています。

## 目次

- [このアプリで解決したいこと](#このアプリで解決したいこと)
- [実装済みの機能](#実装済みの機能)
- [独自の工夫と設計上の判断](#独自の工夫と設計上の判断)
- [アーキテクチャ](#アーキテクチャ)
- [使用技術](#使用技術)
- [セットアップと動作環境](#セットアップと動作環境)
- [テスト](#テスト)
- [開発の進捗と今後の計画](#開発の進捗と今後の計画)

## このアプリで解決したいこと

試合日程・スコアを確認する行為と、特定の試合について意見を交わす行為を、同じ画面遷移の中で完結させることを目指しています。

ユーザーは `大会一覧 → 試合一覧 → 試合メニュー → コメント掲示板／過去ログ` の順に目的の試合へ移動します。試合をキーに投稿を管理することで、異なる試合の話題が混ざりにくい構成にしています。

## 実装済みの機能

### Androidアプリ

- [x] football-data.org APIから大会・試合情報を取得し、Jetpack Composeで表示
- [x] 試合日時、チーム名、スコア、チーム・大会ロゴの表示
- [x] Navigation Composeを用いた大会・試合・掲示板・過去ログ間の画面遷移
- [x] Roomへの大会・試合情報の保存／更新（`@Upsert`）
- [x] 外部APIの通信失敗時に、保存済みデータを使うフォールバック
- [x] Roomの`Flow`による試合情報変更の監視
- [x] Roomのスキーマ変更に伴うMigrationの実装
- [x] Ktor APIを利用した試合別コメントの取得・投稿
- [x] 投稿中の重複送信抑止、読み込み状態・エラー表示・再試行UI
- [x] 終了したスレッドの一覧・コメント過去ログの閲覧
- [x] DataStoreを用いた初回アカウント登録案内の表示済み状態の保存（スキップ可能）
- [x] ViewModel／StateFlow／UiStateでの画面状態管理
- [x] Hiltによる依存性注入と、用途別のRetrofitをQualifierで区別

**注意：初回登録案内は実装済みですが、アカウントの作成・ログイン・投稿権限の制御は未実装です。** 現状のコメント投稿では投稿者名を入力する方式を採用しています。

### Ktor API・PostgreSQL

- [x] `GET /health` によるヘルスチェック
- [x] `GET /matches/{matchId}/comments`：対象試合のコメント取得
- [x] `POST /matches/{matchId}/comments`：対象試合へのコメント投稿
- [x] `GET /matches/{matchId}/threads`：過去スレッド一覧
- [x] `GET /matches/{matchId}/threads/{threadId}/comments`：過去スレッドのコメント取得
- [x] JDBCによるPostgreSQLへの永続化
- [x] コメント投稿時のOPENスレッドの取得／必要に応じた新規作成
- [x] 1スレッド1,000コメントを上限とした自動切り替え
- [x] トランザクションとPostgreSQLのアドバイザリロックによる同一試合への並行投稿制御
- [x] 投稿データの空文字・文字数・試合IDの検証

## 独自の工夫と設計上の判断

### 1. コメントが1,000件に達したらスレッドを自動更新する

**課題**：試合が盛り上がったとき、投稿が無制限に一つのスレッドへ蓄積すると、話題の区切りや過去の会話の閲覧が難しくなります。

**実装**：投稿時に現在のOPENスレッドのコメント件数を確認し、1,000件到達後の次の投稿で旧スレッドをCLOSEDにして新しいスレッドを作成します。旧スレッドは過去ログから参照できます。

```text
試合A
 ├─ スレッド #1（1〜1,000件）→ CLOSED → 過去ログへ
 ├─ スレッド #2（次の投稿から）→ OPEN
 └─ ...
```

**整合性への配慮**：複数の投稿が同時に発生しても、同じ試合のスレッド作成・コメント保存が競合しにくいよう、`pg_advisory_xact_lock`とトランザクションを利用しています。保存途中で失敗した場合はロールバックします。1,000件目と1,001件目の切り替えを確認するDB統合テストも用意しています。

関連コード：[JdbcCommentRepository.kt](server/src/main/kotlin/com/example/soccerapp/server/JdbcCommentRepository.kt) ／ [JdbcCommentRepositoryIntegrationTest.kt](server/src/test/kotlin/com/example/soccerapp/server/JdbcCommentRepositoryIntegrationTest.kt)

### 2. 通信に失敗しても試合情報を表示できる構成

**課題**：外部APIの一時的な障害や通信不良で、試合情報が全く見えなくなることを避けたい。

**実装**：取得した大会・試合情報をRoomに保存し、次回の通信が失敗した場合には端末に保存済みのデータを返すようにしています。データ未保存で通信にも失敗した場合はエラー扱いにします。キャンセル例外は通常の通信エラーとは区別して再送出します。

```text
通信成功: football-data.org → Retrofit → Repository → Roomに保存 → UI
通信失敗: Repository → Roomの既存データ → UI
キャッシュなし: 通信失敗 → エラー状態
```

関連コード：[MatchRepository.kt](app/src/main/java/com/example/soccerapp/data/repository/MatchRepository.kt)

### 3. UI・データ取得・保存処理を分離する

**課題**：画面から直接APIやDBを操作すると、状態管理、テスト、仕様変更が難しくなります。

**実装**：Composeで画面を描画し、ViewModelで状態を管理し、Repositoryにデータ取得・変換を委ねています。APIのDTO、RoomのEntity、画面で使用するモデルは用途を分けています。

- `sealed interface`を使い、Loading・Success・Errorなどの画面状態を表現
- `StateFlow`を公開し、Compose側では`collectAsStateWithLifecycle()`で監視
- `viewModelScope`のCoroutineと`Job.cancel()`を使い、リーグ切り替え時の不要な取得・監視を整理
- HiltでRepository、API Service、Room、DAOなどの依存関係を注入
- Repositoryを差し替えたViewModelテストで成功・失敗ケースを検証

## アーキテクチャ

```text
Android
  Jetpack Compose (Screen / Route)
       ↓ UI action                  ↑ UiState / StateFlow
     ViewModel ──────────────────────┘
       ↓
    Repository
       ├─ Retrofit ──→ football-data.org
       ├─ Room (SQLite: 大会・試合キャッシュ)
       ├─ DataStore (初回案内の設定)
       └─ Retrofit ──→ Ktor API
                          ↓
                    CommentRepository
                          ↓ JDBC
                       PostgreSQL
```

```text
API JSON → DTO → Repository → Domain Model → ViewModel → Compose
API JSON → DTO → Repository → Room Entity → Room (保存)
Room Entity → Repository → Domain Model → ViewModel → Compose
```

リポジトリ構成：

- `app/`：Androidアプリ（画面、ViewModel、Repository、Room、Retrofit、Hilt、DataStore）
- `server/`：Ktor API、JDBC・PostgreSQL、API／DBテスト
- `app/src/test/`：Android側のユニットテスト
- `app/src/androidTest/`：Compose UIなどの計測テスト
- `server/src/test/`：Ktor API・PostgreSQLのテスト

## 使用技術

| 分類 | 技術 |
| --- | --- |
| 言語 | Kotlin |
| Android UI | Jetpack Compose、Material 3、Navigation Compose、Coil |
| アーキテクチャ | MVVM、Repositoryパターン、UDF、DTO／Entity／Domain Modelの分離 |
| 状態管理・非同期処理 | ViewModel、StateFlow、Flow、Coroutines、UiState |
| API通信 | Retrofit、OkHttp、kotlinx.serialization |
| ローカル保存 | Room、SQLite、DataStore |
| 依存性注入 | Hilt |
| バックエンド | Ktor Server、JDBC、PostgreSQL |
| ビルド・コード生成 | Gradle Kotlin DSL、KSP、kapt |
| テスト | JUnit、kotlinx-coroutines-test、Ktor Server Test、AndroidX Compose UI Test |

## セットアップと動作環境

現段階はローカル開発向けの構成です。**実行済みの完成品を配布しているわけではありません。**

### Android側

1. Android Studioでこのリポジトリを開き、Android SDKとGradleの同期を準備します。
2. [football-data.org](https://www.football-data.org/) のAPIトークンを取得します。
3. プロジェクト直下の`local.properties`に、Android SDKの設定に加えて以下を設定します。

```properties
FOOTBALL_DATA_API_TOKEN=your_api_token
```

4. Androidエミュレーターで`app`を実行します。

`local.properties`はGit管理対象外です。APIトークンをリポジトリにコミットしないでください。なお、現状の実装はトークンをBuildConfig経由でアプリに含めるため、**APK内で秘匿できる構成ではありません**。外部配布に向けて認証情報の扱いを再検討します。

### コメント用バックエンド

Ktorサーバーはローカル環境のPostgreSQLに接続して起動します。

- DB接続先：環境変数`SOCCER_DB_URL`（未指定時：`jdbc:postgresql://localhost:5432/soccer_app`）
- DBユーザー：`soccer_app_user`（現状の実装で固定）
- DBパスワード：環境変数`SOCCER_DB_PASSWORD`（必須）
- APIポート：`8080`

データベースと必要なテーブルは事前に準備してください。**テーブル作成用SQLの配布・自動初期化は現時点で整備途中**のため、クローンだけではDBを含む全機能が起動する状態ではありません。

設定後、プロジェクト直下からKtorサーバーを起動します。

```text
./gradlew :server:run
```

Windowsでは`gradlew.bat :server:run`を使用します。起動後は`http://localhost:8080/health`でヘルスチェックできます。

Android側のコメントAPIの接続先は現状`http://10.0.2.2:8080/`に固定されています。これはAndroidエミュレーターから開発用PCのローカルサーバーへ接続するためのアドレスで、**実機・外部ユーザー向けの公開URLではありません**。APK配布前にデプロイ先のHTTPS URLへ切り替える必要があります。

## テスト

テストコードとして以下を追加しています（このREADMEの更新作業ではテスト実行結果を保証していません）。

| 対象 | 確認する内容 |
| --- | --- |
| MatchDetailViewModel | 試合取得の成功／未検出／失敗 |
| MatchThreadViewModel | コメント取得・投稿時の画面状態 |
| CommentPostSection | 入力表示、入力可否、投稿操作、エラー表示 |
| Ktor CommentRoutes | 投稿・取得・不正な入力に対するAPI応答 |
| JdbcCommentRepository | 1,000件から次スレッドへの切り替えとDB内の状態 |

Android側のユニットテスト例：`./gradlew :app:testDebugUnitTest`

Ktor側のテスト例：`./gradlew :server:test`

Compose UIの計測テストにはエミュレーターまたは実機が必要です。DB統合テストは専用DB `soccer_app_test`を確認してから書き込みを行う構成です。接続先の設定なしにDB統合テストを実行しないでください。

## 開発の進捗と今後の計画

### 現在までに達成したこと

- [x] 大会・試合情報の取得とCompose UIへの表示
- [x] Roomによるキャッシュ・Migration・変更監視
- [x] KtorとPostgreSQLによるコメントAPIと永続化
- [x] コメント投稿・過去スレッド閲覧
- [x] 1,000件ごとのスレッド切り替えとトランザクション制御
- [x] Hilt、Repository、ViewModel、StateFlowによる責務分離
- [x] ViewModel・API・Compose UI・DB統合テストのコード追加
- [x] DataStoreによる初回登録案内の表示状態の保存

### 次に取り組む機能（予定）

1. **アカウント認証**：メールアドレスとパスワードによる登録・ログイン。未登録ユーザーは閲覧のみ、登録ユーザーは投稿可能とする。メール認証は後段。
2. **交流機能の拡張**：いいね、画像投稿、自分のコメント履歴、コメントのコピー・検索など。コメントへの返信・引用、返信先への移動も追加する。
3. **サッカー情報の拡張**：ニュース一覧、記事ごとのコメント機能。
4. **UI/UX改善**：Material 3に沿ったホーム画面、ナビゲーション、アイコン、操作性の改善。
5. **公開可能なバックエンド**：Dockerによる開発環境整理、AWSへのKtor・PostgreSQL配置、HTTPS対応、必要なセキュリティ対策。
6. **リアルタイム性の強化**：WebSocketによるコメント更新、再接続・ライフサイクル制御。
7. **品質保証とAPK配布**：追加テスト、GitHub ActionsによるCI、署名済みAPKの作成、GitHub Releasesを通じた配布。
8. **紹介用Webサイトの制作・更新**：アプリの概要、主要機能、画面イメージ、開発の進捗、更新内容、技術的な取り組みを掲載するWebサイトを新たに制作し、開発に合わせて継続的に更新する。APKの配布を開始した際はダウンロード先や利用手順も案内する。
9. **正式リリース**：アプリの動作検証、公開バックエンドの整備、認証・セキュリティ対応、操作性の改善などを進め、将来的にはGoogle Playでの公開を目指す。
10. **通報・ミュートと投稿管理**：コメントの通報、利用者によるミュート、管理者による投稿非表示を追加し、公開後の荒らしへの対応手段を用意する。
11. **スコア予想・ポイントランキング**：ログインしたユーザーが試合前にスコアを予想し、結果に応じてポイントを獲得する。リーグ・シーズンごとの合計ポイントでランキングを表示する。

#### スコア予想・ランキングの仮仕様（未実装）

- 1人につき1試合1件の予想を登録でき、試合開始前なら変更可能。締切はサーバー側で判定する。
- スコア完全一致は3ポイント、勝ち・引き分け・負けのみ一致は1ポイント、不一致は0ポイント。完全一致に勝敗分を重ねて加算しない。
- リーグ・シーズンごとにポイントを集計し、同点は同順位とする。
- 最初は1リーグで、予想入力・保存、締切、採点、ランキングまでを実装してから対象を広げる。
- 採点を再実行してもポイントが二重加算されないようにし、延期・中止は採点保留、結果訂正時は再採点できる構成を目指す。
- ランキング用の公開表示名と掲示板のスレッド別匿名IDを分け、ランキングから他スレッドの投稿履歴をたどれない形にする。

上記は開発の方向性を示したもので、着手順や公開時期を固定するものではありません。まずは**署名済みAPKを作成して動作確認・配布できる状態**を目指し、並行して**紹介用Webサイトを制作・更新**していく予定です。その後、アプリの品質と運用環境を整え、**Google Playでの正式リリース**も目標としています。

現状のコメントAPIはローカルサーバー向けの設定です。外部配布にあたっては、公開APIへの接続、認証、APIトークンの取り扱い、HTTPS、運用上の安全性などを確認・対応する必要があります。

### 追加構想：試合予想・交流表現の拡張（検討段階）

独立した「手紙アプリ」をもう一つ作るのではなく、SoccerAppの試合別交流を深める方向で検討しています。**以下は未実装の構想です。**

- **試合予想タイムカプセル**：試合前に予想やメッセージを投稿し、締切後または指定時刻に内容を公開する機能。まずは投稿・締切・公開という小さな単位で検証。
- **マッチレター**：タイムカプセルを発展させ、メッセージのランダム配達や後日の返信を取り入れる案。
- **戦術ボード**：選手配置や試合展開のアイデアを視覚的に共有する機能。

最初から全機能を同時に実装するのではなく、基本的な実装を確認してから必要な要素を段階的に追加します。
