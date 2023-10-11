package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.domain.model.Photo


interface PhotoCacheDataSource {

    suspend fun getPhotosFromCache(page: Int):List<Photo>?
    suspend fun savePhotosToCache(page: Int, photos:List<Photo>)
    suspend fun getPhotoFromCache(photoId: String):Photo?
    suspend fun savePhotoToCache(photoId: String, photo:Photo)
}
