package com.vng.sajja.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vng.sajja.domain.model.CollageImage

@Composable
fun ImageThumbnail(
    image: CollageImage,
    isSelected: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                2.dp,
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image.uri)
                .size(80)
                .crossfade(true)
                .build(),
            contentDescription = "Thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Opacity indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f * (1 - image.opacity)))
        )

        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        // Z-index indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${image.zIndex}",
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}
