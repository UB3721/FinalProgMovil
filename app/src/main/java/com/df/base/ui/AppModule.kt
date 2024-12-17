package com.df.base.ui

import com.df.base.data.MangasRepository
import com.df.base.data.OfflineMangasRepository
import com.df.base.network.BackApi
import com.df.base.network.BackApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): BackApiService {
        return BackApi.retrofitService
    }

    @Provides
    @Singleton
    fun provideMangasRepository(apiService: BackApiService): MangasRepository {
        return OfflineMangasRepository(apiService)
    }
}
