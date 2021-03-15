package de.dreier.mytargets.features.settings.migrate.api

import de.dreier.mytargets.features.settings.migrate.model.Post
import retrofit2.Response
import retrofit2.http.GET

interface SimpleApi {
    @GET("posts/1")
    suspend fun getPost(): Response<Post>
}