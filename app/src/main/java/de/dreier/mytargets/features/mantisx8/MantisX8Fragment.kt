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
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import de.dreier.mytargets.R
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.databinding.FragmentLandingPageBinding
import de.dreier.mytargets.features.help.licences.LicencesActivity
import de.dreier.mytargets.utils.ToolbarUtils
import kotlinx.android.synthetic.main.fragment_landing_page.*
import uk.co.hassie.library.versioninfomdialog.VersionInfoMDialog
import java.io.IOException

/**
Shows info regarding the Mantis X8
 */
class MantisX8Fragment : Fragment() {

    private lateinit var navigationController: NavigationController
    private lateinit var binding: FragmentLandingPageBinding

    @CallSuper
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_landing_page, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationController = NavigationController(this)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.help, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_version_info -> {
                VersionInfoMDialog.Builder(context)
                        .setCopyrightText(R.string.app_copyright)
                        .setVersionPrefix(R.string.version_prefix)
                        .show()
                return true
            }
            R.id.action_open_source_licences -> {
                startActivity(Intent(context, LicencesActivity::class.java))
                return true
            }
            R.id.action_about -> {
                navigationController.navigateToAbout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
