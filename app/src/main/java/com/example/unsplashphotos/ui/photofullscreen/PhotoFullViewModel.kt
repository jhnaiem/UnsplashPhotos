package com.example.unsplashphotos.ui.photofullscreen


import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.unsplashphotos.data.model.Photo
import com.example.unsplashphotos.data.repository.DownloaderUtils
import com.example.unsplashphotos.domain.usecase.PhotoFullScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PhotoFullViewModel @Inject constructor(
    private val photoFullScreenUseCase: PhotoFullScreenUseCase, private val state: SavedStateHandle
) : ViewModel(), Observable {

    @Inject
    lateinit var downloaderUtils: DownloaderUtils
    val photoId = state.get<String>("photoId")
    private val mutableStateFlow = MutableStateFlow<Photo?>(null)
    val stateFlow = mutableStateFlow.asStateFlow()

    @Bindable
    val fabToggle = MutableLiveData<Boolean>()
    private val fabMutableStateFlow = MutableStateFlow<Boolean>(false)
    val fabStateFlow = fabMutableStateFlow.asStateFlow()


    suspend fun getPhotoById() {
        if (photoId != null)
            mutableStateFlow.value = photoFullScreenUseCase.execute(photoId)
    }

    fun onClickDownloadFab(url: String) {
        if (photoId != null)
            downloaderUtils.downloadPhoto(url, photoId)
    }

    fun onImageClicked() {
        if (fabToggle.value == true) {
            fabToggle.value = false
            fabMutableStateFlow.value = false
        } else {
            fabToggle.value = true
            fabMutableStateFlow.value = true

        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}
