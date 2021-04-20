package de.dreier.mytargets.features.settings.migrate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.dreier.mytargets.features.settings.migrate.model.FileResponse
import de.dreier.mytargets.features.settings.migrate.model.User
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody


class MainViewModel(private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Response<User>> = MutableLiveData()
    val fileResponse: MutableLiveData<Response<FileResponse>> = MutableLiveData()

    fun createUser(user: User) {
        viewModelScope.launch {
            val response = repository.createUser(user)
            myResponse.value = response
        }
    }

    fun loginUser(user: User) {
        viewModelScope.launch {
            val response = repository.loginUser(user)
            myResponse.value = response
        }
    }

    fun getPost() {
        viewModelScope.launch {
            val response = repository.getPost()
            myResponse.value = response
        }
    }

    fun uploadFile(body: RequestBody, trainingCSV: MultipartBody.Part,
                   roundCSV: MultipartBody.Part, endCSV: MultipartBody.Part) {
        viewModelScope.launch {
            val response = repository.uploadFile(body, trainingCSV, roundCSV, endCSV)
            fileResponse.value = response
        }
    }
}