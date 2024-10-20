package com.kuro.prologger.presentation.floating_view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kuro.prologger.R
import com.kuro.prologger.presentation.theme.BackgroundColor

@Composable
fun FloatingView(
    shouldShowDetail: Boolean,
    onExitClick: () -> Unit
) {
    val context = LocalContext.current
    if (!shouldShowDetail) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(BackgroundColor, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.ic_floating_button),
                contentDescription = null
            )
        }
    } else {
        Column(
            modifier = Modifier
                .clickable {
                    Toast
                        .makeText(context, "Click!!", Toast.LENGTH_LONG)
                        .show()
                }
                .width(200.dp)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "This is a text...")
            Icon(
                modifier = Modifier.clickable { onExitClick.invoke() },
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    }

}