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
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import de.dreier.mytargets.R

abstract class SimpleFragmentActivityBase : ChildActivityBase() {

    private var activityToolbar: Toolbar? = null

    val childFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)!!

    protected abstract fun instantiateFragment(): Fragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge from the start to properly handle soft navigation keys
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        
        setContentView(R.layout.activity_simple_fragment)
        
        // Set up activity toolbar - fragments can override this with their own
        activityToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(activityToolbar)
        
        // Apply insets to the activity toolbar
        applyInsetsToActivityToolbar()

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
    
    private fun applyInsetsToActivityToolbar() {
        activityToolbar?.let { toolbar ->
            ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(
                    view.paddingLeft,
                    insets.top,
                    view.paddingRight,
                    view.paddingBottom
                )
                windowInsets
            }
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
        var childFragment: Fragment? = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (childFragment == null && intent?.extras != null) {
            childFragment = instantiateFragment()
            childFragment.arguments = intent.extras
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, childFragment!!, FRAGMENT_TAG)
            .commit()
    }

    companion object {
        const val FRAGMENT_TAG = "fragment"
    }
}
