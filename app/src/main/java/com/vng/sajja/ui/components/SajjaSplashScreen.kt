package com.vng.sajja.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SajjaSplashScreen(
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.4f) }
    val alphaAnim = remember { Animatable(0f) }
    val textAlphaAnim = remember { Animatable(0f) }
    val textTranslateAnim = remember { Animatable(30f) }
    val glowScaleAnim = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        // Logo spring scaling
        launch {
            scaleAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        // Logo alpha fade-in
        launch {
            alphaAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
            )
        }
        
        // Violet glow backdrop scale expansion
        launch {
            glowScaleAnim.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )
        }

        // Slide up text branding details
        delay(600.milliseconds)
        launch {
            textAlphaAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            textTranslateAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }

        delay(1400.milliseconds) // Total duration 2 seconds
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020208),
                        Color(0xFF0F1026)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Pulser glow ring
        Box(
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer {
                    scaleX = glowScaleAnim.value
                    scaleY = glowScaleAnim.value
                    alpha = alphaAnim.value * 0.15f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8A2BE2),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_playstore),
                contentDescription = "Sajja App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                        alpha = alphaAnim.value
                    }
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SAJJA",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                color = Color.White,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = textAlphaAnim.value
                        translationY = textTranslateAnim.value
                    }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Customize Your Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .graphicsLayer {
                        alpha = textAlphaAnim.value
                        translationY = textTranslateAnim.value
                    }
            )
        }
    }
}
