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

package de.dreier.mytargets.features.settings

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import de.dreier.mytargets.features.settings.migrate.MainViewModel
import de.dreier.mytargets.features.settings.migrate.MainViewModelFactory
import de.dreier.mytargets.features.settings.migrate.model.User
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.features.settings.migrate.repository.Repository

class MigrateSettingsFragment : SettingsFragmentBase() {

    public override fun updateItemSummaries() {
        // Disable file type selection for pre-Kitkat, since they do not support PDF generation
        val shareCategory = preferenceManager.findPreference(KEY_STATISTICS)
        shareCategory.isVisible = Utils.isKitKat

        val connectButton: Preference = findPreference("connect_button")
        connectButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            var viewModel: MainViewModel
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            viewModel.getPost()
            viewModel.myResponse.observe(this, Observer { response ->
                if(response.isSuccessful){
                    Log.d("response", response.body()?.userId.toString())
                    Log.d("response", response.body()?.id.toString())
                    Log.d("response", response.body()?.title!!)
                    Log.d("response", response.body()?.body!!)
                } else {
                    Log.d("response", response.errorBody().toString())
                }
            })
            //code for what you want it to do
            true
        }


        val signupButton: Preference = findPreference("signup_button")
        signupButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            var viewModel: MainViewModel
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

            val myPost = User(2, 2, "abc", "abcd")
            viewModel.pushPost(myPost)
            viewModel.myResponse.observe(this, Observer { response ->
                if(response.isSuccessful){
                    Log.d("response", response.body().toString())
                    Log.d("response", response.code().toString())
                    Log.d("response", response.message())
                } else {
                    Log.d("response", response.errorBody().toString())
                }
            })
            //code for what you want it to do
            true
        }
    }

    companion object {
        const val KEY_STATISTICS = "migrate"
    }
}
