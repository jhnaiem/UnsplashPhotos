package com.example.unsplashphotos.di

import com.example.unsplashphotos.data.repository.*
import com.example.unsplashphotos.domain.repository.PhotoRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhotoDataSource(photoDataSourceImpl: PhotoDataSourceImpl): PhotoDataSource

    @Binds
    @Singleton
    abstract fun bindPhotoCacheDataSource(photoCacheDataSourceImpl: PhotoCacheDataSourceImpl): PhotoCacheDataSource

    @Singleton
    @Binds
    abstract fun getPhotoRepository(photoRepoImpl: PhotoRepoImpl): PhotoRepo
}

