package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.domain.model.Photo
import javax.inject.Inject


class PhotoCacheDataSourceImpl @Inject constructor() : PhotoCacheDataSource {

    var maxMemory: Int = ((Runtime.getRuntime().maxMemory() / 1024).toInt())
    private val maxCacheSize = maxMemory / 8
    var cache = LruBitmapCachePhotos(maxCacheSize)

    var cachePhoto = LruBitmapCachePhoto(maxCacheSize)

    override suspend fun getPhotosFromCache(page: Int): List<Photo>? {
        return cache.getBitmap(page)
    }

    override suspend fun savePhotosToCache(page: Int, photos: List<Photo>) {
        if (cache.getBitmap(page) == null) {
            cache.putBitmap(page, photos)
        }
    }

    override suspend fun getPhotoFromCache(photoId: String): Photo? {
        return cachePhoto.getBitmap(photoId)
    }

    override suspend fun savePhotoToCache(photoId: String, photo: Photo) {
        if (cachePhoto.getBitmap(photoId) == null) {
            cachePhoto.putBitmap(photoId, photo)
        }
    }
}
