package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.data.model.PhotoRemoteEntity
import retrofit2.Response

interface PhotoDataSource {
    suspend fun getPhotos(page: Int, perPage: Int): Response<List<PhotoRemoteEntity>>
    suspend fun getPhotoById(id: String): Response<PhotoRemoteEntity>
}

