package com.kuro.prologger.presentation.splash

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kuro.prologger.R
import com.kuro.prologger.navigation.Route
import com.kuro.prologger.presentation.theme.BackgroundColor
import com.kuro.prologger.presentation.theme.GreenColor
import com.kuro.prologger.util.RequestExternalStoragePermission
import com.kuro.prologger.util.RequestMultiplePermissions

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.splash)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    val isTimerFinished by viewModel.isTimerFinished.collectAsState()

    RequestMultiplePermissions(
        permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    ) {
        viewModel.checkPermissions()
    }

    RequestExternalStoragePermission(context, {
        viewModel.checkPermissions()
    })

    LaunchedEffect(isTimerFinished) {
        if (isTimerFinished) {
            viewModel.navigateTo(
                route = Route.Home,
                popUpToRoute = Route.Splash,
                inclusive = true,
                isSingleTop = true
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        LottieAnimation(
            modifier = Modifier.align(Alignment.Center),
            progress = { progress },
            composition = composition
        )
        CircularProgressIndicator(
            color = GreenColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
        )
    }
}