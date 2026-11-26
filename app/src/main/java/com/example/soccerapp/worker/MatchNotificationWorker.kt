
package com.example.soccerapp.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.soccerapp.data.local.dao.FavoriteMatchDao
import com.example.soccerapp.data.local.dao.MatchDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MatchNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val favoriteMatchDao: FavoriteMatchDao,
    private val matchDao: MatchDao
) : CoroutineWorker(appContext, workerParams) {





    override suspend fun doWork(): Result {
        val matchId = inputData.getInt("matchId", -1)
        val reminderType = inputData.getString("reminderType")
            ?: return Result.failure()

        if (matchId == -1 || reminderType !in setOf("24h", "1h")) {
            return Result.failure()
        }

        if (!favoriteMatchDao.isFavorite(matchId)) {
            return Result.success()
        }

        val match = matchDao.getMatchById(matchId)
            ?: return Result.success()

        if (match.status !in setOf("SCHEDULED", "TIMED")) {
            return Result.success()
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                return Result.success()
            }
        }




        val notification = NotificationCompat.Builder(
            applicationContext,
            "favorite_matches"
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("お気に入りの試合")
            .setContentText(
                "${match.homeTeam} vs ${match.awayTeam}"
            )
            .setAutoCancel(true)
            .build()

        val notificationId =
            "$matchId-$reminderType".hashCode()

        NotificationManagerCompat.from(applicationContext)
            .notify(notificationId, notification)

        return Result.success()
    }
}
