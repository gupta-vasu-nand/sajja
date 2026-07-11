package com.vng.sajja.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vng.sajja.R
import kotlinx.coroutines.delay

@Composable
fun SajjaSplashScreen(
    onDismiss: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val yOffsetFraction by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -1.8f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "splash_offset"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000) // Display splash screen for exactly 2 seconds
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020208), // Dark sky background color adjusted to match launch image
                        Color(0xFF0F1026)  // Cohesive dark navy/purple tone
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_playstore),
            contentDescription = "Sajja App Logo",
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer {
                    translationY = yOffsetFraction * this.size.height * 2.5f
                }
        )
    }
}
