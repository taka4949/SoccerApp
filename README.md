 SoccerApp 

   概要 

   海外サッカー（CL、プレミアリーグ）およびJリーグの試合経過を確認しながら、ユーザー同士がコメントを投稿できる掲示板アプリのプロトタイプです。
   
    主な機能 

   - リーグ選択 /  CL、プレミア、Jリーグの3つのカテゴリから選択可能.
   試合一覧 /  選択したリーグで開催中の試合を表示。
   - 状態管理の最適化 / MainViewModel を導入し、UIレイヤーからロジックを分離。単方向データフロー（UDF）と状態ホイスティングを適切に実装。
   - 掲示板機能 /  試合ごとに専用スレッドがあり、匿名でコメントを投稿可能。
   - 画面遷移 /  リーグ選択 → 試合一覧 → スレッドという階層構造の実装。

    使用技術 

   - Language: Kotlin
   - UI Framework: Jetpack Compose 
   - Architecture: ViewModel / State Hoisting / Single Activity Architecture

    今後の実装予定 

   - データリアルタイム同期 /  Firebase Cloud Firestore などを活用した、スコアや実況コメントの完全リアルタイム同期機能の実装。
   - データ永続化 /  Room Databaseを使用したコメントのローカル保存機能。
   - ネットワーク通信 / 実際のサッカーAPI（Retrofit等）との連携。
   - 認証機能 / Authentication: Firebaseを使用したユーザーログイン機能。