package com.shrikantbadwaik.tmdb.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

// Access Token for Bearer authentication
private const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZjQxMzc0ODFjZWIxMmI5YjA2NWVlMjIxOTEzMGNmNSIsIm5iZiI6MTc2MjYwNDM1Mi4yNTcsInN1YiI6IjY5MGYzNTQwYTRhMWM5NmNjODMxYWE2ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.lxgiBVGpqH1zvHGskabyjiUemhjVODG3jNHfBaQiY1Y"

interface TMDbApi {
    @GET("movie/popular")
    @Headers("accept: application/json")
    suspend fun getPopularMovies(
        @Header("Authorization") authorization: String = "Bearer $ACCESS_TOKEN",
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): PopularResponse
}

data class PopularResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<MovieDto>
)

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?
) {
    fun fullPosterUrl(): String? = posterPath?.let { "$IMAGE_BASE_URL$it" }
}

object TMDbService {
    val api: TMDbApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDbApi::class.java)
    }
}


