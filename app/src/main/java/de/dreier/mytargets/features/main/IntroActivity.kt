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

package de.dreier.mytargets.features.main

import io.github.dreierf.materialintroscreen.MaterialIntroActivity
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.dreier.mytargets.R

class IntroActivity : MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for SDK 36+
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        
        // Apply bottom insets to the navigation buttons container
        applyBottomInsetsToNavigation()

        hideBackButton()

        enableLastSlideAlphaExitTransition(true)

        addSlide(
            SlideFragmentBuilder()
                .backgroundColor(R.color.introBackground)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.intro_screen_1)
                .title(getString(R.string.intro_title_track_training_progress))
                .description(getString(R.string.intro_description_track_training_progress))
                .build()
        )

        addSlide(
            SlideFragmentBuilder()
                .backgroundColor(R.color.introBackground)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.intro_screen_2)
                .title(getString(R.string.intro_title_everything_in_one_place))
                .description(getString(R.string.intro_description_everything_in_one_place))
                .build()
        )
    }
    
    private fun applyBottomInsetsToNavigation() {
        // Find the bottom navigation container (FAB and other nav elements)
        val decorView = window.decorView
        decorView.post {
            // Find FABs and navigation elements and apply bottom insets
            findAndApplyInsetsToFabs(decorView as ViewGroup)
        }
    }
    
    private fun findAndApplyInsetsToFabs(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is FloatingActionButton) {
                val originalMarginBottom = (child.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
                ViewCompat.setOnApplyWindowInsetsListener(child) { v, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    val lp = v.layoutParams as? ViewGroup.MarginLayoutParams
                    if (lp != null) {
                        lp.bottomMargin = originalMarginBottom + insets.bottom
                        v.layoutParams = lp
                    }
                    windowInsets
                }
                ViewCompat.requestApplyInsets(child)
            } else if (child is ViewGroup) {
                // Check if this is a navigation container near the bottom
                if (child.id != View.NO_ID) {
                    val resourceName = try {
                        resources.getResourceEntryName(child.id)
                    } catch (e: Exception) {
                        ""
                    }
                    if (resourceName.contains("navigation", ignoreCase = true) ||
                        resourceName.contains("button", ignoreCase = true)) {
                        val originalPaddingBottom = child.paddingBottom
                        ViewCompat.setOnApplyWindowInsetsListener(child) { v, windowInsets ->
                            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                            v.setPadding(
                                v.paddingLeft,
                                v.paddingTop,
                                v.paddingRight,
                                originalPaddingBottom + insets.bottom
                            )
                            windowInsets
                        }
                        ViewCompat.requestApplyInsets(child)
                    }
                }
                findAndApplyInsetsToFabs(child)
            }
        }
    }
}
