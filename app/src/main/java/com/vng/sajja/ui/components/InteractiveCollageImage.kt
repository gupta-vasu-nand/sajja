package com.vng.sajja.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vng.sajja.domain.model.CollageImage
import com.vng.sajja.domain.model.ScaleType
import com.vng.sajja.domain.model.WallpaperSettings

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractiveCollageImage(
    image: CollageImage,
    settings: WallpaperSettings,
    isSelected: Boolean,
    containerWidth: Dp,
    containerHeight: Dp,
    onClick: () -> Unit,
    onUpdate: (CollageImage) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = (image.x * containerWidth.value).dp,
                    y = (image.y * containerHeight.value).dp
                )
                .size(
                    width = (image.width * containerWidth.value).dp,
                    height = (image.height * containerHeight.value).dp
                )
                .rotate(image.rotation)
                .pointerInput(image.uri) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = { onRemove() }
                    )
                }
                .pointerInput(image.uri) {
                    detectTransformGestures(
                        onGesture = { _, pan, gestureScale, gestureRotation ->
                            val deltaX = pan.x / size.width
                            val deltaY = pan.y / size.height

                            val newX = (image.x + deltaX).coerceIn(-0.5f, 1.5f)
                            val newY = (image.y + deltaY).coerceIn(-0.5f, 1.5f)
                            
                            val newWidth = (image.width * gestureScale).coerceIn(0.05f, 1.0f)
                            val newHeight = (image.height * gestureScale).coerceIn(0.05f, 1.0f)
                            val newRotation = (image.rotation + gestureRotation) % 360f

                            val updated = image.copy(
                                x = newX,
                                y = newY,
                                width = newWidth,
                                height = newHeight,
                                rotation = newRotation
                            )
                            onUpdate(updated)
                        }
                    )
                }
                .border(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.White.copy(alpha = image.opacity * 0.5f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(image.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Collage image",
                modifier = Modifier.fillMaxSize(),
                contentScale = when (image.scaleType) {
                    ScaleType.CENTER_CROP -> ContentScale.Crop
                    ScaleType.CENTER_INSIDE -> ContentScale.Inside
                    ScaleType.FIT_CENTER -> ContentScale.Fit
                    ScaleType.ORIGINAL -> ContentScale.None
                },
                alpha = image.opacity
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )

                // Selection rotation handle
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset((-4).dp, (-4).dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(1.dp, Color.White, CircleShape)
                        .clickable {
                            val updated = image.copy(rotation = (image.rotation + 45) % 360f)
                            onUpdate(updated)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.RotateRight,
                        contentDescription = "Rotate",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
