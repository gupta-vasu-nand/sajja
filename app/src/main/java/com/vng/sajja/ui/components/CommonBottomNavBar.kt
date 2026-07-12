package com.vng.sajja.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.vng.sajja.ui.theme.getContrastingTextColor

data class BottomNavBarItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

object BottomNavBarItems {
    val items = listOf(
        BottomNavBarItem("dashboard", "Home", Icons.Default.Home),
        BottomNavBarItem("clock_settings", "Clock Face", Icons.Default.AccessTime),
        BottomNavBarItem("background_settings", "Background", Icons.Default.Image)
    )
}

@Composable
fun CommonBottomNavBar(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val navItems = BottomNavBarItems.items

    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val contrastColor = getContrastingTextColor(containerColor)

    Surface(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                spotColor = Color.Black
            ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = containerColor
    ) {
        NavigationBar(
            modifier = Modifier.heightIn(max = 75.dp),
            containerColor = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index

                    NavigationBarItem(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        alwaysShowLabel = true,
                        selected = isSelected,
                        onClick = { onTabSelected(index) },
                        icon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(20.dp),
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        contrastColor.copy(alpha = 0.6f)
                                )

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        contrastColor.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = contrastColor,
                            unselectedTextColor = contrastColor,
                            indicatorColor = Color.Transparent
                        )
                    )

                    if (index < navItems.lastIndex) {
                        VerticalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp),
                            color = contrastColor.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}
