package de.dreier.mytargets.features.settings.migrate

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.common.net.MediaType
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.FragmentUploadMantisBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import de.dreier.mytargets.shared.SharedApplicationInstance
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.http.*
import java.io.*
import java.net.URL
import java.net.URLConnection

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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_mantis, container, false)
        binding.migrateNowButton.setOnClickListener {
//
//            val trainings = trainingDAO.loadTrainings()
//
//            var fileWriter: FileWriter? = null
//
//            try {
//                fileWriter = FileWriter("customer.csv")
//
//                fileWriter.append(TRAINING_CSV_HEADER)
//                fileWriter.append('\n')
//
//                for (training in trainings) {
//                    fileWriter.append(training.id.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.title)
//                    fileWriter.append(',')
//                    fileWriter.append(training.date.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.standardRoundId.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.bowId.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.arrowId.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.arrowNumbering.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.environment.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.comment)
//                    fileWriter.append(',')
//                    fileWriter.append(training.archerSignatureId.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.witnessSignatureId.toString())
//                    fileWriter.append(',')
//                    fileWriter.append(training.score.toString())
//                    fileWriter.append('\n')
//                }
//
//                println("Write CSV successfully!")
//            } catch (e: Exception) {
//                println("Writing CSV error!")
//                e.printStackTrace()
//            } finally {
//                try {
//                    fileWriter!!.flush()
//                    fileWriter.close()
//                } catch (e: IOException) {
//                    println("Flushing/closing error!")
//                    e.printStackTrace()
//                }
//            }
//
//            val rounds = trainingDAO.loadTrainings().flatMap {
//                            training -> roundDAO.loadRounds(training.id)
//                        }
//
//            Log.d("rounds", rounds.size.toString())
//
//            val ends = trainingDAO.loadTrainings().flatMap {
//                            training -> roundDAO.loadRounds(training.id).flatMap {
//                                round -> endDAO.loadEnds(round.id)
//                            }
//                        }
//
//            Log.d("ends", ends.size.toString())
            upload()
        }
        return binding.root
    }

    fun upload() {
        var fileWriter: FileWriter? = null
        fileWriter = FileWriter(File(context!!.filesDir, "customer.csv"))
        fileWriter.append(TRAINING_CSV_HEADER)
        fileWriter.append('\n')
        fileWriter!!.flush()
        fileWriter.close()

        var file = File(context!!.filesDir, "customer.csv")

        // create a new file
        val isNewFileCreated :Boolean = file.createNewFile()

        if(isNewFileCreated){
            Log.d("abcd","is created successfully.")
        } else{
            Log.d("abcd","already exists.")
        }

        val requestFile = file.asRequestBody("text/csv".toMediaTypeOrNull())
        val imageFile: MultipartBody.Part = MultipartBody.Part.createFormData("migration", file.getName(), requestFile)

        val jsonObject = JSONObject()
        jsonObject.put("name", "Ancd test")
        jsonObject.put("city", "delhi")
        jsonObject.put("age", "23")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        var viewModel: MainViewModel
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.uploadFile(body, body, imageFile)
        viewModel.fileResponse.observe(this, Observer { response ->
            if(response.isSuccessful){
                Log.d("hello kitty", response.body()?.response!!.toString())
                Toast.makeText(activity, "Could connect to our server, try again later.", Toast.LENGTH_SHORT).show()
            }
            Log.d("the tag", response.toString())
//            Toast.makeText(activity, response.isSuccessful, Toast.LENGTH_SHORT).show()
        })
    }

    override fun setActivityTitle() {
        activity!!.setTitle("Upload to Mantis")
    }
}
