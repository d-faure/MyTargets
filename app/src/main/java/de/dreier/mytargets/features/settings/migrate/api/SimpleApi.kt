package de.dreier.mytargets.features.settings.migrate.api

import de.dreier.mytargets.features.settings.migrate.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


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

    @Multipart
    @POST("mytargets/upload")
    suspend fun uploadFile(
            @Part("user_id") id: RequestBody,
            @Part("full_name") fullName: RequestBody,
            @Part image: MultipartBody.Part
    ): Observable<ResponseBody>
}