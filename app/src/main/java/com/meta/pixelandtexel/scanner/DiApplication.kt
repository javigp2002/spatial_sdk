package com.meta.pixelandtexel.scanner

import android.app.Application
import com.meta.pixelandtexel.scanner.android.datasource.networkModule
import com.meta.pixelandtexel.scanner.android.datasource.repository.SmartHomeRepositoryImpl
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetConnectionUsecase
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetDevicesOfASmarthomeType
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetSmartPlugInfoUsecase
import com.meta.pixelandtexel.scanner.android.domain.usecases.ToggleSmartPlugUsecase
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.MRUKObjectsRepositoryImpl
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository.IMRUKObjectsRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.IObjectDetectorHelper
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.MLKitObjectDetector
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.IObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.repository.ObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.repository.DisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

val appModule = module {
    single<IDisplayedEntityRepository>{ DisplayedEntityRepository() }
    single<IObjectDetectorHelper>{ MLKitObjectDetector()}

    single<IObjectDetectionRepository> {
        ObjectDetectionRepository(get(), get())
    }

    single<IMRUKObjectsRepository> { MRUKObjectsRepositoryImpl() }

    single<SmartHomeRepository>{
        SmartHomeRepositoryImpl(get())
    }

    factory { GetConnectionUsecase(get())}
    factory { ToggleSmartPlugUsecase(get())}
    factory { GetSmartPlugInfoUsecase(get())}

    factory { GetDevicesOfASmarthomeType(get()) }

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