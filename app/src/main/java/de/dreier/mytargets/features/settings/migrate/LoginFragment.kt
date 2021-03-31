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
import de.dreier.mytargets.databinding.FragmentLoginBinding
import de.dreier.mytargets.databinding.FragmentMigrateBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.features.settings.migrate.model.User
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_migrate.*

    class LoginFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentLoginBinding

    public override fun onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.loginButton.setOnClickListener {
            var loginUsername = loginUsername.text.toString()
            var loginPass = loginPass.text.toString()

            if ((loginUsername.trim().isNotEmpty() ||
                            loginUsername.trim().isNotBlank()) &&
                (loginPass.trim().isNotEmpty() ||
                        loginPass.trim().isNotBlank())
            ) {
                var viewModel: MainViewModel
                val repository = Repository()
                val viewModelFactory = MainViewModelFactory(repository)
                viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

                val myPost = User("", loginUsername, loginPass, "", "0")
                viewModel.loginUser(myPost)
                viewModel.myResponse.observe(this, Observer { response ->
                    if(response.isSuccessful){
                        Log.d("response", response.body().toString())
                        Log.d("response", response.code().toString())
                        Log.d("response", response.body()?.email!!)
                        Log.d("response", response.body()?.user_secret_key!!)
                        Log.d("response", response.body()?.user_pk!!)

                        // store token in sharedpreferences
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

        return binding.root
    }

    override fun setActivityTitle() {
        activity!!.setTitle("Login")
    }
}
