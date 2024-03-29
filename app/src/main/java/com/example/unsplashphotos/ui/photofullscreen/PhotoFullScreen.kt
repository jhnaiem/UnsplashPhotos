package com.example.unsplashphotos.ui.photofullscreen

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest.Builder
import com.example.unsplashphotos.R
import com.example.unsplashphotos.domain.model.Photo
import com.example.unsplashphotos.ui.AppBar
import com.example.unsplashphotos.ui.theme.Bittersweet
import com.example.unsplashphotos.ui.theme.UnsplashTheme
import com.example.unsplashphotos.utils.DataState.Error
import com.example.unsplashphotos.utils.DataState.Loading
import com.example.unsplashphotos.utils.DataState.Success
import com.example.unsplashphotos.utils.ExternalStoragePermissionTextProvider
import com.example.unsplashphotos.utils.PermissionDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.jhasan.bitmapdrawablesharelib.BitmapDrawableSharer
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoFullView(
    modifier: Modifier = Modifier,
    onShareClicked: () -> Unit,
    photo: Photo,
) {
    val context = LocalContext.current.applicationContext
    val viewModel = hiltViewModel<PhotoFullViewModel>()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val requiredPermissionsState =
        rememberPermissionState(permission = permission.WRITE_EXTERNAL_STORAGE)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val storagePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                viewModel.onPermissionResult(
                    permission = permission.WRITE_EXTERNAL_STORAGE,
                )
            } else if (isGranted) {
                viewModel.onClickDownloadFab(photo.links.download, photo.id)
            }
        },
    )

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        ExternalStoragePermissionTextProvider()
                    }

                    else -> return@forEach
                },
                isPermanentlyDeclined = !requiredPermissionsState.shouldShowRationale,
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    viewModel.dismissDialog()
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                onGoToAppSettingsClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
            )
        }

    UnsplashTheme {
        val snackbarHostState = scaffoldState.snackbarHostState
        Scaffold(topBar = {
            AppBar(
                Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
            )
        }, scaffoldState = scaffoldState, floatingActionButton = {
            FloatingActionButtons(
                onShareClicked,
                storagePermissionResultLauncher,
                requiredPermissionsState,
                photo,
            )
        }) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                photoItem(
                    photoUrl = photo.urls.regular,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    viewModel
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 20.dp, start = 5.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                FloatingActionButtonInfo(onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Likes: ${photo.likes}",
                            actionLabel = null,
                        )
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun FloatingActionButtons(
    onShareClicked: () -> Unit,
    storagePermissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>,
    requiredPermissionsState: PermissionState,
    photo: Photo,
) {
    val viewModel = hiltViewModel<PhotoFullViewModel>()

    Column() {
        FloatingActionButtonShare(onShareClicked = onShareClicked)
        Spacer(modifier = Modifier.height(8.dp))
        FloatingActionButtonDownload(onDownloadClicked = {
            if (requiredPermissionsState.shouldShowRationale) {
                viewModel.onPermissionResult(
                    permission = permission.WRITE_EXTERNAL_STORAGE,
                )
            } else {
                storagePermissionResultLauncher.launch(
                    permission.WRITE_EXTERNAL_STORAGE,
                )
            }
        })
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator(color = Bittersweet.copy(alpha = .5f))
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun PhotoFullScreen(
    photo: Photo,
) {
    val viewModel = hiltViewModel<PhotoFullViewModel>()
    viewModel.getPhotoById(photoId = photo.id)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (uiState) {
        is Error -> {}
        is Loading -> {
            FullScreenLoading()
        }

        is Success -> {
            PhotoFullView(
                Modifier,
                onShareClicked = {
                    viewModel.bitmapDrawable.value?.let {
                        BitmapDrawableSharer.shareImage(
                            context = context,
                            photoId = photo.id,
                            it,
                        )
                    }
                },
                (uiState as Success<Photo>).data,
            )
        }

        else -> {}
    }
}

@Composable
fun FloatingActionButtonShare(
    onShareClicked: () -> Unit = {},
    containerColor: Color = Bittersweet,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
) {
    FloatingActionButton(
        onClick = onShareClicked,
        shape = shape,
        backgroundColor = containerColor,
    ) {
        Icon(
            imageVector = Rounded.Share,
            contentDescription = "Share photo",
            tint = Color.Black,
        )
    }
}

@Composable
fun FloatingActionButtonInfo(
    onClick: () -> Unit = {},
    containerColor: Color = Bittersweet,
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
) {
    FloatingActionButton(
        modifier = Modifier.size(40.dp),
        onClick = onClick,
        shape = shape,
        backgroundColor = containerColor,
    ) {
        Icon(
            imageVector = Rounded.Info,
            contentDescription = "Information",
            tint = Color.Black,
        )
    }
}

@Composable
fun FloatingActionButtonDownload(
    onDownloadClicked: () -> Unit = {},
    containerColor: Color = Bittersweet,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
) {
    FloatingActionButton(
        onClick = onDownloadClicked,
        shape = shape,
        backgroundColor = containerColor,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_baseline_arrow_circle_down_24),
            contentDescription = "Download",
            tint = Color.Black,
        )
    }
}

@Composable
fun photoItem(
    photoUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    viewModel: PhotoFullViewModel,
) {
    val painter = rememberAsyncImagePainter(
        Builder(LocalContext.current)
            .data(photoUrl)
            .build(),
    )
    Image(
        contentScale = contentScale,
        painter = painter,
        contentDescription = null,
        modifier = modifier,
    )
    val state = painter.state as? AsyncImagePainter.State.Success
    val drawable = state?.result?.drawable
    drawable?.let {
        viewModel.bitmapDrawable.value = it as BitmapDrawable
    }
}

@Preview("Photo item")
@Composable
private fun PhotoFullScreenPreview() {
    UnsplashTheme {
//        PhotoFullScreen(Modifier, Photo("", "", Urls.EMPTY, Links.EMPTY, 0))
    }
}
