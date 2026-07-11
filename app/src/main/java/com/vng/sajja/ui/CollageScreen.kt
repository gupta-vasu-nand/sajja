package com.vng.sajja.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vng.sajja.domain.model.BackgroundType
import com.vng.sajja.domain.model.CollageImage
import com.vng.sajja.domain.model.CollageLayout
import com.vng.sajja.domain.model.ScaleType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.components.ImageThumbnail
import com.vng.sajja.ui.components.InteractiveCollageImage
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
    var isFullScreen by remember { mutableStateOf(false) }
    var showColorPickerTarget by remember { mutableStateOf<String?>(null) }

    // Floating bar offset state for dragging
    var barOffsetX by remember { mutableStateOf(0f) }
    var barOffsetY by remember { mutableStateOf(0f) }

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
                width = 0.25f,
                height = 0.25f,
                rotation = (index * 5f) % 360f,
                zIndex = index,
                opacity = 0.9f
            )
        }

        val updated = settings.copy(
            collageImages = settings.collageImages + newImages
        )
        onUpdateSettings(updated)
    }

    if (isFullScreen) {
        // Immersive Full Screen Collage Editor Mode using Dialog
        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Background Canvas
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { selectedImage = null }
                    ) {
                        val containerWidth = maxWidth
                        val containerHeight = maxHeight

                        // Render Background
                        when (settings.backgroundType) {
                            BackgroundType.SOLID -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(settings.backgroundColor))
                                )
                            }

                            BackgroundType.GRADIENT -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(settings.gradientStartColor),
                                                    Color(settings.gradientEndColor)
                                                )
                                            )
                                        )
                                )
                            }

                            BackgroundType.IMAGE -> {
                                settings.backgroundImageUri?.let { uri ->
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        coil.compose.AsyncImage(
                                            model = uri,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                } ?: Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(settings.backgroundColor))
                                )
                            }

                            BackgroundType.COLLAGE -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(settings.backgroundColor))
                                )
                            }
                        }

                        // Render collage images
                        settings.collageImages.sortedBy { it.zIndex }.forEach { image ->
                            key(image.uri) {
                                InteractiveCollageImage(
                                    image = image,
                                    settings = settings,
                                    isSelected = selectedImage?.uri == image.uri,
                                    containerWidth = containerWidth,
                                    containerHeight = containerHeight,
                                    onClick = { selectedImage = image },
                                    onUpdate = { updatedImage ->
                                        val updatedList = settings.collageImages.map {
                                            if (it.uri == updatedImage.uri) updatedImage else it
                                        }
                                        onUpdateSettings(settings.copy(collageImages = updatedList))
                                        if (selectedImage?.uri == image.uri) {
                                            selectedImage = updatedImage
                                        }
                                    },
                                    onRemove = {
                                        val updatedList =
                                            settings.collageImages.filter { it.uri != image.uri }
                                        onUpdateSettings(settings.copy(collageImages = updatedList))
                                        if (selectedImage?.uri == image.uri) {
                                            selectedImage = null
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Top overlay bar (Exit, Guidance Info, Add Image)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isFullScreen = false },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Exit Full Screen",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Drag bar to reposition controls",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        IconButton(
                            onClick = { showImagePicker = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Images",
                                tint = Color.White
                            )
                        }
                    }

                    // Draggable Floating Horizontal Editing Bar with Glassmorphic visual style
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 24.dp)
                            .padding(horizontal = 16.dp)
                            .widthIn(max = 600.dp)
                            .offset { IntOffset(barOffsetX.roundToInt(), barOffsetY.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    barOffsetX += dragAmount.x
                                    barOffsetY += dragAmount.y
                                }
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Title/Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Drag Handle",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (selectedImage != null) "Edit Selected Photo" else "Canvas Background",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                if (selectedImage == null) {
                                    // BG type selector
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        BackgroundType.entries.filter { it != BackgroundType.COLLAGE }
                                            .forEach { type ->
                                                FilterChip(
                                                    selected = settings.backgroundType == type,
                                                    onClick = {
                                                        onUpdateSettings(
                                                            settings.copy(
                                                                backgroundType = type
                                                            )
                                                        )
                                                    },
                                                    label = {
                                                        Text(
                                                            type.name,
                                                            fontSize = 10.sp,
                                                            color = Color.White
                                                        )
                                                    },
                                                    modifier = Modifier.height(28.dp),
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                        selectedLabelColor = Color.White
                                                    )
                                                )
                                            }
                                    }
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedImage != null) {
                                    val img = selectedImage!!
                                    // Image Editing Options
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Rotate 90
                                        IconButton(onClick = {
                                            val updated =
                                                img.copy(rotation = (img.rotation + 90f) % 360f)
                                            updateImage(updated, settings, onUpdateSettings)
                                            selectedImage = updated
                                        }) {
                                            Icon(
                                                Icons.Default.RotateRight,
                                                contentDescription = "Rotate 90",
                                                tint = Color.White
                                            )
                                        }

                                        // Scale Type Cycle
                                        IconButton(onClick = {
                                            val newScale = when (img.scaleType) {
                                                ScaleType.CENTER_CROP -> ScaleType.CENTER_INSIDE
                                                ScaleType.CENTER_INSIDE -> ScaleType.FIT_CENTER
                                                ScaleType.FIT_CENTER -> ScaleType.ORIGINAL
                                                ScaleType.ORIGINAL -> ScaleType.CENTER_CROP
                                            }
                                            val updated = img.copy(scaleType = newScale)
                                            updateImage(updated, settings, onUpdateSettings)
                                            selectedImage = updated
                                        }) {
                                            Icon(
                                                Icons.Default.AspectRatio,
                                                contentDescription = "Scale Mode",
                                                tint = Color.White
                                            )
                                        }

                                        // Opacity Controls
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val updated = img.copy(
                                                        opacity = (img.opacity - 0.1f).coerceIn(
                                                            0.1f,
                                                            1f
                                                        )
                                                    )
                                                    updateImage(updated, settings, onUpdateSettings)
                                                    selectedImage = updated
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.RemoveCircleOutline,
                                                    contentDescription = "Dim",
                                                    tint = Color.White
                                                )
                                            }
                                            Text(
                                                "${(img.opacity * 10).roundToInt()}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            IconButton(
                                                onClick = {
                                                    val updated = img.copy(
                                                        opacity = (img.opacity + 0.1f).coerceIn(
                                                            0.1f,
                                                            1f
                                                        )
                                                    )
                                                    updateImage(updated, settings, onUpdateSettings)
                                                    selectedImage = updated
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.AddCircleOutline,
                                                    contentDescription = "Brighten",
                                                    tint = Color.White
                                                )
                                            }
                                        }

                                        // Layer Z-Index Up
                                        IconButton(onClick = {
                                            val maxZ =
                                                settings.collageImages.maxOfOrNull { it.zIndex }
                                                    ?: 0
                                            val updated = img.copy(zIndex = maxZ + 1)
                                            updateImage(updated, settings, onUpdateSettings)
                                            selectedImage = updated
                                        }) {
                                            Icon(
                                                Icons.Default.Layers,
                                                contentDescription = "Bring to Front",
                                                tint = Color.White
                                            )
                                        }

                                        // Delete
                                        IconButton(onClick = {
                                            val updatedList =
                                                settings.collageImages.filter { it.uri != img.uri }
                                            onUpdateSettings(settings.copy(collageImages = updatedList))
                                            selectedImage = null
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Photo",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                } else {
                                    // Canvas Background options inside floating bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (settings.backgroundType) {
                                            BackgroundType.SOLID -> {
                                                Text(
                                                    "Solid Color:",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(settings.backgroundColor))
                                                        .border(2.dp, Color.White, CircleShape)
                                                        .clickable {
                                                            showColorPickerTarget = "bg_color"
                                                        }
                                                )
                                            }

                                            BackgroundType.GRADIENT -> {
                                                Text(
                                                    "Gradient Start/End:",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(settings.gradientStartColor))
                                                            .border(2.dp, Color.White, CircleShape)
                                                            .clickable {
                                                                showColorPickerTarget = "grad_start"
                                                            }
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(settings.gradientEndColor))
                                                            .border(2.dp, Color.White, CircleShape)
                                                            .clickable {
                                                                showColorPickerTarget = "grad_end"
                                                            }
                                                    )
                                                }
                                            }

                                            BackgroundType.IMAGE -> {
                                                Text(
                                                    "Single background image is active.",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                            }

                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Normal configuration screen view
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                        text = "${settings.collageImages.size} images added",
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

            // Full screen editor card trigger
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Reset floating bar offset when entering full screen
                        barOffsetX = 0f
                        barOffsetY = 0f
                        isFullScreen = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Open Full Screen Designer",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Drag, zoom, rotate, and arrange images freely.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
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
                    Text(
                        text = "Global Collage Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

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
                            Text("Image Spacing (Automatic Layouts)")
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

                    // Quick layout chips
                    if (settings.collageImages.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = {
                                    applyLayout(CollageLayout.GRID, settings, onUpdateSettings)
                                },
                                label = { Text("Grid") }
                            )
                            AssistChip(
                                onClick = {
                                    applyLayout(
                                        CollageLayout.CENTER_FOCUS,
                                        settings,
                                        onUpdateSettings
                                    )
                                },
                                label = { Text("Center Focus") }
                            )
                            AssistChip(
                                onClick = {
                                    applyLayout(CollageLayout.RANDOM, settings, onUpdateSettings)
                                },
                                label = { Text("Random") }
                            )
                        }
                    }
                }
            }

            // Collage preview/editor
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(
                        Color(settings.backgroundColor),
                        RoundedCornerShape(16.dp)
                    )
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                val containerWidth = maxWidth
                val containerHeight = maxHeight

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
                    // Render background
                    if (settings.backgroundType == BackgroundType.GRADIENT) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(settings.gradientStartColor),
                                            Color(settings.gradientEndColor)
                                        )
                                    )
                                )
                        )
                    } else if (settings.backgroundType == BackgroundType.IMAGE) {
                        settings.backgroundImageUri?.let { uri ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                coil.compose.AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        }
                    }

                    // Render interactive collage items inside bounds
                    settings.collageImages.sortedBy { it.zIndex }.forEach { image ->
                        key(image.uri) {
                            InteractiveCollageImage(
                                image = image,
                                settings = settings,
                                isSelected = selectedImage?.uri == image.uri,
                                containerWidth = containerWidth,
                                containerHeight = containerHeight,
                                onClick = { selectedImage = image },
                                onUpdate = { updatedImage ->
                                    val updatedList = settings.collageImages.map {
                                        if (it.uri == updatedImage.uri) updatedImage else it
                                    }
                                    onUpdateSettings(settings.copy(collageImages = updatedList))
                                    if (selectedImage?.uri == image.uri) {
                                        selectedImage = updatedImage
                                    }
                                },
                                onRemove = {
                                    val updatedList =
                                        settings.collageImages.filter { it.uri != image.uri }
                                    onUpdateSettings(settings.copy(collageImages = updatedList))
                                    if (selectedImage?.uri == image.uri) {
                                        selectedImage = null
                                    }
                                }
                            )
                        }
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
                                        val updatedList =
                                            settings.collageImages.filter { it.uri != image.uri }
                                        onUpdateSettings(settings.copy(collageImages = updatedList))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Color Picker Dialog overlay
    showColorPickerTarget?.let { target ->
        AdvancedColorPickerDialog(
            initial = when (target) {
                "bg_color" -> Color(settings.backgroundColor)
                "grad_start" -> Color(settings.gradientStartColor)
                "grad_end" -> Color(settings.gradientEndColor)
                else -> Color.White
            },
            onDismiss = { showColorPickerTarget = null }
        ) { color ->
            val updated = when (target) {
                "bg_color" -> settings.copy(backgroundColor = color.toArgb())
                "grad_start" -> settings.copy(gradientStartColor = color.toArgb())
                "grad_end" -> settings.copy(gradientEndColor = color.toArgb())
                else -> settings
            }
            onUpdateSettings(updated)
            showColorPickerTarget = null
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
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp
                                )
                            ) {
                                Icon(
                                    when (layout) {
                                        CollageLayout.GRID -> Icons.Default.GridView
                                        CollageLayout.MASONRY -> Icons.Default.ViewDay
                                        CollageLayout.CENTER_FOCUS -> Icons.Default.CenterFocusStrong
                                        CollageLayout.SPIRAL -> Icons.Default.Sync
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
                                val updatedImages =
                                    settings.collageImages.map { it.copy(opacity = newOpacity) }
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
                                        val updatedImages =
                                            settings.collageImages.map { it.copy(scaleType = scaleType) }
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
                    val angle =
                        ((index - 1) * 360f / (settings.collageImages.size - 1)) * (Math.PI / 180).toFloat()
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
