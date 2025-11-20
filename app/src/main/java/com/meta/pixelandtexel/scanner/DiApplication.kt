package com.meta.pixelandtexel.scanner

import android.app.Application
import com.meta.pixelandtexel.scanner.di.AppContainer

class DiApplication: Application(){

    val appContainer by lazy { AppContainer() }
}