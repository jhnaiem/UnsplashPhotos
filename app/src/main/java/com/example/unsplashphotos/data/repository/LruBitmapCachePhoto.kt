package com.example.unsplashphotos.data.repository

import android.util.LruCache
import com.example.unsplashphotos.domain.model.Photo


class LruBitmapCachePhoto constructor(sizeInKB: Int = defaultLruCacheSize):
    LruCache<String, Photo>(sizeInKB){

    override fun sizeOf(key: String, value: Photo): Int =  1024 * 2

    fun getBitmap(photoId: String): Photo? {
        return get(photoId)
    }

    fun putBitmap(photoId: String, photo: Photo) {
        put(photoId, photo)
    }

    companion object {
        val defaultLruCacheSize: Int
            get() {
                val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
                val cacheSize = maxMemory / 8
                return cacheSize
            }
    }
}
