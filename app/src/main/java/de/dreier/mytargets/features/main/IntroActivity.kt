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
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import de.dreier.mytargets.R

class IntroActivity : MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        try {
            super.onRestoreInstanceState(savedInstanceState)
        } catch (e: NullPointerException) {
            // Work around a third-party intro library crash where page indicators
            // can restore before internal dot arrays are initialized.
            val isKnownIndicatorCrash = e.stackTrace.any { it.className == "io.github.dreierf.materialintroscreen.widgets.InkPageIndicator" }
            if (!isKnownIndicatorCrash) {
                throw e
            }
        }
    }

    private fun applyBottomInsetsToNavigation() {
        val navView = findViewById<View>(R.id.navigation_view) ?: return
        val originalPaddingBottom = navView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(navView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val extraBottomPx = (8 * v.resources.displayMetrics.density).toInt()
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                originalPaddingBottom + insets.bottom + extraBottomPx
            )
            windowInsets
        }
        ViewCompat.requestApplyInsets(navView)
    }
}
