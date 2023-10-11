package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.data.model.PhotoRemoteToPhotoMapperImpl
import com.example.unsplashphotos.domain.model.Photo
import com.example.unsplashphotos.domain.repository.PhotoRepo
import com.example.unsplashphotos.utils.DataState
import timber.log.Timber
import javax.inject.Inject

class PhotoRepoImpl @Inject constructor(
    private val photoDataSource: PhotoDataSource,
    private val photoRemoteToPhotoMapper: PhotoRemoteToPhotoMapperImpl,
    private val photoCacheDataSource: PhotoCacheDataSource
) : PhotoRepo {
    override suspend fun getPhotos(page: Int, perPage: Int): DataState<List<Photo>> {
        val fetchedList = getPhotosFromCache(page, perPage)

        return if (fetchedList.isNotEmpty()) {
            DataState.Success(fetchedList)
        } else {
            DataState.Error(Exception("Unknown error"))
        }
    }

    override suspend fun getPhotoById(photoId: String): DataState<Photo> {
        val fetchedPhoto = getPhotoFromCache(photoId)
        return if (fetchedPhoto != null) {
            DataState.Success(fetchedPhoto)
        } else {
            DataState.Error(Exception("Photo fetching failed"))
        val response = photoDataSource.getPhotoById(photoId)
        val body = response.body()
        Timber.tag("T===>").i("Photo: %s", body)
        return if (body != null) {
            DataState.Success(photoRemoteToPhotoMapper.mapFromEntity(body))
        } else {
            DataState.Error(Exception(response.message()))
        }
    }

    private suspend fun getPhotosFromCache(page: Int, perPage: Int): List<Photo> {
        val photoList: List<Photo>
        val cachePhotos = photoCacheDataSource.getPhotosFromCache(page)
        if (cachePhotos == null) {
            photoList = getPhotosFromAPI(page, perPage)
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

    private suspend fun getPhotosFromAPI(page: Int, perPage: Int): List<Photo> {
        var photoList = listOf<Photo>()

        try {
            val response = photoDataSource.getPhotos(page, perPage)
            val body = response.body()
            Timber.tag("===>").d("CheckPoint 1%s", body);

            if (body != null) {
                photoList = body.map { photoRemoteToPhotoMapper.mapFromEntity(it) }
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
                photoStore = photoRemoteToPhotoMapper.mapFromEntity(body)
            }
        } catch (exception: Exception) {
            Timber.e(exception.message.toString())
        }
        return photoStore
    }
}
