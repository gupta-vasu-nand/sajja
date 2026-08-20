package com.vng.sajja.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import com.vng.sajja.ui.theme.getContrastingTextColor
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

fun Float.toRadians(): Double = Math.toRadians(this.toDouble())

data class FabMenuItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun ExpandableFab(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animated container for size changes
    val size by animateDpAsState(
        targetValue = if (expanded) 64.dp else 56.dp,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "fab_size"
    )

    val containerColor = if (expanded) Color.White else MaterialTheme.colorScheme.primary
    val contentColor = getContrastingTextColor(containerColor)

    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        modifier = modifier.size(size)
    ) {
        // Smoother rotation with easing
        val rotation by animateFloatAsState(
            targetValue = if (expanded) 135f else 0f,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            ),
            label = "fab_rotation"
        )

        // Scale effect on click
        val scale by animateFloatAsState(
            targetValue = if (expanded) 1.1f else 1f,
            animationSpec = spring(
                dampingRatio = 0.5f,
                stiffness = 500f
            ),
            label = "fab_scale"
        )

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = if (expanded) "Close menu" else "Open menu",
            tint = contentColor,
            modifier = Modifier
                .rotate(rotation)
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                }
        )
    }
}

@Composable
fun FabMenuItemView(
    item: FabMenuItem,
    onItemClick: (FabMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "item_press_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = 42.dp,
                    color = contentColor.copy(alpha = 0.35f)
                )
            ) { onItemClick(item) }
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = item.label,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CircularFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    items: List<FabMenuItem>,
    onItemClick: (FabMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = getContrastingTextColor(backgroundColor),
    menuRadius: Dp = 200.dp,
    backgroundRadius: Dp = 240.dp,
    startAngleDegrees: Float = 15f,
    endAngleDegrees: Float = 75f,
    scaffoldPadding: Dp = 16.dp,
    content: @Composable (FabMenuItem) -> Unit = { item ->
        FabMenuItemView(item, onItemClick, contentColor = iconColor)
    }
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Quarter-circle background that concentricly scales up/down
        val bgRadius by animateDpAsState(
            targetValue = if (expanded) backgroundRadius else 0.dp,
            animationSpec = if (expanded) {
                spring(
                    dampingRatio = 0.7f,
                    stiffness = 250f
                )
            } else {
                tween(durationMillis = 200)
            },
            label = "bg_radius"
        )

        if (bgRadius > 0.dp) {
            Box(
                modifier = Modifier
                    .size(bgRadius)
                    // Offset moves the bottom-right corner of the shape to align with the screen corner (neutralizing Scaffold margin)
                    .offset(x = scaffoldPadding, y = scaffoldPadding)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(topStart = bgRadius)
                    )
            )
        }

        items.forEachIndexed { index, item ->
            // Staggered animation delay based on index (bottom-most item opens first)
            val animationDelay = (items.size - 1 - index) * 50

            // Animate radius dynamically for a smooth radial slide-out effect from the main FAB center
            val radius by animateDpAsState(
                targetValue = if (expanded) menuRadius else 0.dp,
                animationSpec = if (expanded) {
                    spring(
                        dampingRatio = 0.6f,
                        stiffness = 300f
                    )
                } else {
                    tween(durationMillis = 150)
                },
                label = "radius_$index"
            )

            // Distribute items evenly along the specified angular arc (quarter circle)
            val angleRange = endAngleDegrees - startAngleDegrees
            val angleDegrees = if (items.size <= 1) {
                endAngleDegrees
            } else {
                endAngleDegrees - index * (angleRange / (items.size - 1))
            }
            val angleRad = angleDegrees.toFloat().toRadians()

            // Calculate x and y offsets for the 80.dp x 80.dp box center.
            // (40.dp + scaffoldPadding) offsets the center of the items to align concentrically with the screen corner.
            val centerOffset = 40.dp + scaffoldPadding
            val offsetX = (radius.value * cos(angleRad)).dp - centerOffset
            val offsetY = (radius.value * sin(angleRad)).dp - centerOffset

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(200, delayMillis = animationDelay)) +
                        scaleIn(tween(200, delayMillis = animationDelay), initialScale = 0.8f),
                exit = fadeOut(tween(150)) +
                        scaleOut(tween(150), targetScale = 0.8f)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .offset(x = -offsetX, y = -offsetY)
                ) {
                    content(item)
                }
            }
        }

        ExpandableFab(
            expanded = expanded,
            onClick = onToggle
        )
    }
}
