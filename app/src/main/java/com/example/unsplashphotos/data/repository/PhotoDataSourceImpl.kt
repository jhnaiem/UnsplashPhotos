package com.example.unsplashphotos.data.repository

import com.example.unsplashphotos.BuildConfig.CLIENT_ID
import com.example.unsplashphotos.data.api.PhotoRemoteDataSource
import com.example.unsplashphotos.data.model.Photo
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject


class PhotoDataSourceImpl @Inject constructor(
    private val photoRemoteDataSource: PhotoRemoteDataSource
) : PhotoDataSource {

    override suspend fun getPhotos(page: Int): Response<List<Photo>> {
        val store = photoRemoteDataSource.getPhotos(CLIENT_ID, page)
        Timber.tag("===>").d("photos: %s", store)
        return store
    }

    override suspend fun getPhotoById(photoId: String): Response<Photo> {
        val photo = photoRemoteDataSource.getPhotoById(photoId, CLIENT_ID)
        Timber.d("CheckPhoto: %s", photo)
        return photo
    }

}

