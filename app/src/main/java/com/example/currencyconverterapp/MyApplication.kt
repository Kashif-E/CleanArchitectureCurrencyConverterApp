package com.example.currencyconverterapp

import android.app.Application
import androidx.appcompat.app.AlertDialog
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconverterapp.data.worker.FetchDataWorker
import com.example.currencyconverterapp.domain.utils.Constants.WORKER_TAG
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {



    init {
        instance = this
    }


    companion object {

        private var instance: MyApplication? = null
        fun getInstance(): MyApplication {
            synchronized(MyApplication::class.java) {
                if (instance == null)
                 instance =
                   MyApplication()

            }
            return instance!!
        }




    }
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        initWorkManager()
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(android.util.Log.DEBUG)
        .build()

    private fun initWorkManager() {
        scope.launch {
            val worker = PeriodicWorkRequestBuilder<FetchDataWorker>(
                30,
                TimeUnit.MINUTES
            ).addTag(WORKER_TAG).setInitialDelay(30, TimeUnit.MINUTES).build()

            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    WORKER_TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    worker
                )
        }
    }

}
