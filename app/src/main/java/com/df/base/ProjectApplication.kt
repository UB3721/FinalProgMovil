package com.df.base

import android.app.Application
import com.df.base.data.AppContainer
import com.df.base.data.AppDataContainer

class ProjectApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}