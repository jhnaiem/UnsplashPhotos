package com.example.unsplashphotos.domain.repository

import com.example.unsplashphotos.utils.Resource
import com.example.unsplashphotos.data.model.Photo


interface PhotoRepo {
    suspend fun getPhotos(page: Int): List<Photo>?
    suspend fun getPhotoById(photoId: String): Resource<Photo>
}
