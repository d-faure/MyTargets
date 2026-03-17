package de.dreier.mytargets.base.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

/**
 * Backfills missing Shot records for historical ends.
 *
 * Older app versions did not pre-create all Shot slots when an end was started,
 * so some ends have fewer Shot rows than the round's [shotsPerEnd]. This migration
 * finds the exact missing indexes (0 until shotsPerEnd) and inserts them with
 * default values (scoringRing = -2 / NOTHING_SELECTED).
 */
object Migration27 : Migration(26, 27) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.i("Migrating DB from version 26 to 27: backfill missing shots")

        // Find all ends that have fewer shots than expected
        val endCursor = database.query(
            """
            SELECT e.id AS endId, r.shotsPerEnd
            FROM `End` e
            JOIN `Round` r ON e.`roundId` = r.`id`
            WHERE (SELECT COUNT(*) FROM `Shot` WHERE `endId` = e.id) < r.`shotsPerEnd`
            """
        )

        endCursor.use {
            while (it.moveToNext()) {
                val endId = it.getLong(0)
                val shotsPerEnd = it.getInt(1)

                // Get existing indexes for this end
                val existingIndexes = mutableSetOf<Int>()
                val indexCursor = database.query(
                    "SELECT `index` FROM `Shot` WHERE `endId` = ?",
                    arrayOf<Any>(endId)
                )
                indexCursor.use { ic ->
                    while (ic.moveToNext()) {
                        existingIndexes.add(ic.getInt(0))
                    }
                }

                // Insert shots for each missing index in 0 until shotsPerEnd
                for (i in 0 until shotsPerEnd) {
                    if (!existingIndexes.contains(i)) {
                        database.execSQL(
                            """
                            INSERT INTO `Shot` (`index`, `endId`, `x`, `y`, `scoringRing`, `arrowNumber`)
                            VALUES (?, ?, 0.0, 0.0, -2, NULL)
                            """,
                            arrayOf<Any>(i, endId)
                        )
                    }
                }
            }
        }
    }
}
