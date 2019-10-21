package com.walmart.labs

import android.app.Application
import timber.log.Timber

/**
 * Overriding the application in order to plant a Timber tree for logging.
 */
class WalmartLabsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(object: Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return "${super.createStackElementTag(element)}: ${element.lineNumber}"
                }
            })
        }
    }
}