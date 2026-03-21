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

package de.dreier.mytargets.base.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.features.settings.SettingsManager
import im.delight.android.languages.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ChildActivityBase : AppCompatActivity() {

    protected lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        navigationController = NavigationController(this)
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val eventName = javaClass.simpleName
        lifecycleScope.launch(Dispatchers.IO) {
            FirebaseAnalytics.getInstance(this@ChildActivityBase.applicationContext).logEvent(eventName, null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigationController.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        navigationController.finish()
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
