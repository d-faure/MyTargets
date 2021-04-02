    package de.dreier.mytargets.features.settings.migrate

import android.content.SharedPreferences
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
import de.dreier.mytargets.shared.SharedApplicationInstance
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_migrate.*

    class LoginFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentLoginBinding

    public override fun onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    private fun launchMigrate() {
        /*
        Opens the fragment where one can have the option to update
        your data into Mantis backend
        */
        val fragment: Fragment = UploadToMantisFragment()
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.login_frame, fragment)
        fragmentTransaction.commit()
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
                        var email =  response.body()?.email!!
                        var user_secret_key = response.body()?.user_secret_key!!
                        var user_pk = response.body()?.user_pk!!

                        val editor: SharedPreferences.Editor = SharedApplicationInstance.sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.putString("user_secret_key", user_secret_key)
                        editor.putString("user_pk", user_pk)

                        editor.apply()
                        editor.commit()

                        launchMigrate()
                    } else {
                        Toast.makeText(activity, "Couldn't connect to our server, try again later.", Toast.LENGTH_SHORT).show()
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
