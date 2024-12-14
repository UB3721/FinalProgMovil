package com.df.base.data

import android.content.Context
import com.df.base.network.BackApi

interface AppContainer {
    val mangasRepository: MangasRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val mangasRepository: MangasRepository by lazy {
        OfflineMangasRepository(BackApi.retrofitService)
    }
}