package com.vng.sajja.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vng.sajja.settings.CollageImage
import com.vng.sajja.settings.CollageLayout
import com.vng.sajja.settings.ScaleType
import com.vng.sajja.settings.WallpaperSettings
import kotlin.math.roundToInt

@Composable
fun CollageScreen(
    settings: WallpaperSettings,
    onUpdateSettings: (WallpaperSettings) -> Unit
) {
    val context = LocalContext.current

    var showImagePicker by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<CollageImage?>(null) }
    var showLayoutOptions by remember { mutableStateOf(false) }
    var showBatchEditor by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val newImages = uris.mapIndexed { index, uri ->
            val position = index % 9
            val row = position / 3
            val col = position % 3

            CollageImage(
                uri = uri.toString(),
                x = 0.05f + col * 0.3f,
                y = 0.05f + row * 0.3f,
                width = 0.25f + (index % 3) * 0.05f,
                height = 0.25f + (index % 3) * 0.05f,
                rotation = (index * 5f) % 360f,
                zIndex = index,
                opacity = 0.8f + (index % 3) * 0.05f
            )
        }

        val updated = settings.copy(
            collageImages = settings.collageImages + newImages
        )
        onUpdateSettings(updated)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Image Collage",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${settings.collageImages.size} images",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (settings.collageImages.isEmpty()) {
                            showAddImagesToast(context)
                        } else {
                            showLayoutOptions = true
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.AutoAwesomeMosaic, contentDescription = "Layout")
                }
                IconButton(
                    onClick = {
                        if (settings.collageImages.isEmpty()) {
                            showAddImagesToast(context)
                        } else {
                            showBatchEditor = true
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Batch Edit")
                }
                FilledTonalButton(
                    onClick = { showImagePicker = true }
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Images",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Quick controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Overall Settings",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Adjust all images at once",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Collage Opacity")
                        Text("${(settings.collageOpacity * 100).roundToInt()}%")
                    }
                    Slider(
                        value = settings.collageOpacity,
                        onValueChange = {
                            onUpdateSettings(settings.copy(collageOpacity = it))
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Image Spacing")
                        Text("${settings.imageSpacing.roundToInt()}px")
                    }
                    Slider(
                        value = settings.imageSpacing,
                        onValueChange = {
                            onUpdateSettings(settings.copy(imageSpacing = it))
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Quick layout buttons
                if (settings.collageImages.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {
                                // Arrange in grid
                                val images = settings.collageImages.mapIndexed { index, image ->
                                    val row = index / 3
                                    val col = index % 3
                                    image.copy(
                                        x = 0.05f + col * 0.3f,
                                        y = 0.05f + row * 0.3f,
                                        width = 0.25f,
                                        height = 0.25f,
                                        rotation = 0f
                                    )
                                }
                                onUpdateSettings(settings.copy(collageImages = images))
                            },
                            label = { Text("Grid") }
                        )
                        AssistChip(
                            onClick = {
                                // Arrange in circle
                                val images = settings.collageImages.mapIndexed { index, image ->
                                    val total = settings.collageImages.size
                                    val angle = (index * 360f / total) * (Math.PI / 180).toFloat()
                                    val radius = 0.3f
                                    image.copy(
                                        x = 0.5f + radius * kotlin.math.cos(angle) - image.width / 2,
                                        y = 0.5f + radius * kotlin.math.sin(angle) - image.height / 2,
                                        rotation = angle * (180 / Math.PI).toFloat()
                                    )
                                }
                                onUpdateSettings(settings.copy(collageImages = images))
                            },
                            label = { Text("Circle") }
                        )
                        AssistChip(
                            onClick = {
                                // Random arrangement
                                val images = settings.collageImages.map { image ->
                                    image.copy(
                                        x = (0..70).random() / 100f,
                                        y = (0..70).random() / 100f,
                                        rotation = (0..360).random().toFloat(),
                                        opacity = (50..100).random() / 100f
                                    )
                                }
                                onUpdateSettings(settings.copy(collageImages = images))
                            },
                            label = { Text("Random") }
                        )
                    }
                }
            }
        }

        // Collage preview/editor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Color(settings.backgroundColor),
                    RoundedCornerShape(16.dp)
                )
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (settings.collageImages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No images in collage",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap 'Add Images' to create your collage",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = { showImagePicker = true }
                    ) {
                        Text("Select Photos")
                    }
                }
            } else {
                // Draw collage preview with interactivity
                settings.collageImages.sortedBy { it.zIndex }.forEach { image ->
                    InteractiveCollageImage(
                        image = image,
                        settings = settings,
                        isSelected = selectedImage?.uri == image.uri,
                        onClick = { selectedImage = image },
                        onUpdate = { updatedImage ->
                            val updatedList = settings.collageImages.map {
                                if (it.uri == updatedImage.uri) updatedImage else it
                            }
                            onUpdateSettings(settings.copy(collageImages = updatedList))
                        },
                        onRemove = {
                            val updatedList = settings.collageImages.filter { it.uri != image.uri }
                            onUpdateSettings(settings.copy(collageImages = updatedList))
                            if (selectedImage?.uri == image.uri) {
                                selectedImage = null
                            }
                        }
                    )
                }
            }
        }

        // Image list for quick selection
        if (settings.collageImages.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Images (${settings.collageImages.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                if (settings.collageImages.isEmpty()) {
                                    showAddImagesToast(context)
                                } else {
                                    onUpdateSettings(settings.copy(collageImages = emptyList()))
                                    selectedImage = null
                                }
                            }
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
                        }
                    }

                    // Horizontal scrollable image list
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        settings.collageImages.forEach { image ->
                            ImageThumbnail(
                                image = image,
                                isSelected = selectedImage?.uri == image.uri,
                                onClick = { selectedImage = image },
                                onRemove = {
                                    val updatedList = settings.collageImages.filter { it.uri != image.uri }
                                    onUpdateSettings(settings.copy(collageImages = updatedList))
                                }
                            )
                        }
                    }
                }
            }
        }

        // Selected image controls
        selectedImage?.let { image ->
            AnimatedVisibility(
                visible = selectedImage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Edit Image",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        // Bring to front
                                        val maxZ = settings.collageImages.maxOfOrNull { it.zIndex } ?: 0
                                        val updated = image.copy(zIndex = maxZ + 1)
                                        updateImage(updated, settings, onUpdateSettings)
                                        selectedImage = updated
                                    }
                                ) {
                                    Icon(Icons.Default.Layers, contentDescription = "To Front")
                                }
                                IconButton(
                                    onClick = {
                                        val updatedList = settings.collageImages.filter { it.uri != image.uri }
                                        onUpdateSettings(settings.copy(collageImages = updatedList))
                                        selectedImage = null
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }

                        // Basic controls
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Opacity")
                                Text("${(image.opacity * 100).roundToInt()}%")
                            }
                            Slider(
                                value = image.opacity,
                                onValueChange = {
                                    val updated = image.copy(opacity = it)
                                    updateImage(updated, settings, onUpdateSettings)
                                    selectedImage = updated
                                },
                                valueRange = 0f..1f
                            )
                        }

                        // Size controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Width", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = image.width,
                                    onValueChange = {
                                        val updated = image.copy(width = it)
                                        updateImage(updated, settings, onUpdateSettings)
                                        selectedImage = updated
                                    },
                                    valueRange = 0.1f..0.8f
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Height", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = image.height,
                                    onValueChange = {
                                        val updated = image.copy(height = it)
                                        updateImage(updated, settings, onUpdateSettings)
                                        selectedImage = updated
                                    },
                                    valueRange = 0.1f..0.8f
                                )
                            }
                        }

                        // Scale type selector
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Scale Type", style = MaterialTheme.typography.bodyMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ScaleType.entries.forEach { scaleType ->
                                    FilterChip(
                                        selected = image.scaleType == scaleType,
                                        onClick = {
                                            val updated = image.copy(scaleType = scaleType)
                                            updateImage(updated, settings, onUpdateSettings)
                                            selectedImage = updated
                                        },
                                        label = { Text(scaleType.name.replace('_', ' '), maxLines = 2, overflow = TextOverflow.Ellipsis) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Quick actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    val updated = image.copy(rotation = (image.rotation + 90) % 360)
                                    updateImage(updated, settings, onUpdateSettings)
                                    selectedImage = updated
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Rotate90DegreesCcw, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rotate 90°")
                            }
                            FilledTonalButton(
                                onClick = {
                                    val updated = image.copy(opacity = if (image.opacity > 0.5f) 0.3f else 1f)
                                    updateImage(updated, settings, onUpdateSettings)
                                    selectedImage = updated
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Toggle Dim")
                            }
                        }
                    }
                }
            }
        }
    }

    // Layout options dialog
    if (showLayoutOptions) {
        AlertDialog(
            onDismissRequest = { showLayoutOptions = false },
            title = { Text("Collage Layout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CollageLayout.entries.forEach { layout ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings(settings.copy(collageLayout = layout))
                                    applyLayout(layout, settings, onUpdateSettings)
                                    showLayoutOptions = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (settings.collageLayout == layout)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    when (layout) {
                                        CollageLayout.GRID -> Icons.Default.GridView
                                        CollageLayout.MASONRY -> Icons.Default.ViewDay
                                        CollageLayout.CENTER_FOCUS -> Icons.Default.CenterFocusStrong
                                        CollageLayout.SPIRAL -> Icons.Default.CompareArrows
                                        CollageLayout.RANDOM -> Icons.Default.Shuffle
                                    },
                                    contentDescription = null
                                )
                                Column {
                                    Text(layout.name.replace('_', ' '))
                                    Text(
                                        when (layout) {
                                            CollageLayout.GRID -> "Regular grid arrangement"
                                            CollageLayout.MASONRY -> "Pinterest-style layout"
                                            CollageLayout.CENTER_FOCUS -> "One large center image"
                                            CollageLayout.SPIRAL -> "Spiral artistic layout"
                                            CollageLayout.RANDOM -> "Random artistic placement"
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLayoutOptions = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Batch editor dialog
    if (showBatchEditor) {
        AlertDialog(
            onDismissRequest = { showBatchEditor = false },
            title = { Text("Batch Edit All Images") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Opacity for all images")
                        Slider(
                            value = settings.collageImages.map { it.opacity }.average().toFloat(),
                            onValueChange = { newOpacity ->
                                val updatedImages = settings.collageImages.map { it.copy(opacity = newOpacity) }
                                onUpdateSettings(settings.copy(collageImages = updatedImages))
                            },
                            valueRange = 0f..1f
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Scale Type for all images")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScaleType.entries.forEach { scaleType ->
                                FilterChip(
                                    selected = settings.collageImages.all { it.scaleType == scaleType },
                                    onClick = {
                                        val updatedImages = settings.collageImages.map { it.copy(scaleType = scaleType) }
                                        onUpdateSettings(settings.copy(collageImages = updatedImages))
                                    },
                                    label = { Text(scaleType.name.first().toString()) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBatchEditor = false }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchEditor = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Trigger image picker
    if (showImagePicker) {
        LaunchedEffect(showImagePicker) {
            imagePicker.launch("image/*")
            showImagePicker = false
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InteractiveCollageImage(
    image: CollageImage,
    settings: WallpaperSettings,
    isSelected: Boolean,
    onClick: () -> Unit,
    onUpdate: (CollageImage) -> Unit,
    onRemove: () -> Unit
) {
    var offsetX by remember { mutableStateOf(image.x) }
    var offsetY by remember { mutableStateOf(image.y) }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(image.rotation) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { centroid, pan, gestureScale, gestureRotation ->
                        val newScale = (scale * gestureScale).coerceIn(0.5f, 3f)
                        val newRotation = (rotation + gestureRotation) % 360

                        // Calculate position change
                        val deltaX = pan.x / 400f // 400 is preview width
                        val deltaY = pan.y / 400f // 400 is preview height

                        val newX = (offsetX + deltaX).coerceIn(0f, 1f - image.width * newScale)
                        val newY = (offsetY + deltaY).coerceIn(0f, 1f - image.height * newScale)

                        if (newX != offsetX || newY != offsetY || newScale != scale || newRotation != rotation) {
                            offsetX = newX
                            offsetY = newY
                            scale = newScale
                            rotation = newRotation

                            val updated = image.copy(
                                x = offsetX,
                                y = offsetY,
                                width = image.width * scale,
                                height = image.height * scale,
                                rotation = rotation
                            )
                            onUpdate(updated)
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = (offsetX * 400).dp,
                    y = (offsetY * 400).dp
                )
                .size(
                    width = (image.width * 400 * scale).dp,
                    height = (image.height * 400 * scale).dp
                )
                .rotate(rotation)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onRemove
                )
                .border(
                    if (isSelected) 3.dp else 1.dp,
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

                // Selection handles
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset((-8).dp, (-8).dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable {
                            val updated = image.copy(rotation = (image.rotation + 45) % 360)
                            onUpdate(updated)
                        }
                ) {
                    Icon(
                        Icons.Default.RotateRight,
                        contentDescription = "Rotate",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnail(
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

private fun updateImage(
    updated: CollageImage,
    settings: WallpaperSettings,
    onUpdate: (WallpaperSettings) -> Unit
) {
    val updatedList = settings.collageImages.map {
        if (it.uri == updated.uri) updated else it
    }
    onUpdate(settings.copy(collageImages = updatedList))
}

private fun applyLayout(
    layout: CollageLayout,
    settings: WallpaperSettings,
    onUpdate: (WallpaperSettings) -> Unit
) {
    val images = settings.collageImages.mapIndexed { index, image ->
        when (layout) {
            CollageLayout.GRID -> {
                val row = index / 3
                val col = index % 3
                image.copy(
                    x = 0.05f + col * 0.3f,
                    y = 0.05f + row * 0.3f,
                    width = 0.25f,
                    height = 0.25f,
                    rotation = 0f
                )
            }
            CollageLayout.MASONRY -> {
                val positions = listOf(
                    Pair(0.05f, 0.05f) to Pair(0.45f, 0.25f),
                    Pair(0.55f, 0.05f) to Pair(0.4f, 0.35f),
                    Pair(0.05f, 0.35f) to Pair(0.35f, 0.3f),
                    Pair(0.45f, 0.45f) to Pair(0.5f, 0.25f),
                    Pair(0.05f, 0.7f) to Pair(0.4f, 0.25f)
                )
                val posIndex = index % positions.size
                val (pos, size) = positions[posIndex]
                image.copy(
                    x = pos.first,
                    y = pos.second,
                    width = size.first,
                    height = size.second,
                    rotation = (index * 5f) % 15f
                )
            }
            CollageLayout.CENTER_FOCUS -> {
                if (index == 0) {
                    image.copy(
                        x = 0.25f,
                        y = 0.25f,
                        width = 0.5f,
                        height = 0.5f,
                        rotation = 0f
                    )
                } else {
                    val angle = ((index - 1) * 360f / (settings.collageImages.size - 1)) * (Math.PI / 180).toFloat()
                    val radius = 0.35f
                    image.copy(
                        x = 0.5f + radius * kotlin.math.cos(angle) - 0.1f,
                        y = 0.5f + radius * kotlin.math.sin(angle) - 0.1f,
                        width = 0.2f,
                        height = 0.2f,
                        rotation = angle * (180 / Math.PI).toFloat()
                    )
                }
            }
            CollageLayout.SPIRAL -> {
                val angle = index * 0.5f
                val radius = 0.1f + index * 0.03f
                image.copy(
                    x = 0.5f + radius * kotlin.math.cos(angle) - 0.1f,
                    y = 0.5f + radius * kotlin.math.sin(angle) - 0.1f,
                    width = 0.2f - index * 0.01f,
                    height = 0.2f - index * 0.01f,
                    rotation = angle * (180 / Math.PI).toFloat() * 2
                )
            }
            CollageLayout.RANDOM -> {
                image.copy(
                    x = (0..70).random() / 100f,
                    y = (0..70).random() / 100f,
                    width = 0.15f + (0..15).random() / 100f,
                    height = 0.15f + (0..15).random() / 100f,
                    rotation = (0..360).random().toFloat(),
                    opacity = 0.6f + (0..40).random() / 100f
                )
            }
        }
    }
    onUpdate(settings.copy(collageImages = images))
}

private fun showAddImagesToast(context: android.content.Context) {
    android.widget.Toast
        .makeText(context, "Add images first", android.widget.Toast.LENGTH_SHORT)
        .show()
}
