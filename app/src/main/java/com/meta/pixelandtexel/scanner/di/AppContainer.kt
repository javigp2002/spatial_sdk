package com.meta.pixelandtexel.scanner.di

import com.meta.pixelandtexel.scanner.objectdetection.detector.MLKitObjectDetector
import com.meta.pixelandtexel.scanner.objectdetection.repository.DisplayedEntityRepository
import com.meta.pixelandtexel.scanner.objectdetection.repository.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.objectdetection.repository.detection.ObjectDetectionRepository

class AppContainer {

    val displayedEntityRepository: IDisplayedEntityRepository = DisplayedEntityRepository()

    val mLKitObjectDetector = MLKitObjectDetector()
    val objectDetectRepository = ObjectDetectionRepository(mLKitObjectDetector)
}