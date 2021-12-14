package com.example.unsplashphotos.ui.photofullscreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.unsplashphotos.R
import com.example.unsplashphotos.common.ImageLoader
import com.example.unsplashphotos.data.model.Photo
import com.example.unsplashphotos.data.repository.DownloaderUtils
import com.example.unsplashphotos.databinding.FragmentPhotoFullScreenBinding
import com.example.unsplashphotos.ui.ShareUtils.Companion.shareImage
import com.example.unsplashphotos.utils.Resource
import com.example.unsplashphotos.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PhotoFullScreenFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader
    @Inject
    lateinit var downloaderUtils: DownloaderUtils
    private lateinit var binding: FragmentPhotoFullScreenBinding
    private val photoFullViewModel by viewModels<PhotoFullViewModel>()
    private lateinit var photoId: String
    private lateinit var imageView: ImageView
    private lateinit var downloadLink: String
    private lateinit var shareHtmlLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoId = arguments?.getString("photoId").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoFullScreenBinding.inflate(inflater)
        imageView = binding.imgPhoto
        binding.saveFab.setOnClickListener {
            activityResultLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        binding.shareFab.setOnClickListener {
            shareImage(requireContext(), photoId, imageView)
        }
        return binding.root
    }

    private fun sharePhotoLink() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/html"
        share.putExtra(Intent.EXTRA_TEXT, shareHtmlLink)
        startActivity(Intent.createChooser(share, getString(R.string.sharevia)))
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Handle Permission granted/rejected
            if (isGranted) {
                downloaderUtils.downloadPhoto(downloadLink, photoId)
            } else {
                Toast.makeText(activity, getString(R.string.storageperm), Toast.LENGTH_SHORT).show()
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayPhoto()
    }

    private fun displayPhoto() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            photoFullViewModel.getPhotoById(photoId)
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoFullViewModel.stateFlow.collectLatest {
                    handlePhotoFullResult(it)
                }
            }
        }
    }

    private fun handlePhotoFullResult(it: Resource<Photo>?) {
        when (it?.status) {
            Status.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.show()
            }
            Status.SUCCESS -> {
                binding.progressBar.visibility = View.INVISIBLE
                binding.photoFullScreen = it.data
                it.data?.urls?.let { it1 ->
                    loadPhoto(it1.regular)
                    downloadLink = it.data.links.download
                }
                binding.progressBar.hide()
            }
            Status.ERROR -> {
                binding.progressBar.visibility = View.INVISIBLE
                binding.textTitle.text = getString(R.string.no_data)
                Toast.makeText(activity, getString(R.string.no_data), Toast.LENGTH_LONG)
                    .show()
                binding.progressBar.hide()
            }
            else -> {}
        }
    }

    private fun loadPhoto(url: String) {
        imageLoader.load(url, binding.imgPhoto)
    }
}
