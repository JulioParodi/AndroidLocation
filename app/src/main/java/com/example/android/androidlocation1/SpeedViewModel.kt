package com.example.android.androidlocation1

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

class SpeedViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    val speedDao: SpeedDao = AppDatabase.getAppDataBase(application).speedDao()

    val allSpeeds: LiveData<List<Speed>> = speedDao.getSpeeds()

    fun insert(speed: Speed) = scope.launch(Dispatchers.IO) {
        speedDao.insertSpeed(speed)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}