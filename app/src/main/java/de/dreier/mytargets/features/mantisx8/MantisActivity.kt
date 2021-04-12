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

package de.dreier.mytargets.features.mantisx8

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase


class MantisActivity : SimpleFragmentActivityBase() {

    override fun instantiateFragment(): Fragment {
        return MantisX8Fragment()
    }

    fun goToShopify(view: View?) {
        goToUrl("https://mantisarchery.com/products/mantis-x8?utm_source=mytargets")
    }

    private fun goToUrl(url: String) {
        val uriUrl: Uri = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }

    override fun onBackPressed() {
        navigationController.finish(animate = false)
    }
}
