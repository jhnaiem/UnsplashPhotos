package com.example.unsplashphotos.ui.photofullscreen


import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.preference.PreferenceManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.unsplashphotos.data.model.Photo
import com.example.unsplashphotos.data.repository.DownloaderUtils
import com.example.unsplashphotos.domain.usecase.PhotoFullScreenUseCase
import com.example.unsplashphotos.ui.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PhotoFullViewModel @Inject constructor(
    private val photoFullScreenUseCase: PhotoFullScreenUseCase, private val state: SavedStateHandle
) : ViewModel() {

    @Inject
    lateinit var downloaderUtils: DownloaderUtils
    val photoId = state.get<String>("photoId")
    private val mutableStateFlow = MutableStateFlow<Photo?>(null)
    val stateFlow = mutableStateFlow.asStateFlow()

    suspend fun getPhotoById() {
        if (photoId != null)
            mutableStateFlow.value = photoFullScreenUseCase.execute(photoId)
    }

    fun onClickDownloadFab(url: String) {
        if (photoId != null)
            downloaderUtils.downloadPhoto(url, photoId)
    }
}
