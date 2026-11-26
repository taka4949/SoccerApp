
package com.example.soccerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.soccerapp.data.local.entity.FavoriteMatchEntity
import kotlinx.coroutines.flow.Flow
import com.example.soccerapp.data.local.entity.MatchEntity



@Dao
interface FavoriteMatchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteMatchEntity)



    @Query("DELETE FROM favorite_matches WHERE matchId = :matchId")
    suspend fun removeFavorite(matchId: Int)

    @Query("SELECT matchId FROM favorite_matches")
    fun observeFavoriteMatchIds(): Flow<List<Int>>


    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM favorite_matches
        WHERE matchId = :matchId
    )
""")
    suspend fun isFavorite(matchId: Int): Boolean



    @Query("""
    SELECT matches.*
    FROM matches
    INNER JOIN favorite_matches
        ON matches.id = favorite_matches.matchId
    WHERE matches.utcDate >= :nowUtc
      AND matches.status IN ('SCHEDULED', 'TIMED')
    ORDER BY matches.utcDate ASC
    LIMIT 1
""")
    suspend fun getNextFavoriteMatch(
        nowUtc: String
    ): MatchEntity?

}
