package com.meta.pixelandtexel.scanner.di

import com.meta.pixelandtexel.scanner.android.datasource.repository.SmartHomeRepositoryImpl
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideSmartHomeRepository(impl: SmartHomeRepositoryImpl): SmartHomeRepository{
        return impl
    }
}