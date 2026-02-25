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

package de.dreier.mytargets.features.help

import android.app.AlertDialog
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import android.view.*
import de.dreier.mytargets.R
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.databinding.FragmentWebBinding
import de.dreier.mytargets.features.help.licences.LicencesActivity
import de.dreier.mytargets.utils.ToolbarUtils
import java.io.IOException

/**
 * Shows all rounds of one training.
 */
class HelpFragment : Fragment() {

    private lateinit var navigationController: NavigationController
    private lateinit var binding: FragmentWebBinding

    private val helpHtmlPage: String
        get() {
            var prompt = ""
            try {
                val inputStream = resources.openRawResource(R.raw.help)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                prompt = String(buffer)
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return prompt
        }

    private fun addBottomSpacerToHtml(html: String): String {
        val spacer = "<div style=\"height: 140px;\"></div>"
        return if (html.contains("</body>", ignoreCase = true)) {
            html.replace("</body>", "$spacer</body>", ignoreCase = true)
        } else {
            html + spacer
        }
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false)
        val prompt = addBottomSpacerToHtml(helpHtmlPage)
        binding.webView
            .loadDataWithBaseURL("file:///android_asset/", prompt, "text/html", "utf-8", "")
        binding.webView.isHorizontalScrollBarEnabled = false
        val originalPaddingLeft = binding.webView.paddingLeft
        val originalPaddingTop = binding.webView.paddingTop
        val originalPaddingRight = binding.webView.paddingRight
        val originalPaddingBottom = binding.webView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.webView) { view, windowInsets ->
            val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
            val safeBottomInset = maxOf(navInsets.bottom, imeInsets.bottom)
            val extraBottomPx = (40 * view.resources.displayMetrics.density).toInt()
            view.setPadding(
                originalPaddingLeft,
                originalPaddingTop,
                originalPaddingRight,
                originalPaddingBottom + safeBottomInset + extraBottomPx
            )
            binding.webView.clipToPadding = false
            windowInsets
        }
        ViewCompat.requestApplyInsets(binding.webView)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationController = NavigationController(this)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_source_licences -> {
                startActivity(Intent(context, LicencesActivity::class.java))
                true
            }

            R.id.action_about -> {
                navigationController.navigateToAbout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
