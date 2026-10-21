# SoccerApp

## 画面・画像・UIを見る

**[アプリの画面・画像・UIはこちらから見られます → 紹介ホームページ](https://soccerapp-showcase-taka4949.saltyrice4.chatgpt.site)**

インストール不要で、起動画面・登録画面・ホーム・リーグ一覧・試合一覧・掲示板など、実際のスクリーンショット9枚を確認できます。各画面の実装方法を短く説明し、Kotlinコードの抜粋も掲載しています。学習内容を残す「知識ノート」も用意しています。

Kotlinで個人開発している、**サッカーの試合情報と試合別のコメント掲示板を組み合わせたAndroidアプリ**です。

大会・試合情報の取得とローカルキャッシュ、試合別コメントの投稿・閲覧、一定件数に達したスレッドの自動切り替えと過去ログ閲覧、メールアドレス・パスワードによるアカウント登録、セッショントークンを用いた認証基盤を実装しています。Androidアプリだけでなく、KtorによるAPIとPostgreSQLによる永続化も開発しています。

> **開発状況（2026年10月22日）**：個人開発中のプロトタイプです。AndroidアプリとKtor API・PostgreSQLを開発し、コメントAPIはOCI Compute VMに配置してCaddy経由のHTTPS通信を確認しています。Ktorのログイン・セッションAPIとAndroidの暗号化トークン保存・起動時の認証状態確認を実装し、GitHubの`main`に統合しました。ローカルPostgreSQLを用いたアカウント登録・トークン発行・`GET /auth/me`の認証、AndroidのDebugビルド、KtorのCIを確認しています。署名付きRelease APK（v0.1.0）を作成し、Pixel 7エミュレータで起動を確認してGitHub ReleasesでPre-releaseとして公開しました。

## APKのダウンロード

SoccerAppの初期テスト版（v0.1.0）をGitHub Releasesで公開しています。

**[Android向けAPKをダウンロード](https://github.com/taka4949/SoccerApp/releases/download/v0.1.0/app-release.apk)**

[GitHub Releases](https://github.com/taka4949/SoccerApp/releases/tag/v0.1.0)

### インストール方法

1. Android端末でAPKをダウンロードします。
2. ダウンロードしたファイルを開きます。
3. 必要に応じて、ダウンロード元からのアプリのインストールを許可します。
4. インストールが完了したらSoccerAppを起動します。

### 動作確認

- Android StudioのPixel 7エミュレータでRelease APKの起動を確認
- エミュレータで画面表示、コメント閲覧・投稿、アカウント登録、ログイン・ログアウトを確認（Debug版）
- Android実機での動作は未確認

現在は開発途中のPre-releaseです。コメント投稿のログイン必須化は未実装です。また、公開APKの一般ユーザー環境からのバックエンド接続は未検証です。

## 目次

- [画面・画像・UIを見る](#画面画像uiを見る)
- [APKのダウンロード](#apkのダウンロード)
- [このアプリで解決したいこと](#このアプリで解決したいこと)
- [実装済みの機能](#実装済みの機能)
- [独自の工夫と設計上の判断](#独自の工夫と設計上の判断)
- [アーキテクチャ](#アーキテクチャ)
- [使用技術](#使用技術)
- [Oracle Cloudへのデプロイ](#oracle-cloudへのデプロイ)
- [セットアップと動作環境](#セットアップと動作環境)
- [テスト](#テスト)
- [CI（自動テスト）](#ci自動テスト)
- [開発の進捗と今後の計画](#開発の進捗と今後の計画)

## このアプリで解決したいこと

試合日程・スコアを確認する行為と、特定の試合について意見を交わす行為を、同じ画面遷移の中で完結させることを目指しています。

ユーザーは `大会一覧 → 試合一覧 → 試合メニュー → コメント掲示板／過去ログ` の順に目的の試合へ移動します。試合をキーに投稿を管理することで、異なる試合の話題が混ざりにくい構成にしています。

## 実装済みの機能

### Androidアプリ

- [x] 起動画像の表示と、2.5秒後の画面切り替え
- [x] ダークトーンのホーム・登録・リーグ・試合・掲示板UI
- [x] メールアドレス・パスワードによるアカウント登録と登録完了表示
- [x] 登録時に受け取ったアクセストークンをAndroid KeystoreのAES-GCMで暗号化し、DataStoreに保存する処理
- [x] アプリ起動時の保存トークン復号・`GET /auth/me`による認証状態確認（Loading／LoggedIn／LoggedOut／Unavailable）
- [x] 認証状態の表示・再確認操作、ログイン済み時の新規登録ボタン非表示
- [x] 暗号化済みセッションデータをバックアップ・端末移行から除外する設定
- [x] 登録中の重複送信抑止、入力不備・メール重複・通信失敗の表示
- [x] 掲示板からのスコア手動更新と端末内データへの反映
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

**アカウント登録・登録直後のトークン保存・保存済みトークンによるログイン状態確認のコードを実装済みです。** 現状のコメント投稿では投稿者名を入力する方式を採用しています。ホームのニュース・予想ランキングはメニュー表示のみです。

### Ktor API・PostgreSQL

- [x] `POST /auth/register`：メールアドレス・パスワードによるアカウント作成。セッション発行を組み込み、成功時にアクセストークンと有効期限を返す
- [x] `POST /auth/login`：登録済みアカウントの資格情報検証とセッショントークン発行
- [x] `GET /auth/me`：Bearerトークンの検証とユーザーIDの取得（未認証時は401）
- [x] `POST /auth/logout`：指定セッションの無効化
- [x] `user_sessions` テーブルで有効期限付きセッションを管理（トークン自体ではなくSHA-256ハッシュを保存、期限は発行から30日）
- [x] メール形式・長さ、パスワード長（12〜128文字）の検証
- [x] メールアドレスの前後空白除去・小文字化と、重複登録時の`409 Conflict`応答
- [x] Argon2idによるパスワードのハッシュ化と、ユーザー情報のPostgreSQLへの保存
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

### Oracle Cloud（OCI）へのデプロイ

- [x] OCI ComputeにUbuntu 24.04のVM（`VM.Standard.E2.1.Micro`、1GBメモリ）を作成
- [x] VMにJava 17・PostgreSQLを導入し、Ktor APIをビルドして配置
- [x] `users`・`threads`・`comments`のテーブルを作成し、JDBCから接続
- [x] `systemd`でKtorをサービス化し、自動起動を有効化
- [x] Caddyをリバースプロキシとして導入し、HTTPSで公開
- [x] HTTPS経由の`/health`応答、コメントAPIの取得・投稿、PostgreSQLへの保存、Androidでの表示を確認

クラウド上の構成は動作検証用です。GETの一部を公開し、コメント投稿のPOSTはCaddyで指定した接続元IPに制限しています。コメント投稿のユーザー単位の認可などは今後整備する方針です。

### 紹介サイト・CI

- [x] 紹介ホームページの公開と、実際の画面9枚・簡単な実装解説の掲載
- [x] 紹介文の編集、知識ノートの作成・編集・保存・公開／下書き切り替え
- [x] GitHub ActionsでKtorのヘルスチェック・コメントAPI・登録時セッション発行・認証ルートのテストを自動実行

紹介サイトの編集・保存機能はWebサイト側の機能で、Androidアプリの機能とは別です。

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

### 4. 登録結果を画面の状態に置き換える

AndroidからRetrofitで登録情報を送り、Ktor側で入力を検証してPostgreSQLに保存します。パスワードはArgon2idでハッシュ化し、元の文字列のまま保存しません。

RepositoryでHTTPの応答を「登録成功」「入力不備」「メール重複」に変換し、ViewModelが読み込み中・成功・エラーの画面状態を管理します。Composeはその状態に応じて、入力欄・エラー・登録完了表示を切り替えます。

関連コード：[RegisterViewModel.kt](app/src/main/java/com/example/soccerapp/RegisterViewModel.kt) ／ [NetworkAuthRepository.kt](app/src/main/java/com/example/soccerapp/data/repository/NetworkAuthRepository.kt) ／ [AuthRoutes.kt](server/src/main/kotlin/com/example/soccerapp/server/AuthRoutes.kt) ／ [JdbcAuthRepository.kt](server/src/main/kotlin/com/example/soccerapp/server/JdbcAuthRepository.kt)

### 5. ログイン状態を端末に保存し、サーバーで検証する

**課題**：アカウントを登録しても毎回本人確認をやり直すのは不便です。一方で、端末に保存した情報だけを根拠に「ログイン中」と判断すると、期限切れ・無効化されたセッションを見逃します。

**実装**：Ktorで発行したランダムなアクセストークンをAndroid KeystoreのAES-GCM鍵で暗号化し、DataStore（`auth_session`）に暗号文を保存します。起動時は復号したトークンをBearer認証で`GET /auth/me`に送信し、サーバー側で有効性を検証します。

```text
新規登録 → Ktor POST /auth/register → ユーザー作成 + セッション発行
         → Android TokenCipher（AES-GCM暗号化）→ TokenRepository → DataStore

アプリ起動 → DataStore → TokenRepository → TokenCipher（復号）
           → Retrofit GET /auth/me → Ktor（トークンのSHA-256ハッシュを照合）
           ├─ 200 → LoggedIn（ホームに「ログイン中」を表示）
           ├─ 401 → トークン破棄 → LoggedOut
           └─ 通信失敗など → Unavailable（再試行可能）
```

トークンはJWTではなく不透明なランダム文字列です。PostgreSQLにはトークンのSHA-256ハッシュとユーザーID・作成日時・有効期限を保持します。Androidでは暗号文と復号鍵を別の保存場所に分離し、バックアップ／端末移行でKeystore鍵だけが失われる事態に備えてセッションDataStoreをバックアップ対象から除外しています。

**設計上の境界**：`TokenRepository`は保存・読み出し、`TokenCipher`は暗号化・復号、`NetworkAuthRepository`はAPI応答の解釈、`StartupViewModel`は`SessionUiState`の更新、Composeは状態の表示を担当します。トークン取得と画面の認証済み表示を分け、実際の有効性判断はKtorに任せています。

関連コード：[TokenCipher.kt](app/src/main/java/com/example/soccerapp/data/local/TokenCipher.kt) ／ [TokenRepository.kt](app/src/main/java/com/example/soccerapp/data/repository/TokenRepository.kt) ／ [SessionService.kt](server/src/main/kotlin/com/example/soccerapp/server/SessionService.kt) ／ [SessionRoutes.kt](server/src/main/kotlin/com/example/soccerapp/server/SessionRoutes.kt) ／ [StartupViewModel.kt](app/src/main/java/com/example/soccerapp/StartupViewModel.kt)

**動作確認**：AndroidのDebug APKビルドが成功。ローカルPostgreSQLとKtorの登録・トークン発行・本人確認APIは実リクエストで確認済みです。

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
       ├─ DataStore（初回案内の設定・暗号化済みセッショントークン）
       ├─ Android Keystore（暗号化鍵）
       └─ Retrofit ──→ Ktor API（ローカル／OCI）
                          ↓
             CommentRepository / JdbcAuthRepository / JdbcSessionService
                          ↓ JDBC
                       PostgreSQL
```

OCI上で検証したコメント通信の構成：

```text
Android（SoccerApp）
    ↓ Retrofit / HTTPS
OCI Compute VM（Ubuntu 24.04）
    ↓ Caddy（HTTPS終端・リバースプロキシ）
    ↓ Ktor API（systemd管理、8080番で待機）
    ↓ JDBC
PostgreSQL（同一VM内）
```

試合情報は引き続きAndroidからfootball-data.orgへ直接アクセスします。上記のOCI構成はコメントAPIなど自作バックエンドの通信経路です。

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
| ローカル保存 | Room、SQLite、DataStore、Android Keystore（AES-GCM） |
| 依存性注入 | Hilt |
| バックエンド | Ktor Server、JDBC、PostgreSQL |
| クラウド・OS | Oracle Cloud Infrastructure（OCI Compute VM）、Ubuntu 24.04 |
| HTTPS・リバースプロキシ | Caddy、TLS証明書、HTTPS |
| サービス管理 | systemd（Ktorの自動起動・再起動設定） |
| パスワード保存 | Argon2id |
| CI | GitHub Actions（Ktorの一部テストを自動実行） |
| ビルド・コード生成 | Gradle Kotlin DSL、KSP、kapt |
| テスト | JUnit、kotlinx-coroutines-test、Ktor Server Test、AndroidX Compose UI Test |

## Oracle Cloudへのデプロイ

2026年10月18日、OCIのCompute VM（Ubuntu 24.04、`VM.Standard.E2.1.Micro`）上でKtor APIとPostgreSQLを稼働させました。ローカルPCとは別のサーバーでデータを保持し、AndroidからHTTPSでアクセスできることを確認しています。

```text
Android → HTTPS → Caddy（443番） → Ktor（8080番） → PostgreSQL
```

- **サーバー構築**：VMへSSHで接続し、Java 17・PostgreSQLを導入。GitHubのソースを取得してKtorの配布用ファイルをビルド・配置しました。
- **データ保存**：PostgreSQLに`users`・`threads`・`comments`のテーブルを作成。Androidから投稿し、DBに保存されたコメントを確認しました。
- **HTTPS**：Caddyを導入し、公開ホスト名でTLS証明書を取得。`GET /health`で`200 OK`、コメント取得で`200 OK`、コメント投稿と画面表示も確認しました。
- **起動管理**：Ktorを`systemd`サービスに登録し、`enabled`・`active (running)`とヘルスチェック応答を確認しました。
- **アクセス制御**：外部公開はCaddyの経路・HTTPメソッド・接続元IPで限定。OCI側もHTTP/HTTPSの受信ルールを設定し、PostgreSQLを直接外部公開する構成にはしていません。

**現時点の範囲**：手動デプロイした検証環境です。GitHub Actionsからの自動デプロイ（CD）、Dockerでの本番起動、データバックアップ・復元手順、監視・障害対応などは今後整備します。IP制限はアカウント認証の代わりにはなりません。

## セットアップと動作環境

ローカル開発用の手順と、OCI上で動作確認した構成を区別して記載します。署名済みAPK（v0.1.0）はGitHub Releasesでテスト版として公開しています。

### Android側

1. Android Studioでこのリポジトリを開き、Android SDKとGradleの同期を準備します。
2. [football-data.org](https://www.football-data.org/) のAPIトークンを取得します。
3. プロジェクト直下の`local.properties`に、Android SDKの設定に加えて以下を設定します。

```properties
FOOTBALL_DATA_API_TOKEN=your_api_token
```

4. Androidエミュレーターで`app`を実行します。

`local.properties`はGit管理対象外です。APIトークンをリポジトリにコミットしないでください。なお、現状の実装はトークンをBuildConfig経由でアプリに含めるため、**APK内で秘匿できる構成ではありません**。外部配布に向けて認証情報の扱いを再検討します。

### コメント・アカウント登録用バックエンド

ローカル開発ではKtorサーバーをローカル環境のPostgreSQLに接続して起動します。OCI環境では同じKtor APIをVM内のPostgreSQLへ接続して動作確認しました。

- DB接続先：環境変数`SOCCER_DB_URL`（未指定時：`jdbc:postgresql://localhost:5432/soccer_app`）
- DBユーザー：`soccer_app_user`（現状の実装で固定）
- DBパスワード：環境変数`SOCCER_DB_PASSWORD`（必須）
- APIポート：`8080`

データベースと既存の`users`・`threads`・`comments`テーブルは事前に準備してください。**すべてのテーブルの自動初期化は未整備**のため、クローンだけではDBを含む全機能が起動する状態ではありません。今回追加したセッション用テーブルのSQLは[`server/sql/001_sessions.sql`](server/sql/001_sessions.sql)にあります。既存の`users`テーブルを前提として、開発用DBに適用します。

```bash
psql -h localhost -U soccer_app_user -d soccer_app -v ON_ERROR_STOP=1 -f server/sql/001_sessions.sql
```

テーブル作成時に`users`への外部キー参照権限が不足している場合は、DB管理者が`soccer_app_user`に`users(id)`の`REFERENCES`権限を付与します。ローカル環境ではテーブル作成を確認済みですが、**OCI側のDBへの適用はまだ行っていません**。

設定後、プロジェクト直下からKtorサーバーを起動します。

```text
./gradlew :server:run
```

Windowsでは`gradlew.bat :server:run`を使用します。起動後は`http://localhost:8080/health`でヘルスチェックできます。

**GitHubにあるAndroid側のコメントAPI接続先**は、現時点では`http://10.0.2.2:8080/`に固定されています。これはAndroidエミュレーターからローカルPCへ接続する開発用URLです。OCI接続の検証時にはAndroid側の`CommentNetworkModule.kt`の`baseUrl`をHTTPSのデプロイ先へ切り替えて動作確認しましたが、この接続先変更はまだリポジトリに反映していません。配布前には開発／検証／本番の接続先を切り替えられる構成へ整理します。

## テスト

以下のテストコードを用意しています。CIで実行している範囲と確認済みの結果は、次の「CI（自動テスト）」に記載しています。

| 対象 | 確認する内容 |
| --- | --- |
| Ktor ApplicationTest | `/health`の応答 |
| MatchDetailViewModel | 試合取得の成功／未検出／失敗 |
| MatchThreadViewModel | コメント取得・投稿時の画面状態 |
| CommentPostSection | 入力表示、入力可否、投稿操作、エラー表示 |
| Ktor CommentRoutes | 投稿・取得・不正な入力に対するAPI応答 |
| Ktor SessionRoutesTest | トークンあり／なしの本人確認、ログイン・ログアウトのルート応答 |
| Ktor RegistrationSessionTest | アカウント登録成功時のセッション発行 |
| JdbcCommentRepository | 1,000件から次スレッドへの切り替えとDB内の状態 |

Android側のユニットテスト例：`./gradlew :app:testDebugUnitTest`

Ktor側のテスト例：`./gradlew :server:test`

**今回の手動確認（2026年10月21日）**：Windows上でPostgreSQL 18に`user_sessions`を作成し、Ktor起動とDB接続、`GET /health`の200、トークンなし`GET /auth/me`の401、新規登録時のトークン発行、発行トークンによる`GET /auth/me`のユーザーID一致を確認しました。Androidは`./gradlew :app:assembleDebug`が成功しました。

Compose UIの計測テストにはエミュレーターまたは実機が必要です。DB統合テストは専用DB `soccer_app_test`を確認してから書き込みを行う構成です。接続先の設定なしにDB統合テストを実行しないでください。

## CI（自動テスト）

[![Ktor CI](https://github.com/taka4949/SoccerApp/actions/workflows/server-ci.yml/badge.svg)](https://github.com/taka4949/SoccerApp/actions/workflows/server-ci.yml)

GitHub Actionsの[Ktor CI](.github/workflows/server-ci.yml)を導入しています。コードを更新した際に、サーバーの基本的な動作が壊れていないかを自動で確認します。

- **実行タイミング**：`main`へのpush、`main`向けのプルリクエスト
- **環境**：Ubuntu、Java 17（Temurin）、Gradle
- **対象**：`ApplicationTest`（ヘルスチェック）、`CommentRoutesTest`（コメント投稿・取得など）、`SessionRoutesTest`（セッションAPI）、`RegistrationSessionTest`（登録時のトークン発行）
- **確認結果**：2026年10月21日時点で、認証機能を統合した[main上の実行](https://github.com/taka4949/SoccerApp/actions/runs/39465248301)が成功

現時点のCIは、実際のPostgreSQLに接続しないKtorルートテストが対象です。登録時のセッション発行はテストダブルを用いて検証しますが、実DBを使う認証の統合テスト、Android側のテスト・APKビルド・配布・自動デプロイは含みません。OCIへのデプロイも手動で実施しており、CI成功時にクラウドへ反映される仕組みはまだありません。

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
- [x] アカウント登録API・Argon2idによるパスワード保存・登録完了UI
- [x] Ktorのログイン／本人確認／ログアウトAPI、ハッシュ化したセッショントークンのDB管理
- [x] Androidのトークン暗号化・DataStore保存・起動時セッション確認・認証状態UIを実装しビルド成功
- [x] ローカルPostgreSQLでセッションテーブルを作成し、登録・トークン発行・本人確認をAPI経由で確認
- [x] Ktor認証API（PR #16）とAndroid認証保存処理（PR #17）をGitHubの`main`に統合
- [x] 起動画面・ホーム・各画面のダークUI整備
- [x] スコアの手動更新
- [x] 紹介ホームページ公開・実際のスクリーンショット掲載・知識ノート
- [x] GitHub ActionsによるKtorの一部テストの自動実行
- [x] OCI上にKtor・PostgreSQLをデプロイし、CaddyのHTTPS経由でAndroidからコメント投稿・取得を確認
- [x] `systemd`でKtorを自動起動する設定を導入
- [x] 署名付きRelease APK（v0.1.0）を作成し、Pixel 7エミュレータで起動を確認してGitHub ReleasesでPre-releaseとして公開

### 次に取り組む機能（予定）

1. **認証機能の拡張・投稿認可**：実装済みのKtorログインAPIとAndroidの保存済みセッション確認に続き、Androidのログイン／ログアウト画面を追加する。コメント投稿は未認証ユーザーを閲覧のみに制限し、登録ユーザーのトークンをサーバー側で検証して許可する。メール認証は後段。
2. **交流機能の拡張**：いいね、画像投稿、自分のコメント履歴、コメントのコピー・検索など。コメントへの返信・引用、返信先への移動も追加する。
3. **サッカー情報の拡張**：ニュース一覧、記事ごとのコメント機能。
4. **UI/UX改善の継続**：整備した起動画面・ダークUIを基に、ナビゲーション、アイコン、操作性を改善する。
5. **クラウド運用の整備**：導入済みのOCI環境をベースに、バックアップ・復元、ログ・監視、設定・シークレット管理、デプロイ手順を整える。Dockerは環境再現性を高める手段として必要に応じて導入する。
6. **リアルタイム性の強化**：WebSocketによるコメント更新、再接続・ライフサイクル制御。
7. **CI拡張とAPK更新**：署名済みAPK（v0.1.0）はGitHub Releasesで公開済み。今後は実DBの認証統合テスト・Android側の検証を拡充する。
8. **公開済みホームページの更新**：開発に合わせて画面・実装解説を更新し、知識ノートに学習内容を蓄積する。公開済みAPKのダウンロード先や利用手順も案内する。
9. **正式リリース**：アプリの動作検証、公開バックエンドの整備、認証・セキュリティ対応、操作性の改善などを進め、将来的にはGoogle Playでの公開を目指す。
10. **通報・ミュートと投稿管理**：コメントの通報、利用者によるミュート、管理者による投稿非表示を追加し、公開後の荒らしへの対応手段を用意する。
11. **スコア予想・ポイントランキング**：ログインしたユーザーが試合前にスコアを予想し、結果に応じてポイントを獲得する。リーグ・シーズンごとの合計ポイントでランキングを表示する。

### クラウド選定方針（OCIとAWS）

現在のSoccerAppにとって、**AWSへの移行・追加導入は必須ではありません**。OCIでAndroid → HTTPS → Ktor → PostgreSQLの接続を確認できており、同じ構成をAWSで再構築するより、認証・運用・テストとアプリ機能の完成を優先します。AWS固有サービスを使う要件や、運用規模・コスト・可用性などの理由が生じたときに比較検討します。AWSの利用経験を別途得ることは選択肢ですが、現段階の必須タスクにはしません。

#### スコア予想・ランキングの仮仕様（未実装）

- 1人につき1試合1件の予想を登録でき、試合開始前なら変更可能。締切はサーバー側で判定する。
- スコア完全一致は3ポイント、勝ち・引き分け・負けのみ一致は1ポイント、不一致は0ポイント。完全一致に勝敗分を重ねて加算しない。
- リーグ・シーズンごとにポイントを集計し、同点は同順位とする。
- 最初は1リーグで、予想入力・保存、締切、採点、ランキングまでを実装してから対象を広げる。
- 採点を再実行してもポイントが二重加算されないようにし、延期・中止は採点保留、結果訂正時は再採点できる構成を目指す。
- ランキング用の公開表示名と掲示板のスレッド別匿名IDを分け、ランキングから他スレッドの投稿履歴をたどれない形にする。

上記は開発の方向性を示したもので、着手順や公開時期を固定するものではありません。**署名済みAPK（v0.1.0）の作成・Pixel 7エミュレータでの起動確認・GitHub Releasesでの公開**を行いました。今後は**公開済みの紹介ホームページを更新**しながら、アプリの品質と運用環境を整え、**Google Playでの正式リリース**も目標としています。

既存のコメントAPIはOCI上にHTTPSで配置し、Androidからの通信を検証済みです。GitHubのAndroidコードはローカル開発用接続先を使用しています。外部配布に向けて、投稿認可、APIトークンの取り扱い、接続先設定、バックアップ・運用上の安全性などを整えていきます。

### 追加構想：試合予想・交流表現の拡張（検討段階）

独立した「手紙アプリ」をもう一つ作るのではなく、SoccerAppの試合別交流を深める方向で検討しています。**以下は未実装の構想です。**

- **試合予想タイムカプセル**：試合前に予想やメッセージを投稿し、締切後または指定時刻に内容を公開する機能。まずは投稿・締切・公開という小さな単位で検証。
- **マッチレター**：タイムカプセルを発展させ、メッセージのランダム配達や後日の返信を取り入れる案。
- **戦術ボード**：選手配置や試合展開のアイデアを視覚的に共有する機能。

最初から全機能を同時に実装するのではなく、基本的な実装を確認してから必要な要素を段階的に追加します。
