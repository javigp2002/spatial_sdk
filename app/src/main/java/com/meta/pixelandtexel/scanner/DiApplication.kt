package com.meta.pixelandtexel.scanner

import android.app.Application
import com.meta.pixelandtexel.scanner.android.datasource.networkModule
import com.meta.pixelandtexel.scanner.android.datasource.repository.SmartHomeRepositoryImpl
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetConnectionUsecase
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.IObjectDetectorHelper
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.MLKitObjectDetector
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.ObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.DisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

val appModule = module {
    single<IDisplayedEntityRepository>{ DisplayedEntityRepository() }
    single<IObjectDetectorHelper>{ MLKitObjectDetector()}
    single { ObjectDetectionRepository(get(), get()) }

    single<SmartHomeRepository>{
        SmartHomeRepositoryImpl(get())
    }

    factory { GetConnectionUsecase(get())}
    factory { ToggleSmartPlugUsecase(get())}
    factory { GetSmartPlugInfoUsecase(get())}

}

class DiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DiApplication)
            modules(networkModule, appModule)
        }
    }
}