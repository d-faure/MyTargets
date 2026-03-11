/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.annotation.StringRes
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

open class SharedApplicationInstance : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initialize(applicationContext)
    }

    protected fun enableDebugLogging() {
//        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
////                .detectAll()
//                .penaltyLog()
//                .build())
//        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .build())
        Timber.plant(Timber.DebugTree())
    }

    companion object {

        @Volatile
        private var appContext: Context? = null

        val contextOrNull: Context?
            get() = appContext

        var context: Context
            get() = appContext
                ?: throw UninitializedPropertyAccessException(
                    "SharedApplicationInstance.context has not been initialized"
                )
            set(value) {
                appContext = value.applicationContext
            }

        fun initialize(context: Context) {
            appContext = context.applicationContext
        }

        fun <T> runWithContext(onMissing: () -> T, block: Context.() -> T): T {
            val context = appContext ?: return onMissing()
            return block(context)
        }

        fun getStr(@StringRes string: Int): String {
            return context.getString(string)
        }

        val sharedPreferences: SharedPreferences
            get() {
                return PreferenceManager.getDefaultSharedPreferences(context)
            }
    }
}
