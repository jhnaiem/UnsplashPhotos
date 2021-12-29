package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.data.model.Photo
import retrofit2.Response


interface PhotoDataSource {
    suspend fun getPhotos(page: Int): Response<List<Photo>>
    suspend fun getPhotoById(id: String): Response<Photo>
}

