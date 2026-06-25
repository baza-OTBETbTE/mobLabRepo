package com.example.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos/random?count=30")
    suspend fun getPhotos(
        @Query("client_id") clientId: String
    ): List<UnsplashPhoto>

    @GET("search/photos?per_page=30")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("client_id") clientId: String
    ): UnsplashSearchResponse
}