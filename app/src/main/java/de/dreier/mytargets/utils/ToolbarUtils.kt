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

package de.dreier.mytargets.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import de.dreier.mytargets.R
import timber.log.Timber

object ToolbarUtils {
    
    /**
     * Apply window insets to toolbar for edge-to-edge display (SDK 36+)
     * Call this after setSupportActionBar
     */
    fun applyWindowInsets(toolbar: Toolbar) {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            Timber.d("Applying window insets to toolbar - Top: ${insets.top}, Bottom: ${insets.bottom}")
            
            // Apply top inset as padding to toolbar
            val currentPaddingLeft = view.paddingLeft
            val currentPaddingRight = view.paddingRight
            val currentPaddingBottom = view.paddingBottom
            
            view.setPadding(
                currentPaddingLeft,
                insets.top,
                currentPaddingRight,
                currentPaddingBottom
            )
            
            Timber.d("Toolbar padding applied: top=${insets.top}")
            
            // Don't consume - let other views handle insets too
            windowInsets
        }
        
        // Force request insets from the decor view (more reliable)
        toolbar.post {
            toolbar.context?.let { context ->
                if (context is AppCompatActivity) {
                    ViewCompat.requestApplyInsets(context.window.decorView)
                } else {
                    ViewCompat.requestApplyInsets(toolbar)
                }
            } ?: ViewCompat.requestApplyInsets(toolbar)
        }
    }
    
    /**
     * Apply window insets to a bottom view (like bottom navigation or FAB)
     */
    fun applyWindowInsetsToBottom(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            Timber.d("Applying window insets to bottom view - Bottom: ${insets.bottom}")
            
            // Apply bottom inset as margin for FABs or padding for other views
            val lp = v.layoutParams
            if (lp is android.view.ViewGroup.MarginLayoutParams) {
                lp.bottomMargin = insets.bottom + 16 // Add extra margin for FABs
                v.layoutParams = lp
            } else {
                // Apply bottom inset as padding
                v.setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    insets.bottom
                )
            }
            
            // Don't consume insets
            windowInsets
        }
        
        // Force request insets from decor view (more reliable)
        view.post {
            view.context?.let { context ->
                if (context is AppCompatActivity) {
                    ViewCompat.requestApplyInsets(context.window.decorView)
                } else {
                    ViewCompat.requestApplyInsets(view)
                }
            } ?: ViewCompat.requestApplyInsets(view)
        }
    }

    fun showUpAsX(fragment: Fragment) {
        showUpAsX((fragment.activity as AppCompatActivity?)!!)
    }

    private fun showUpAsX(activity: AppCompatActivity) {
        val supportActionBar = activity.supportActionBar!!
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
    }

    fun showHomeAsUp(fragment: Fragment) {
        showHomeAsUp((fragment.activity as AppCompatActivity?)!!)
    }

    fun showHomeAsUp(activity: AppCompatActivity) {
        val supportActionBar = activity.supportActionBar!!
        supportActionBar.setDisplayHomeAsUpEnabled(true)
    }

    fun setSupportActionBar(fragment: Fragment, toolbar: Toolbar) {
        val activity = fragment.activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
        
        // If the activity is SimpleFragmentActivityBase, hide its toolbar since fragment has its own
        if (activity is de.dreier.mytargets.base.activities.SimpleFragmentActivityBase) {
            activity.hideActivityToolbar()
            // Enable edge-to-edge for fragments with their own toolbar
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            activity.window.statusBarColor = android.graphics.Color.TRANSPARENT
        }
        
        // Automatically apply window insets for edge-to-edge display
        Timber.d("setSupportActionBar called for ${fragment.javaClass.simpleName} - applying insets")
        applyWindowInsets(toolbar)
    }

    fun setTitle(fragment: Fragment, @StringRes title: Int) {
        setTitle((fragment.activity as AppCompatActivity?)!!, title)
    }

    fun setTitle(fragment: Fragment, title: String) {
        setTitle((fragment.activity as AppCompatActivity?)!!, title)
    }

    fun setTitle(activity: AppCompatActivity, @StringRes title: Int) {
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.setTitle(title)
    }

    fun setTitle(activity: AppCompatActivity, title: String) {
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.title = title
    }

    fun setSubtitle(fragment: Fragment, subtitle: String) {
        val activity = fragment.activity as AppCompatActivity?
        setSubtitle(activity!!, subtitle)
    }

    fun setSubtitle(activity: AppCompatActivity, subtitle: String) {
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.subtitle = subtitle
    }
}
