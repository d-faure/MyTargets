package de.dreier.mytargets.features.settings.migrate.repository

import de.dreier.mytargets.features.settings.migrate.api.RetrofitInstance
import de.dreier.mytargets.features.settings.migrate.model.FileResponse
import de.dreier.mytargets.features.settings.migrate.model.User
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody


class Repository {

    suspend fun getPost(): Response<User> {
        return RetrofitInstance.api.getPost()
    }

    suspend fun createUser(user: User): Response<User> {
        return RetrofitInstance.api.createUser(user)
    }

    suspend fun loginUser(user: User): Response<User> {
        return RetrofitInstance.api.loginUser(user)
    }

    suspend fun uploadFile(userID: RequestBody, fullName: RequestBody, image: MultipartBody.Part): Response<FileResponse>  {
        return RetrofitInstance.api.uploadFile(userID, fullName, image)
    }
}