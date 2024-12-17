package com.df.base.data

import android.content.Context
import com.df.base.network.BackApi
import com.df.base.ui.login.LoginViewModel

interface AppContainer {
    val mangasRepository: MangasRepository
    val loginViewModel: LoginViewModel
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val mangasRepository: MangasRepository by lazy {
        OfflineMangasRepository(BackApi.retrofitService)
    }

    override val loginViewModel: LoginViewModel by lazy {
        LoginViewModel(mangasRepository)
    }
}