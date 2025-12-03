package com.meta.pixelandtexel.scanner

import android.app.Application
import com.meta.pixelandtexel.scanner.android.datasource.repository.SmartHomeRepositoryImpl
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetConnectionUsecase
import com.meta.pixelandtexel.scanner.di.AppContainer
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

val appModule = module {

    single<SmartHomeRepository>{
        SmartHomeRepositoryImpl()
    }

    factory { GetConnectionUsecase(get()) }
}

class DiApplication : Application() {
    val appContainer by lazy { AppContainer() }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DiApplication)
            modules(appModule)
        }
    }
}