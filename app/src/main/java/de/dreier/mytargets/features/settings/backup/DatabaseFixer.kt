/*
 * Copyright (C) 2019 Florian Dreier
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

package de.dreier.mytargets.features.settings.backup

import de.dreier.mytargets.base.db.AppDatabase
import timber.log.Timber

/**
 * Attempts to repair a potentially corrupted SQLite database by running
 * integrity checks, rebuilding indices, and reclaiming free space.
 *
 * This is exposed to the user via the backup-settings screen as a
 * self-service recovery action. Because it operates on the **app-wide
 * singleton** [AppDatabase], callers must never close the underlying
 * connection — the same Room instance continues to be shared by every
 * DAO in the process after this method returns.
 */
object DatabaseFixer {

    /**
     * Runs a sequence of low-level SQLite maintenance commands against [db]:
     *
     * 1. Disables WAL so that maintenance SQLs run in rollback-journal mode.
     * 2. `REINDEX` — rebuilds all indices.
     * 3. `VACUUM` — defragments and shrinks the database file.
     * 4. `PRAGMA integrity_check` — full page-level consistency verification.
     * 5. Quick sanity read of the `Training` table to confirm data is accessible.
     *
     * **Important:** [db] is the process-wide Room singleton held by
     * [de.dreier.mytargets.app.ApplicationInstance.Companion.db].
     * The connection is intentionally left open so that subsequent DAO
     * operations (e.g. `BowDAO.loadBows()`) do not crash with
     * `SQLException: connection is closed`.
     */
    fun fix(db: AppDatabase) {
        val openHelper = db.openHelper
        openHelper.setWriteAheadLoggingEnabled(false)
        val database = openHelper.writableDatabase
        Timber.d("db integrity: %b", database.isDatabaseIntegrityOk)

        database.query("reindex", arrayOf())
        database.query("vacuum", arrayOf())

        val cursor = database.query("pragma integrity_check;", arrayOf())
        cursor.moveToNext()
        val string = cursor.getString(0)
        Timber.w("pragma integrity_check => $string")
        cursor.close()

        val cursor2 = database.query("SELECT * FROM `Training`", arrayOf())
        cursor2.moveToNext()
        Timber.w("trainings => ${cursor2.count}")
        cursor2.close()
    }
}
