package com.meta.pixelandtexel.scanner.di

import com.meta.pixelandtexel.scanner.objectdetection.repository.DisplayedEntityRepository
import com.meta.pixelandtexel.scanner.objectdetection.repository.IDisplayedEntityRepository

class AppContainer {

    val displayedEntityRepository: IDisplayedEntityRepository = DisplayedEntityRepository()
}