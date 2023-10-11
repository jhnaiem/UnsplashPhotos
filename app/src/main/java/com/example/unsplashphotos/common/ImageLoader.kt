package com.example.unsplashphotos.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ImageLoader(private val context: Context) {

    fun load(
        url: String,
        imageView: ImageView,
    ) {
        GlideApp.with(context)
            .load(url)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    fun load(
        bitmap: Bitmap,
        imageView: ImageView,
        placeholder: Int,
    ) {
        Glide.with(context)
            .load(bitmap)
            .override(imageView.width)
            .placeholder(placeholder)
            .into(imageView)
    }

    fun load(
        drawable: Int,
        imageView: ImageView,
    ) {
        Glide.with(context)
            .load(drawable)
            .fitCenter()
            .into(imageView)
    }

    fun load(
        drawable: Drawable,
        imageView: ImageView,
    ) {
        Glide.with(context)
            .load(drawable)
            .into(imageView)
    }
}
