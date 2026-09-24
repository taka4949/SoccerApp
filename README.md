# SoccerApp

試合情報を確認しながら、試合ごとのスレッドでユーザー同士がコメントできる掲示板アプリを目指して開発しているAndroidアプリです。

football-data.orgから取得した大会・試合データの表示、Roomへの保存、通信失敗時のキャッシュ表示に加え、Ktor・PostgreSQLを利用したコメント掲示板機能を実装しています。

現在は、AndroidからコメントAPIへ接続し、試合ごとのコメント一覧表示・コメント投稿までCompose UIから操作できる状態です。

## 現在できること

### 大会・試合情報を表示する

football-data.orgから大会一覧と試合情報を取得し、Jetpack Composeで表示します。

大会やチームのロゴ画像もAPIから取得し、Coilを利用して表示しています。

試合一覧では、試合日時・ホームチーム・アウェイチームを確認でき、試合を選択すると試合詳細画面へ移動できます。

```text
大会一覧
   ↓
試合一覧
   ↓
試合詳細・コメント掲示板
```

### APIから取得した試合を端末へ保存する

取得した試合情報はRoomにも保存します。

同じ試合が保存されている場合は`@Upsert`によって更新し、API通信に失敗した場合はRoomに保存されているデータを表示します。

チームロゴのURLもRoomへ保存しており、データベース構造の変更にはMigrationを使用しています。

```text
通信成功

football-data.org
        ↓
     Retrofit
        ↓
      Room
        ↓
   Compose UI


通信失敗

      Room
        ↓
   Compose UI
```

### コメントを取得・投稿する

Ktorで試合ごとのコメントを扱うAPIを実装しています。

* `GET /matches/{matchId}/comments`
* `POST /matches/{matchId}/comments`

Android側ではRetrofitを利用してAPIへ接続し、Repository・ViewModelを経由してCompose UIへ状態を反映しています。

試合詳細画面では、

* コメント一覧表示
* 投稿者名入力
* コメント入力
* コメント投稿
* 読み込み中表示
* 読み込み失敗時の再試行
* 投稿中の二重投稿防止

を実装しています。

コメント投稿後は、新しく作成されたコメントを既存のコメント一覧へ追加して画面へ反映します。

### コメントをPostgreSQLへ保存する

コメントはKtorからJDBCを利用してPostgreSQLへ保存します。

Androidからは`matchId`、投稿者名、コメント本文を送り、サーバー側でコメントIDと作成日時を生成します。

コメント投稿時には対象試合のOPENスレッドを検索し、スレッドが存在しない場合は新しいスレッドを作成してからコメントを保存します。

トランザクションを使用し、スレッド作成後にコメント保存へ失敗した場合は変更をロールバックします。

### 画面の状態を管理する

ViewModelとStateFlowを使用し、画面状態を管理しています。

試合情報では、

* `Loading`
* `Success`
* `Error`

コメント画面では、それに加えてコメント投稿中・投稿失敗の状態も管理しています。

Compose側では`collectAsStateWithLifecycle()`を使用し、Lifecycleを考慮しながらStateFlowを監視しています。

### データの役割を分離する

API・Room・UIで同じデータクラスを直接使い回さず、役割ごとに分けています。

```text
DTO
 ↓
Repository
 ↓
Domain Model
 ↓
ViewModel
 ↓
Compose UI
```

Roomを利用するデータでは、

```text
DTO
 ↓
Entity
 ↓
Room
 ↓
Entity
 ↓
Domain Model
```

という変換をRepositoryで行います。

### Hiltで依存関係を管理する

Hiltを利用して、

* Repository
* Retrofit
* API Service
* Room Database
* DAO

などの依存関係を管理しています。

football-data.org用Retrofitと掲示板API用RetrofitはQualifierを使用して区別しています。

## アプリの構成

```text
Compose UI
│
├─ MainViewModel
│  └─ SoccerRepository
│     └─ MatchRepository
│        ├─ SoccerApiService
│        │   └─ Retrofit
│        │       └─ football-data.org
│        │
│        └─ MatchDao
│            └─ Room
│                └─ SQLite
│
└─ MatchThreadViewModel
   └─ CommentRepository
      └─ CommentApiService
         └─ Retrofit
            └─ Ktor API
                └─ JdbcCommentRepository
                    └─ PostgreSQL
```

## 使用技術

| 分類     | 使用技術                                     |
| ------ | ---------------------------------------- |
| 言語     | Kotlin                                   |
| UI     | Jetpack Compose / Material 3             |
| 画像表示   | Coil                                     |
| 設計     | MVVM / Repository Pattern / UDF          |
| 状態管理   | ViewModel / StateFlow / UiState          |
| 非同期処理  | Kotlin Coroutines                        |
| 画面遷移   | Navigation Compose                       |
| API通信  | Retrofit / OkHttp / Ktor Server          |
| JSON変換 | kotlinx.serialization                    |
| ローカル保存 | Room / SQLite                            |
| データベース | PostgreSQL / JDBC                        |
| 依存性注入  | Hilt                                     |
| コード生成  | KSP                                      |
| テスト    | JUnit / Ktor Server Test / AndroidX Test |

## 今後の実装予定

* [x] 大会一覧の表示
* [x] 試合一覧の表示
* [x] 試合詳細画面
* [x] football-data.orgとのAPI連携
* [x] Roomへの試合情報保存
* [x] 通信失敗時のキャッシュ表示
* [x] 大会・チームロゴの表示
* [x] Ktorによる掲示板API
* [x] PostgreSQLへのコメント保存・取得
* [x] Androidから掲示板APIへの通信
* [x] コメント画面のViewModel・UiState
* [x] コメント一覧のCompose UI表示
* [x] コメント投稿機能
* [x] OPENスレッドが存在しない場合の自動作成
* [x] Ktor APIの基本テスト
* [ ] ViewModel・Repository・Roomのテスト
* [ ] Compose UIテスト
* [ ] ユーザー登録・ログイン機能
* [ ] 認証情報とコメント投稿者の紐付け
* [ ] 新しいコメントスレッドを明示的に作成する機能
* [ ] WebSocketによるコメントのリアルタイム更新
* [ ] DockerによるKtor・PostgreSQLの開発環境構築
* [ ] GitHub ActionsによるCI
* [ ] デプロイを含めたCDの検討
* [ ] UI・アプリアイコンの改善
* [ ] Google Playへの公開

過去スレッドの閲覧・保存履歴などは、必要性を確認しながら追加を検討します。

Firebaseは必須とはせず、プッシュ通知やクラッシュ情報の収集など、必要な機能が明確になった段階で導入を検討します。

