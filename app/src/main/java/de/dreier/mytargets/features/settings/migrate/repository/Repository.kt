package de.dreier.mytargets.features.settings.migrate.repository

import de.dreier.mytargets.features.settings.migrate.api.RetrofitInstance
import de.dreier.mytargets.features.settings.migrate.model.User
import retrofit2.Response


class Repository {

    suspend fun getPost(): Response<User> {
        return RetrofitInstance.api.getPost()
    }

    suspend fun pushPost(user: User): Response<User> {
        return RetrofitInstance.api.pushPost(user)
    }

}