package de.dreier.mytargets.features.settings.migrate

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.FragmentUploadMantisBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd

class UploadToMantisFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentUploadMantisBinding
    private val trainingDAO = ApplicationInstance.db.trainingDAO()
    private val roundDAO = ApplicationInstance.db.roundDAO()
    private val endDAO = ApplicationInstance.db.endDAO()

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

            val trainings = trainingDAO.loadTrainings()

            Log.d("trainings", trainings.size.toString())

            val rounds = trainingDAO.loadTrainings().flatMap {
                            training -> roundDAO.loadRounds(training.id)
                        }

            Log.d("rounds", rounds.size.toString())

            val ends = trainingDAO.loadTrainings().flatMap {
                            training -> roundDAO.loadRounds(training.id).flatMap {
                                round -> endDAO.loadEnds(round.id)
                            }
                        }

            Log.d("ends", ends.size.toString())

        }
        return binding.root
    }

    override fun setActivityTitle() {
        activity!!.setTitle("Upload to Mantis")
    }
}
