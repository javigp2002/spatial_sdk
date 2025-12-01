package com.meta.pixelandtexel.scanner.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.ObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.DisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.MLKitObjectDetector
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class AppContainer {

    val displayedEntityRepository: IDisplayedEntityRepository = DisplayedEntityRepository()

    val mLKitObjectDetector = MLKitObjectDetector()
    val objectDetectRepository = ObjectDetectionRepository(mLKitObjectDetector, displayedEntityRepository)

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
}