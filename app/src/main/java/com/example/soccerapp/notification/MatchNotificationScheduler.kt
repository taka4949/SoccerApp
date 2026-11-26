
package com.example.soccerapp.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.soccerapp.data.local.entity.MatchEntity
import com.example.soccerapp.worker.MatchNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MatchNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedule(match: MatchEntity) {
        val formatter = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.US
        ).apply {
            timeZone = TimeZone.getTimeZone("UTC")
            isLenient = false
        }




        val kickoffMillis = try {
            formatter.parse(match.utcDate)?.time ?: return
        } catch (e: java.text.ParseException) {
            return
        }

        val reminders = listOf(
            "24h" to 24L,
            "1h" to 1L
        )

        reminders.forEach { (type, hoursBefore) ->
            val workName = workName(match.id, type)

            val delayMillis = kickoffMillis -
                    TimeUnit.HOURS.toMillis(hoursBefore) -
                    System.currentTimeMillis()

            if (delayMillis <= 0L) {
                workManager.cancelUniqueWork(workName)
                return@forEach
            }

            val request =
                OneTimeWorkRequestBuilder<MatchNotificationWorker>()
                    .setInitialDelay(
                        delayMillis,
                        TimeUnit.MILLISECONDS
                    )
                    .setInputData(
                        workDataOf(
                            "matchId" to match.id,
                            "reminderType" to type
                        )
                    )
                    .build()

            workManager.enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }







    fun cancel(matchId: Int) {
        workManager.cancelUniqueWork(workName(matchId, "24h"))
        workManager.cancelUniqueWork(workName(matchId, "1h"))
    }

    private fun workName(matchId: Int, type: String): String {
        return "favorite_match_${matchId}_$type"
    }
}
