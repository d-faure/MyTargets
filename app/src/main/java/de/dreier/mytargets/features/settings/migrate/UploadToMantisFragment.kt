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
import de.dreier.mytargets.shared.SharedApplicationInstance.Companion.sharedPreferences
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

    public override fun onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_mantis,
                                          container, false)
        binding.migrateNowButton.setOnClickListener {
            upload()
        }
        return binding.root
    }

    fun loadTrainings() {
        val TRAINING_CSV_HEADER = "id, title, date, standardRoundId," +
                "bowId, arrowId, arrowNumbering," +
                "environment, comment, archerSignatureId" +
                "witnessSignatureId, score"

        val trainings = trainingDAO.loadTrainings()
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(File(context!!.filesDir, "trainings.csv"))

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

            Log.d("csv-operations", "Write CSV successfully!")
        } catch (e: Exception) {
            Log.d("csv-operations", "Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                Log.d("csv-operations", "Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }

    fun loadRounds() {
        val ROUNDS_CSV_HEADER = "id, trainingId, index, shotsPerEnd," +
                "maxEndCount, distance, comment, target, score"

        val rounds = trainingDAO.loadTrainings().flatMap {
                        training -> roundDAO.loadRounds(training.id)
                    }

        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(File(context!!.filesDir, "rounds.csv"))

            fileWriter.append(ROUNDS_CSV_HEADER)
            fileWriter.append('\n')

            for (round in rounds) {
                fileWriter.append(round.id.toString())
                fileWriter.append(',')
                fileWriter.append(round.trainingId.toString())
                fileWriter.append(',')
                fileWriter.append(round.index.toString())
                fileWriter.append(',')
                fileWriter.append(round.shotsPerEnd.toString())
                fileWriter.append(',')
                fileWriter.append(round.maxEndCount.toString())
                fileWriter.append(',')
                fileWriter.append(round.distance.toString())
                fileWriter.append(',')
                fileWriter.append(round.comment.toString())
                fileWriter.append(',')
                fileWriter.append(round.target.toString())
                fileWriter.append(',')
                fileWriter.append(round.score.toString())
                fileWriter.append('\n')
            }

            Log.d("csv-operations", "Write CSV successfully!")
        } catch (e: Exception) {
            Log.d("csv-operations", "Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                Log.d("csv-operations", "Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }


    fun loadEnds() {
        val ENDS_CSV_HEADER = "id, index, roundId, standardRoundId," +
                "exact, saveTime, comment, score"

        val ends = trainingDAO.loadTrainings().flatMap {
                        training -> roundDAO.loadRounds(training.id).flatMap {
                            round -> endDAO.loadEnds(round.id)
                        }
                    }

        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(File(context!!.filesDir, "ends.csv"))

            fileWriter.append(ENDS_CSV_HEADER)
            fileWriter.append('\n')

            for (end in ends) {
                fileWriter.append(end.id.toString())
                fileWriter.append(',')
                fileWriter.append(end.index.toString())
                fileWriter.append(',')
                fileWriter.append(end.roundId.toString())
                fileWriter.append(',')
                fileWriter.append(end.exact.toString())
                fileWriter.append(',')
                fileWriter.append(end.saveTime.toString())
                fileWriter.append(',')
                fileWriter.append(end.comment)
                fileWriter.append(',')
                fileWriter.append(end.score.toString())
                fileWriter.append('\n')
            }

            Log.d("csv-operations", "Write CSV successfully!")
        } catch (e: Exception) {
            Log.d("csv-operations", "Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                Log.d("csv-operations", "Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }

    fun upload() {

        loadTrainings()
        loadRounds()
        loadEnds()

        val fileList: List<String> = listOf("trainings.csv", "rounds.csv", "ends.csv")
        val itr = fileList.listIterator()
        val csvFiles: MutableList<MultipartBody.Part> = mutableListOf()

        while (itr.hasNext()) {

            var file = File(context!!.filesDir, itr.next())

            // create a new file
            val isNewFileCreated: Boolean = file.createNewFile()

            if (isNewFileCreated) {
                Log.d("csv-operations", "File created successfully.")
            } else {
                Log.d("csv-operations", "File already exists.")
            }

            val requestFile = file.asRequestBody("text/csv".toMediaTypeOrNull())
            val csvFile: MultipartBody.Part = MultipartBody.Part.createFormData("migration",
                    file.getName(), requestFile)
            csvFiles.add(csvFile)
        }

        // check if token is in shared preferences
        val sharedEmail = sharedPreferences.getString("email","")
        val sharedUserSecretKey = sharedPreferences.getString("user_secret_key","")
        val sharedUserPk = sharedPreferences.getString("user_pk","0")

        val jsonObject = JSONObject()
        jsonObject.put("email", sharedEmail)
        jsonObject.put("secret_key", sharedUserSecretKey)
        jsonObject.put("user_pk", sharedUserPk)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        var viewModel: MainViewModel
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.uploadFile(body, csvFiles[0], csvFiles[1], csvFiles[2])
        viewModel.fileResponse.observe(this, Observer { response ->
            if(response.isSuccessful){
                Toast.makeText(activity, "Migration complete, open Mantis Archery app.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Could not connect to our server, try again later.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun setActivityTitle() {
        activity!!.setTitle("Upload to Mantis")
    }
}
