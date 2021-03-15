package de.dreier.mytargets.features.settings.migrate.repository

import de.dreier.mytargets.features.settings.migrate.api.RetrofitInstance
import de.dreier.mytargets.features.settings.migrate.model.Post
import retrofit2.Response


class Repository {

    suspend fun getPost(): Response<Post> {
        return RetrofitInstance.api.getPost()
    }

}