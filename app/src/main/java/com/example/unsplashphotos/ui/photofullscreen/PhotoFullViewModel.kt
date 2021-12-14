package com.example.unsplashphotos.ui.photofullscreen


import androidx.lifecycle.ViewModel
import com.example.unsplashphotos.data.model.Photo
import com.example.unsplashphotos.domain.usecase.PhotoFullScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PhotoFullViewModel @Inject constructor(private val photoFullScreenUseCase: PhotoFullScreenUseCase) :
    ViewModel() {

    private val mutableStateFlow = MutableStateFlow<Photo?>(null)
    val stateFlow = mutableStateFlow.asStateFlow()

    suspend fun getPhotoById(id: String) {
        mutableStateFlow.value =photoFullScreenUseCase.execute(id)
    }
}
