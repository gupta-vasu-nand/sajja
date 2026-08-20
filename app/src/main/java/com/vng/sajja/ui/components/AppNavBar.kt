package com.vng.sajja.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.R
import com.vng.sajja.ui.theme.getContrastingTextColor

data class AppNavBarItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val iconRes: Int? = null
)

object AppNavBarItems {
    val items = listOf(
        AppNavBarItem("dashboard", "Home", iconRes = R.drawable.ic_nav_home),
        AppNavBarItem("clock_settings", "Clock Face", iconRes = R.drawable.ic_nav_clock_face),
        AppNavBarItem("background_settings", "Background", iconRes = R.drawable.ic_nav_background),
        AppNavBarItem("analytics", "Analytics", iconRes = R.drawable.ic_nav_analytics)
    )
}

@Composable
fun AppNavBar(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val navItems = AppNavBarItems.items
    val isDark = MaterialTheme.colorScheme.background == Color.Black

    val navBarBg = if (isDark) {
        Color(0xFF181824).copy(alpha = 0.92f)
    } else {
        Color.White.copy(alpha = 0.96f)
    }

    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(navBarBg)
                .border(1.dp, borderColor, CircleShape),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.90f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "tab_scale"
                    )

                    val tabWeight by animateFloatAsState(
                        targetValue = if (isSelected) 1.5f else 0.8f,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "tab_weight"
                    )

                    val activeBg = MaterialTheme.colorScheme.primary
                    val activeFg = getContrastingTextColor(activeBg)

                    val itemBgColor by animateColorAsState(
                        targetValue = if (isSelected) activeBg else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "tab_bg_color"
                    )

                    val itemFgColor by animateColorAsState(
                        targetValue = if (isSelected) activeFg else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "tab_fg_color"
                    )

                    val iconSize = if (isSelected) 28.dp else 24.dp

                    Surface(
                        modifier = Modifier
                            .weight(tabWeight)
                            .height(48.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onTabSelected(index) },
                        color = itemBgColor,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.iconRes != null) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(iconSize)
                                )
                            } else if (item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = itemFgColor,
                                    modifier = Modifier.size(iconSize)
                                )
                            }

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = itemFgColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
