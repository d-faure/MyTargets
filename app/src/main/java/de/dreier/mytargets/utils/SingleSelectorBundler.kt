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

import android.os.Bundle

import com.evernote.android.state.Bundler

import de.dreier.mytargets.utils.multiselector.SingleSelector

class SingleSelectorBundler : Bundler<SingleSelector> {
    override fun put(key: String, value: SingleSelector, bundle: Bundle) {
        // Guard against null value from Java-generated StateSaver code
        @Suppress("SENSELESS_COMPARISON")
        if (value == null || bundle == null) return
        bundle.putBundle(key, value.saveSelectionStates())
    }

    override fun get(key: String, bundle: Bundle): SingleSelector {
        val selector = SingleSelector()
        // Guard against null bundle from Java-generated StateSaver code.
        // The Evernote android-state library calls this from generated Java code
        // which can pass null despite Kotlin's non-null declaration.
        @Suppress("SENSELESS_COMPARISON")
        if (bundle == null) return selector
        val savedState = bundle.getBundle(key) ?: return selector
        selector.restoreSelectionStates(savedState)
        return selector
    }
}
