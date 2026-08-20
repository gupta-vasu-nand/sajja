package com.vng.sajja.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun GlassmorphicBatteryWarningDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Color.Black

    val cardBgColor = if (isDark) {
        Color(0xFF1E1E2C).copy(alpha = 0.92f)
    } else {
        Color.White.copy(alpha = 0.95f)
    }

    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.Black.copy(alpha = 0.1f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .background(cardBgColor)
                .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Icon Header Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB300).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Battery Warning",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title
                Text(
                    text = "Battery Usage Notice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Description text
                Text(
                    text = "Enabling live particle animations or digit switching transitions requires continuous graphics rendering, which may result in higher battery consumption.\n\nYou can fine-tune particle density or animation speed at any time in settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    val btnBg = MaterialTheme.colorScheme.primary
                    val btnFg = getContrastingTextColor(btnBg)

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = btnBg,
                            contentColor = btnFg
                        )
                    ) {
                        Text(
                            text = "Enable & Apply",
                            fontWeight = FontWeight.Bold,
                            color = btnFg,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
