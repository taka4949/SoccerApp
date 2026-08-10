
SoccerApp

概要

UEFA Champions League、プレミアリーグ、Jリーグの試合情報を確認しながら、将来的に試合ごとのスレッドでユーザー同士がコメントできる掲示板アプリを目指して開発しているAndroidアプリのプロトタイプです。

現在はダミーデータを使用し、リーグ別の試合一覧表示と試合詳細画面への遷移を実装しています。

実装済みの機能

リーグ・試合一覧
  CL、プレミアリーグ、Jリーグの試合をリーグ別に表示します。

試合詳細画面
  試合を選択すると、対戦カード、スコア、試合時間を表示します。

画面遷移
  Navigation Composeを使用し、試合一覧から試合詳細画面へ遷移します。

UI状態管理
  ViewModelとStateFlowを使用し、Loading、Success、Errorの状態を管理しています。画面側では`collectAsStateWithLifecycle()`で状態を監視し、エラー発生時には再読み込みができます。

データ層の分離
  Repositoryパターンを使用し、ViewModelからデータ取得処理を分離しています。現在はダミーデータを返していますが、今後データの取得元をAPIへ変更できる構成にしています。

依存性注入
  Hiltを使用し、RepositoryをViewModelへ注入しています。

非同期処理
  Kotlin Coroutinesと`viewModelScope`を使用して、データ取得処理を非同期で実行しています。

コメント用データモデル
  掲示板機能の実装に向けて、試合とコメントを関連付けるデータモデルを定義しています。

 使用技術

　・Language: Kotlin
  ・UI: Jetpack Compose / Material 3
  ・Architecture:MVVM / Repository Pattern / UDF / Single Activity
  ・State Management: ViewModel / StateFlow / UiState
  ・Asynchronous Processing:Kotlin Coroutines
  ・Dependency Injection:Hilt
  ・Navigation:Navigation Compose

 今後の実装予定

1. 外部APIとの連携（完了）
   Retrofitを使用して実際の試合情報を取得し、アプリ内のデータモデルへ変換します。

2. ローカル保存
   Roomを使用し、取得した試合情報やコメントを端末内へ保存できるようにします。

3. 掲示板機能
   試合ごとのコメント表示と投稿機能を実装します。

4. Ktorによるバックエンド構築

5. Firebaseの導入
   Goバックエンドの構築後、必要な機能を整理した上でFirebaseとの連携を検討します。

6. Google Playへの公開
   UI、アプリアイコン、リソースを整備し、本番用ビルドと署名設定を行ってGoogle Playへ公開します。
