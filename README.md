SoccerApp (Soccer Bulletin Board Prototype)
概要 / Overview
海外サッカー（CL、プレミアリーグ）およびJリーグの試合経過をリアルタイムで確認しながら、ユーザー同士がコメントを投稿できる掲示板アプリのプロトタイプです。
This is a prototype soccer bulletin board application where users can discuss matches while viewing real-time simulated scores for CL, Premier League, and J-League.

主な機能 / Key Features
リーグ選択 / League Selection: CL、プレミア、Jリーグの3つのカテゴリから選択可能。
試合一覧 / Match List: 選択したリーグで開催中の試合を表示。
リアルタイム・シミュレーション / Real-time Simulation: LaunchedEffect を利用し、バックグラウンドでスコアと試合時間を自動更新。
掲示板機能 / Discussion Board: 試合ごとに専用スレッドがあり、匿名でリアルタイムにコメントを投稿可能。
画面遷移 / Navigation: リーグ選択 → 試合一覧 → スレッドという階層構造の実装。

使用技術 / Tech Stack
Language: Kotlin
UI Framework: Jetpack Compose (Modern Android UI)
Async Processing: Kotlin Coroutines (LaunchedEffect)
Architecture: Single Activity Architecture

今後の実装予定 / Roadmap
データ永続化 / Data Persistence: Room Databaseを使用したコメントの保存機能。
ネットワーク通信 / Networking: 実際のサッカーAPI（Retrofit等）との連携。
認証機能 / Authentication: Firebaseを使用したユーザーログイン機能。
