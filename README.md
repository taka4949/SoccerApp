# SoccerApp

試合情報を確認しながら、試合ごとのスレッドでユーザー同士がコメントできる掲示板アプリを目指して開発しているAndroidアプリです。

現在は、football-data.orgから実際の大会・試合データを取得し、画面への表示とRoomへの保存まで実装しています。掲示板機能は未実装です。

## 現在できること

### 大会一覧をAPIから取得する

アプリを起動すると、football-data.orgから大会一覧を取得して表示します。

ダミーデータではなく、Retrofitを使って実際のAPIへアクセスしています。受け取ったJSONはkotlinx.serializationでKotlinのデータへ変換しています。

### 大会ごとの試合を表示する

大会を選択すると、その大会の開催予定試合をAPIから取得して一覧表示します。

各試合では、以下の情報を確認できます。

* ホームチーム
* アウェイチーム
* スコア
* 試合日時
* 試合ステータス

### 試合詳細画面を表示する

試合一覧から試合を選択すると、試合詳細画面へ移動します。

Navigation Composeを使用し、次の順番で画面を移動できるようにしています。

```text
大会一覧
   ↓
試合一覧
   ↓
試合詳細
```

### APIから取得した試合を端末へ保存する

APIから取得した試合情報は、画面へ表示するだけでなくRoomにも保存します。

同じ試合がすでに保存されている場合は、`@Upsert`によって既存のデータを更新します。

```text
APIから試合を取得
   ↓
Roomへ保存・更新
   ↓
画面へ表示
```

### 通信できない場合は保存済みの試合を表示する

試合情報の取得時に通信エラーが発生した場合は、Roomに保存されている同じ大会の試合を取得して表示します。

オンラインで一度取得した大会であれば、通信を切った状態でも試合一覧を確認できます。

```text
通信成功
API → Roomへ保存 → 画面へ表示

通信失敗
Roomから取得 → 画面へ表示
```

### 画面の状態を管理する

画面の状態はViewModelとStateFlowで管理しています。

* `Loading`：データ取得中
* `Success`：データ取得成功
* `Error`：データ取得失敗

Compose側では`collectAsStateWithLifecycle()`を使用して状態を監視し、状態が変わると画面へ反映されます。

エラー画面からは、データ取得を再実行できます。

### データの役割を分ける

API、Room、画面で同じデータクラスを使い回さず、それぞれの役割に合わせて分けています。

* DTO：APIから受け取るデータ
* Entity：Roomへ保存するデータ
* Domain Model：ViewModelや画面で使用するデータ

RepositoryがDTOやEntityをDomain Modelへ変換し、ViewModelには画面で必要な形式のデータだけを返します。

### Hiltで依存関係を管理する

Hiltを使用して、Repository、Retrofit、API Service、Room Database、DAOを必要なクラスへ渡しています。

ViewModelは`MatchRepository`を直接指定せず、`SoccerRepository`を通してデータを取得する構成にしています。

### 掲示板機能の土台

試合とコメントを関連付けるデータモデルと、試合ごとのスレッド画面の土台を作成しています。

コメントの取得・投稿とバックエンドとの通信は今後実装します。

## アプリの構成

```text
Compose UI
    ↓
MainViewModel
    ↓
SoccerRepository
    ↓
MatchRepository
    ├─ SoccerApiService → Retrofit → football-data.org
    └─ MatchDao → Room → SQLite
```

`MatchRepository`がAPIとRoomを使い分け、最終的に画面で使用する`Match`へ変換してViewModelへ返します。

## 使用技術

| 分類     | 使用技術                                    |
| ------ | --------------------------------------- |
| 言語     | Kotlin                                  |
| UI     | Jetpack Compose / Material 3            |
| 設計     | MVVM / Repository Pattern / UDF         |
| 状態管理   | ViewModel / StateFlow / UiState         |
| 非同期処理  | Kotlin Coroutines                       |
| 画面遷移   | Navigation Compose                      |
| API通信  | Retrofit / OkHttp                       |
| JSON変換 | kotlinx.serialization                   |
| ローカル保存 | Room / SQLite                           |
| 依存性注入  | Hilt                                    |
| コード生成  | KSP                                     |
| テスト    | JUnit / AndroidX Test / Compose UI Test |

## 今後の実装予定

* [x] 大会一覧の表示
* [x] 試合一覧の表示
* [x] 試合詳細画面
* [x] football-data.orgとのAPI連携
* [x] Roomへの試合情報保存
* [x] 通信失敗時のキャッシュ表示
* [ ] キャッシュの最終更新時刻を保存
* [ ] 試合ごとのコメント一覧
* [ ] コメント投稿機能
* [ ] Ktorによる掲示板API
* [ ] PostgreSQLによるユーザー・コメント管理
* [ ] ユーザー認証
* [ ] ViewModel・Repository・Roomのテスト
* [ ] Compose UIテスト
* [ ] Ktor APIテスト
* [ ] Dockerによる開発環境の構築
* [ ] GitHub Actionsによるビルド・テストの自動化
* [ ] UIとアプリアイコンの改善
* [ ] Google Playへの公開

Firebaseは必須とせず、プッシュ通知やクラッシュ情報の収集が必要になった場合に導入を検討します。

