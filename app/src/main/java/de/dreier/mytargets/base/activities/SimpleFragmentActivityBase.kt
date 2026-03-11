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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import de.dreier.mytargets.R
import de.dreier.mytargets.utils.ToolbarUtils

abstract class SimpleFragmentActivityBase : ChildActivityBase() {

    private var activityToolbar: Toolbar? = null

    val childFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)!!

    protected abstract fun instantiateFragment(): Fragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_simple_fragment)
        
        activityToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(activityToolbar)
        activityToolbar?.let { ToolbarUtils.applyWindowInsets(it) }

        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            var childFragment: Fragment? = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (childFragment == null) {
                childFragment = instantiateFragment()
                childFragment.arguments = intent?.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, childFragment, FRAGMENT_TAG)
                .commit()
        }
    }
    
    /**
     * Hide the activity's toolbar when a fragment uses its own toolbar
     */
    fun hideActivityToolbar() {
        activityToolbar?.visibility = View.GONE
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (supportFragmentManager.isStateSaved) return
        var childFragment: Fragment? = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (childFragment == null && intent?.extras != null) {
            childFragment = instantiateFragment()
            childFragment.arguments = intent.extras
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, childFragment!!, FRAGMENT_TAG)
            .commitAllowingStateLoss()
    }

    companion object {
        const val FRAGMENT_TAG = "fragment"
    }
}
