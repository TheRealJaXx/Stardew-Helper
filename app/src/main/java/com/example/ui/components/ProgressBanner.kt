package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalnutDatabase
import com.example.ui.IslandMilestone
import com.example.ui.theme.IslandGreen
import com.example.ui.theme.WalnutGold
import com.example.ui.theme.WalnutGoldDark
import com.example.ui.theme.WalnutGoldLight
import com.example.ui.theme.WoodBrownDark

@Composable
fun ProgressBanner(
    totalFound: Int,
    milestones: List<IslandMilestone>,
    onOpenParrotSheet: () -> Unit,
    onOpenRecountDialog: () -> Unit,
    onOpenResetDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalAvailable = WalnutDatabase.TOTAL_WALNUTS_COUNT // 130
    val progressRatio = (totalFound.toFloat() / totalAvailable.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = tween(durationMillis = 600),
        label = "walnut_progress_anim"
    )
    val percentage = (progressRatio * 100).toInt()

    val nextMilestone = milestones.firstOrNull { it.requiredNuts > totalFound }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("progress_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(WalnutGold.copy(alpha = 0.5f)),
            width = 1.5.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Walnut Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Walnut Coin Badge
                    Surface(
                        shape = CircleShape,
                        color = WalnutGold,
                        modifier = Modifier
                            .size(46.dp)
                            .border(2.dp, WalnutGoldDark, CircleShape),
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🌰",
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Ginger Island Walnuts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (totalFound >= 130) "🌟 Perfect Island Completion!"
                            else if (totalFound >= 100) "✨ Qi's Room Unlocked (${130 - totalFound} left)"
                            else "${130 - totalFound} walnuts remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (totalFound >= 130) IslandGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Walnut Counter
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$totalFound",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = WalnutGoldDark
                        )
                        Text(
                            text = " / $totalAvailable",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "$percentage% found",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Progress Bar with Milestone Flags
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(WalnutGold, WalnutGoldDark)
                            )
                        )
                )
            }

            // Milestone Labels (100: Qi Room, 130: Perfection)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "💎 Qi Room (100)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (totalFound >= 100) FontWeight.Bold else FontWeight.Normal,
                    color = if (totalFound >= 100) IslandGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🏆 130",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (totalFound >= 130) FontWeight.Bold else FontWeight.Normal,
                    color = if (totalFound >= 130) WalnutGoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Next Milestone Card
            if (nextMilestone != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nextMilestone.icon,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Next Goal: ${nextMilestone.title} (${nextMilestone.requiredNuts} nuts)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${nextMilestone.requiredNuts - totalFound} more needed • ${nextMilestone.reward}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    onClick = onOpenParrotSheet,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("leo_parrot_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = WalnutGoldLight,
                        contentColor = WoodBrownDark
                    )
                ) {
                    Text(text = "🦜 Parrot Hints", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenRecountDialog,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("recount_nuts_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Recount / Help")
                }

                OutlinedButton(
                    onClick = onOpenResetDialog,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("reset_progress_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reset checklist",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
