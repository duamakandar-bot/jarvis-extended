package com.example.service

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = false)
data class IgMediaContainerResponse(
    @field:Json(name = "id") val id: String? = null
)

@JsonClass(generateAdapter = false)
data class IgPublishResponse(
    @field:Json(name = "id") val id: String? = null
)

@JsonClass(generateAdapter = false)
data class IgUserAccountResponse(
    @field:Json(name = "id") val id: String? = null,
    @field:Json(name = "username") val username: String? = null,
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "followers_count") val followersCount: Int? = null,
    @field:Json(name = "media_count") val mediaCount: Int? = null
)

interface InstagramApiService {

    @FormUrlEncoded
    @POST("{igUserId}/media")
    suspend fun createMediaContainer(
        @Path("igUserId") igUserId: String,
        @Field("image_url") imageUrl: String,
        @Field("caption") caption: String,
        @Field("access_token") accessToken: String
    ): Response<IgMediaContainerResponse>

    @FormUrlEncoded
    @POST("{igUserId}/media")
    suspend fun createReelsMediaContainer(
        @Path("igUserId") igUserId: String,
        @Field("video_url") videoUrl: String,
        @Field("caption") caption: String,
        @Field("media_type") mediaType: String = "REELS",
        @Field("access_token") accessToken: String
    ): Response<IgMediaContainerResponse>

    @FormUrlEncoded
    @POST("{igUserId}/media_publish")
    suspend fun publishMedia(
        @Path("igUserId") igUserId: String,
        @Field("creation_id") creationId: String,
        @Field("access_token") accessToken: String
    ): Response<IgPublishResponse>

    @GET("{igUserId}")
    suspend fun getAccountDetails(
        @Path("igUserId") igUserId: String,
        @Query("fields") fields: String = "id,username,name,followers_count,media_count",
        @Query("access_token") accessToken: String
    ): Response<IgUserAccountResponse>
}

object InstagramRetrofitClient {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: InstagramApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://graph.facebook.com/v19.0/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(InstagramApiService::class.java)
    }
}
