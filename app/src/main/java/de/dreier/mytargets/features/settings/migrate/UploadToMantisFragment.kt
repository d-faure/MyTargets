package de.dreier.mytargets.features.settings.migrate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.common.net.MediaType
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.FragmentUploadMantisBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileWriter
import java.io.IOException
import okhttp3.RequestBody
import retrofit2.http.*


class UploadToMantisFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentUploadMantisBinding
    private val trainingDAO = ApplicationInstance.db.trainingDAO()
    private val roundDAO = ApplicationInstance.db.roundDAO()
    private val endDAO = ApplicationInstance.db.endDAO()
    private val TRAINING_CSV_HEADER = "id, title, date, standardRoundId," +
                                      "bowId, arrowId, arrowNumbering," +
                                      "environment, comment, archerSignatureId" +
                                      "witnessSignatureId, score"

    public override fun onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var viewModel: MainViewModel
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_mantis, container, false)
        binding.migrateNowButton.setOnClickListener {

            val trainings = trainingDAO.loadTrainings()

            var fileWriter: FileWriter? = null

            try {
                fileWriter = FileWriter("customer.csv")

                fileWriter.append(TRAINING_CSV_HEADER)
                fileWriter.append('\n')

                for (training in trainings) {
                    fileWriter.append(training.id.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.title)
                    fileWriter.append(',')
                    fileWriter.append(training.date.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.standardRoundId.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.bowId.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.arrowId.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.arrowNumbering.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.environment.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.comment)
                    fileWriter.append(',')
                    fileWriter.append(training.archerSignatureId.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.witnessSignatureId.toString())
                    fileWriter.append(',')
                    fileWriter.append(training.score.toString())
                    fileWriter.append('\n')
                }

                println("Write CSV successfully!")
            } catch (e: Exception) {
                println("Writing CSV error!")
                e.printStackTrace()
            } finally {
                try {
                    fileWriter!!.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    println("Flushing/closing error!")
                    e.printStackTrace()
                }
            }

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

    fun upload() {
        val file = File("/storage/emulated/0/Download/Corrections 6.jpg")
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.getName(), requestFile)

        val fullName: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "Your Name")

        viewModel.updateProfile(fullName, fullName, body)
    }

    override fun setActivityTitle() {
        activity!!.setTitle("Upload to Mantis")
    }
}
