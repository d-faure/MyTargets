package de.dreier.mytargets.features.settings.migrate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.FragmentUploadMantisBinding
import de.dreier.mytargets.features.settings.SettingsFragmentBase
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import de.dreier.mytargets.shared.SharedApplicationInstance.Companion.sharedPreferences
import de.dreier.mytargets.shared.models.Target
import org.json.JSONObject
import java.io.*


class UploadToMantisFragment : SettingsFragmentBase() {

    private lateinit var binding: FragmentUploadMantisBinding
    private val trainingDAO = ApplicationInstance.db.trainingDAO()
    private val roundDAO = ApplicationInstance.db.roundDAO()
    private val arrowDAO = ApplicationInstance.db.arrowDAO()
    private val bowDAO = ApplicationInstance.db.bowDAO()
    private val endDAO = ApplicationInstance.db.endDAO()
    private val shotDAO = ApplicationInstance.db.shotDAO()

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
        var file = File(context!!.filesDir, "trainings.csv")
        if (file.exists()) {
            file.delete()
        }

        val TRAINING_CSV_HEADER = "id, title, date, standardRoundId," +
                "bow, arrow, arrowNumbering," +
                "environment, comment, archerSignatureId," +
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
                val bow = bowDAO.loadBow(training.bowId!!.toLong()).toString().replace(",", " ")
                fileWriter.append(bow)
                fileWriter.append(',')
                val arrow = arrowDAO.loadArrow(training.arrowId!!.toLong()).toString().replace(",", " ")
                fileWriter.append(arrow)
                fileWriter.append(',')
                fileWriter.append(training.arrowNumbering.toString())
                fileWriter.append(',')
                val env = training.environment.toString().replace(",", " ")
                fileWriter.append(env)
                fileWriter.append(',')
                fileWriter.append(training.comment)
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
        var file = File(context!!.filesDir, "rounds.csv")
        if (file.exists()) {
            file.delete()
        }

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
                fileWriter.append(round.comment)
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


    fun loadShots() {
        var file = File(context!!.filesDir, "shots.csv")
        if (file.exists()) {
            file.delete()
        }

        val SHOTS_CSV_HEADER = "id, index, roundId, endId, x," +
                "y, score, maxScore, arrowNumber"

        val shots = trainingDAO.loadTrainings().flatMap {
                        training -> roundDAO.loadRounds(training.id).flatMap {
                            round -> endDAO.loadEnds(round.id).flatMap {
                                end -> shotDAO.loadShots((end.id))
                            }
                        }
                    }

        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(File(context!!.filesDir, "shots.csv"))

            fileWriter.append(SHOTS_CSV_HEADER)
            fileWriter.append('\n')

            var target = Target()

            for (shot in shots) {
                fileWriter.append(shot.id.toString())
                fileWriter.append(',')
                fileWriter.append(shot.index.toString())
                fileWriter.append(',')
                fileWriter.append(endDAO.loadRoundId(shot.endId!!.toLong()).toString())
                fileWriter.append(',')
                fileWriter.append(shot.endId.toString())
                fileWriter.append(',')
                fileWriter.append(shot.x.toString())
                fileWriter.append(',')
                fileWriter.append(shot.y.toString())
                fileWriter.append(',')
                var score = target.getSingleReachedScore(shot)
                fileWriter.append(score.reachedPoints.toString())
                fileWriter.append(',')
                fileWriter.append(score.totalPoints.toString())
                fileWriter.append(',')
                fileWriter.append(shot.arrowNumber.toString())
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
        val fileList: List<String> = listOf("trainings.csv", "rounds.csv", "shots.csv")

        loadTrainings()
        loadRounds()
        loadShots()

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
