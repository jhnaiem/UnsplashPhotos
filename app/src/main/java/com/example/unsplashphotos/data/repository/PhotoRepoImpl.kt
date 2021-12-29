package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.data.model.Photo
import com.example.unsplashphotos.domain.repository.PhotoRepo
import timber.log.Timber
import javax.inject.Inject

class PhotoRepoImpl @Inject constructor(
    private val photoDataSource: PhotoDataSource,
    private val photoCacheDataSource: PhotoCacheDataSource
) : PhotoRepo {
    override suspend fun getPhotos(page: Int): List<Photo> {
        return getPhotosFromCache(page)
    }

    override suspend fun getPhotoById(photoId: String): Photo? {
        return getPhotoFromCache(photoId)
    }

    private suspend fun getPhotosFromCache(page: Int): List<Photo> {
        val photoList: List<Photo>
        val cachePhotos = photoCacheDataSource.getPhotosFromCache(page)
        if (cachePhotos == null) {
            photoList = getPhotosFromAPI(page)
            photoCacheDataSource.savePhotosToCache(page, photoList)
        } else {
            photoList = cachePhotos
        }
        return photoList
    }

    private suspend fun getPhotoFromCache(photoId: String): Photo? {
        val photo: Photo?
        val cachedPhoto = photoCacheDataSource.getPhotoFromCache(photoId)
        if (cachedPhoto == null) {
            photo = getPhotoFromApiById(photoId)
            if (photo != null)
                photoCacheDataSource.savePhotoToCache(photoId, photo)
        } else {
            photo = cachedPhoto
        }
        return photo
    }

    private suspend fun getPhotosFromAPI(page: Int): List<Photo> {
        var photoList = listOf<Photo>()

        try {
            val response = photoDataSource.getPhotos(page)
            val body = response.body()
            Timber.tag("===>").d("CheckPoint 1%s", body);

            if (body != null) {
                photoList = body
            }

        } catch (exception: Exception) {
            Timber.e(exception.message.toString())
        }
        return photoList
    }

    private suspend fun getPhotoFromApiById(photoId: String): Photo? {
        var photoStore: Photo? = null

        try {
            val response = photoDataSource.getPhotoById(photoId)
            val body = response.body()
            Timber.tag("T===>").i("Photo: %s", body)

            if (body != null) {
                photoStore = body
            }

        } catch (exception: Exception) {
            Timber.e(exception.message.toString())
        }

        return photoStore
    }
}
