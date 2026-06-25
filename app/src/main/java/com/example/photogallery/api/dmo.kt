package com.example.photogallery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashPhoto(
    @Json(name = "id") val id: String,
    @Json(name = "urls") val urls: UnsplashUrls
)

@JsonClass(generateAdapter = true)
data class UnsplashUrls(
    @Json(name = "regular") val regular: String,
    @Json(name = "small") val small: String
)

@JsonClass(generateAdapter = true)
data class UnsplashSearchResponse(
    @Json(name = "results") val results: List<UnsplashPhoto>
)