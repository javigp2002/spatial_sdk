package com.meta.pixelandtexel.scanner.di

import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.ObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.DisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.MLKitObjectDetector

class AppContainer {

    val displayedEntityRepository: IDisplayedEntityRepository = DisplayedEntityRepository()

    val mLKitObjectDetector = MLKitObjectDetector()
    val objectDetectRepository = ObjectDetectionRepository(mLKitObjectDetector, displayedEntityRepository)
}