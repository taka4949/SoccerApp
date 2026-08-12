package com.example.soccerapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.soccerapp.data.local.dao.MatchDao
import com.example.soccerapp.data.local.entity.MatchEntity

@Database(
    entities = [MatchEntity::class],//このデータベースに含めるentityの登録。[]は複数所持可能という意味。
    version = 1,//roomに変更を知らせるため
    exportSchema = false//データベースのテーブル設計を、確認・テスト用のJSONファイルとして出力しない設定。詳しくは後ほど。
)
abstract class SoccerDatabase : RoomDatabase() {//soccer_databaseというSQLiteファイルとの接続管理

    abstract fun matchDao(): MatchDao//SQLiteへ保存する方法（ここには｛｝←処理内容がない）
}
//SoccerDatabase←roomdatabaseを継承している。これがsqliteとの接続やデータベースを開く、閉じる機能を継承。
//↓
//特定のSQLiteファイルへ接続している
//↓
//matchDao()
//↓
//同じSQLiteファイルへ接続したMatchDaoを返す

//roomdatebaseはクラス型！だからクラスにする！（今はこれでいい）