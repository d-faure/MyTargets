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

package de.dreier.mytargets.app

import androidx.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.multidex.MultiDex
import android.util.Log
import androidx.room.RoomDatabase
import com.evernote.android.state.StateSaver
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.dreier.mytargets.BuildConfig
import de.dreier.mytargets.base.db.AppDatabase
import de.dreier.mytargets.base.db.migrations.*
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.utils.MobileWearableClient
import de.dreier.mytargets.utils.backup.MyBackupAgent
import im.delight.android.languages.Language
import timber.log.Timber

/**
 * Application singleton responsible for one-time process-wide setup:
 * locale configuration, Room database initialization, Wear OS client
 * bootstrap, and Timber logging.
 *
 * The companion object exposes the shared [db] instance and
 * [wearableClient] so they can be accessed from anywhere in the app
 * without a DI framework.
 */
class ApplicationInstance : SharedApplicationInstance() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (BuildConfig.DEBUG) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Ensure Application context/resources are fully initialized before locale mutation.
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
        if (BuildConfig.DEBUG) {
            enableDebugLogging()
        } else {
            Timber.plant(CrashReportingTree())
        }
        handleDatabaseImport()
        initRoomDb(this)
        wearableClient = MobileWearableClient(this)
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)
    }

    /**
     * Replaces the active database file with a previously staged import file
     * (created during a backup-restore flow). This must run **before**
     * [initRoomDb] so Room opens the restored data instead of the old copy.
     */
    private fun handleDatabaseImport() {
        val newDatabasePath = getDatabasePath(AppDatabase.DATABASE_FILE_NAME)
        val oldDatabasePath = getDatabasePath(AppDatabase.DATABASE_IMPORT_FILE_NAME)
        if (oldDatabasePath.exists()) {
            if (newDatabasePath.exists()) {
                newDatabasePath.delete()
            }
            oldDatabasePath.renameTo(newDatabasePath)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
    }

    override fun onTerminate() {
        dbOrNull?.close()
        wearableClient.disconnect()
        super.onTerminate()
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            FirebaseCrashlytics.getInstance().log(message);

            if (t != null) {
                if (priority == Log.ERROR || priority == Log.WARN) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                }
            }
        }
    }

    companion object {

        lateinit var wearableClient: MobileWearableClient
        private val dbLock = Any()
        private var _db: AppDatabase? = null

        val db: AppDatabase
            get() {
                synchronized(dbLock) {
                    val current = _db
                    if (current != null && current.isOpen) {
                        return current
                    }
                    val ctx = SharedApplicationInstance.contextOrNull
                        ?: throw IllegalStateException(
                            "Database accessed before context initialization. " +
                            "Call ensureDbInitialized(context) first."
                        )
                    initRoomDb(ctx)
                    return _db!!
                }
            }

        private val dbOrNull: AppDatabase?
            get() = synchronized(dbLock) { _db }

        /**
         * Guarantees that [db] is usable. Rebuilds the Room instance when
         * it has either never been created (`lateinit` not yet assigned) or
         * has been explicitly closed — e.g. after [DatabaseFixer.fix] or
         * a backup-restore cycle that may invalidate the old connection.
         */
        fun ensureDbInitialized(context: Context) {
            synchronized(dbLock) {
                SharedApplicationInstance.initialize(context)
                val current = _db
                if (current == null || !current.isOpen) {
                    initRoomDb(context.applicationContext)
                }
            }
        }

        val lastSharedPreferences: SharedPreferences
            get() = SharedApplicationInstance.context.getSharedPreferences(MyBackupAgent.PREFS, 0)

        /**
         * Creates (or recreates) the process-wide [AppDatabase] singleton.
         *
         * Uses [RoomDatabase.JournalMode.TRUNCATE] instead of WAL for
         * maximum compatibility with backup/restore and [DatabaseFixer].
         * Main-thread queries are allowed because several legacy UI paths
         * perform synchronous reads during `onCreateView`.
         */
        fun initRoomDb(context: Context) {
            SharedApplicationInstance.initialize(context)
            _db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, AppDatabase.DATABASE_FILE_NAME
            )
                .allowMainThreadQueries()
                .addCallback(RoomCreationCallback(context))
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .addMigrations(
                    Migration2, Migration3, Migration4,
                    Migration5, Migration6, Migration7,
                    Migration8, Migration9, Migration10,
                    Migration11, Migration12, Migration13,
                    Migration14, Migration15, Migration16,
                    Migration17, Migration18, Migration19,
                    Migration20, Migration21, Migration22,
                    Migration23, Migration24, Migration25,
                    Migration26
                )
                .build()
        }
    }

}
