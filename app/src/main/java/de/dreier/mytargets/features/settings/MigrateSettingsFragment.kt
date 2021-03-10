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

package de.dreier.mytargets.features.settings

import android.util.Log
import androidx.preference.Preference
import de.dreier.mytargets.R
import de.dreier.mytargets.utils.Utils

class MigrateSettingsFragment : SettingsFragmentBase() {

    public override fun updateItemSummaries() {
        // Disable file type selection for pre-Kitkat, since they do not support PDF generation
        val shareCategory = preferenceManager.findPreference(KEY_STATISTICS)
        shareCategory.isVisible = Utils.isKitKat

        val button: Preference = findPreference("myCoolButton")
        button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            Log.d("Danger", "djsafjdslajf safdasf asdfasfd laksdjflajskj")
            Log.d("Danger", "djsafjdslajf safdasf asdfasfd laksdjflajskj")
            //code for what you want it to do
            true
        }
    }

    companion object {
        const val KEY_STATISTICS = "migrate"
    }
}
