package de.dreier.mytargets.features.settings.migrate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.dreier.mytargets.features.settings.migrate.model.User
import de.dreier.mytargets.features.settings.migrate.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody


class MainViewModel(private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Response<User>> = MutableLiveData()

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

    fun uploadFile(userID: RequestBody, fullName: RequestBody, image: MultipartBody.Part) {
        viewModelScope.launch {
            val response = repository.uploadFile(userID, fullName, image)
            myResponse.value = response
        }
    }
}