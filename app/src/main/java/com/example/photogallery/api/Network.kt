package com.example.photogallery.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.unsplash.com/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

val api: UnsplashApi = retrofit.create(UnsplashApi::class.java)