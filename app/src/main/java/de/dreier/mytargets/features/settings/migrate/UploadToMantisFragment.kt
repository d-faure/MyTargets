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
import de.dreier.mytargets.databinding.FragmentUploadMantisBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.features.settings.migrate.model.User
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import de.dreier.mytargets.shared.SharedApplicationInstance
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_migrate.*

class UploadToMantisFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentUploadMantisBinding

    public override fun onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_mantis, container, false)
        binding.migrateNowButton.setOnClickListener {
            Log.d("adjsfj", "start the count down")
        }
        return binding.root
    }

    override fun setActivityTitle() {
        activity!!.setTitle("Upload to Mantis")
    }
}
