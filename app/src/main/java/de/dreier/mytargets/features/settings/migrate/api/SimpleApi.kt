package de.dreier.mytargets.features.settings.migrate.api

import de.dreier.mytargets.features.settings.migrate.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SimpleApi {
    @GET("posts/1")
    suspend fun getPost(): Response<User>

    @POST("create-user")
    suspend fun createUser(
            @Body post: User
    ): Response<User>

    @POST("verify")
    suspend fun loginUser(
            @Body post: User
    ): Response<User>
}