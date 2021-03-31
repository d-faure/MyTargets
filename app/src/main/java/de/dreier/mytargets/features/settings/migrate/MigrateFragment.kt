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

package de.dreier.mytargets.features.settings.migrate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.FragmentMigrateBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.features.settings.migrate.model.User
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import kotlinx.android.synthetic.main.fragment_migrate.*


class MigrateFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentMigrateBinding

    public override fun onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_migrate, container, false)

        binding.signupButton.setOnClickListener {

            // if token is in shared preferences
            val sharedIdValue = sharedPreferences.getInt("id_key",0)
            val sharedNameValue = sharedPreferences.getString("name_key","defaultname")
            if(sharedIdValue.equals(0) && sharedNameValue.equals("defaultname")){
                outputName.setText("default name: ${sharedNameValue}").toString()
                outputId.setText("default id: ${sharedIdValue.toString()}")
            }else{
                outputName.setText(sharedNameValue).toString()
                outputId.setText(sharedIdValue.toString())
            }
            // load migrate fragment
            // else

            var emailText = editEmail.text.toString()
            var usernameText = editUsername.text.toString()
            var passwordText = editPass.text.toString()

            if ((usernameText.trim().isNotEmpty() ||
                usernameText.trim().isNotBlank()) &&
                (emailText.trim().isNotEmpty() ||
                emailText.trim().isNotBlank()) &&
                (passwordText.trim().isNotEmpty() ||
                passwordText.trim().isNotBlank())
            ) {
                var viewModel: MainViewModel
                val repository = Repository()
                val viewModelFactory = MainViewModelFactory(repository)
                viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

                val myPost = User(emailText, usernameText, passwordText, "", "0")
                viewModel.createUser(myPost)
                viewModel.myResponse.observe(this, Observer { response ->
                    if(response.isSuccessful){
                        Log.d("response", response.body().toString())
                        Log.d("response", response.code().toString())
                        Log.d("response", response.body()?.email!!)
                        Log.d("response", response.body()?.user_secret_key!!)
                        Log.d("response", response.body()?.user_pk!!)

                        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                        editor.putInt("id_key",id)
                        editor.putString("name_key",name)
                        editor.apply()
                        editor.commit()
                    } else {
                        Log.d("response", "response.errorBody().toString()")
                    }
                })
            } else {
                Toast.makeText(activity, "Every field should be filled.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginFragmentButton.setOnClickListener {
            val fragment: Fragment = LoginFragment()
            val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.login_frame, fragment)
            fragmentTransaction.commit()
        }

        return binding.root
    }

    override fun setActivityTitle() {
        activity!!.setTitle(R.string.migrate_action)
    }
}
