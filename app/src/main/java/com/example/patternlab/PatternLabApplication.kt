package com.example.patternlab

import android.app.Application
import com.example.patternlab.di.AppContainer

/** Lives as long as the process, so the container (and its singletons) does too. */
class PatternLabApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
