package com.example.unsplashphotos.data.repository

import android.util.LruCache
import com.example.unsplashphotos.data.model.Photo

class LruBitmapCache constructor(sizeInKB: Int = defaultLruCacheSize):
    LruCache<Int, List<Photo>>(sizeInKB){

    override fun sizeOf(key: Int, value: List<Photo>): Int =  1024

    fun getBitmap(page: Int): List<Photo>? {
        return get(page)
    }

    fun putBitmap(page: Int, photoList: List<Photo>) {
        put(page, photoList)
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