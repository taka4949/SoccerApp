package com.example.soccerapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.soccerapp.data.local.dao.MatchDao
import com.example.soccerapp.data.repository.SoccerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone







@HiltWorker
class MatchRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val matchDao: MatchDao,
    private val repository: SoccerRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val nowUtc = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.US
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())


            val nextMatch = matchDao.getNextMatch(nowUtc)
                ?: return Result.success()



            repository.refreshMatchById(nextMatch.id)

            Result.success()

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.retry()
        } catch (e: HttpException) {
            if (e.code() == 429 || e.code() >= 500) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
