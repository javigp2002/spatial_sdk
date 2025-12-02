package com.meta.pixelandtexel.scanner

import android.app.Application
import com.meta.pixelandtexel.scanner.di.AppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DiApplication : Application() {

    val appContainer by lazy { AppContainer() }
}