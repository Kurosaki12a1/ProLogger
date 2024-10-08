package com.kuro.prologger.presentation.floating_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kuro.prologger.R
import com.kuro.prologger.presentation.theme.BackgroundColor

@Composable
fun FloatingView(
    shouldShowLog: Boolean
) {
    if (!shouldShowLog) {
        Box(
            modifier = Modifier
                .background(BackgroundColor, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit,
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
        }
    } else {
        Column(
            modifier = Modifier
                .width(200.dp)
                .padding(10.dp)
        ) {
            Text(text = "Texttttttttttttt")
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    }

}