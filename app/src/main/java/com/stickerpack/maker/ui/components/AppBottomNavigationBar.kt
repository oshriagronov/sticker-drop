package com.stickerpack.maker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stickerpack.maker.ui.theme.*

@Composable
fun AppBottomNavigationBar(
    currentRoute: String,
    onNavigateToPacks: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val animationSpec = tween<Color>(durationMillis = 180, easing = FastOutSlowInEasing)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(CircleShape),
        color = SurfaceContainerLow,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isPacksSelected = currentRoute == "pack_list"
            val isAboutSelected = currentRoute == "about_settings"

            val packsBgColor by animateColorAsState(
                targetValue = if (isPacksSelected) SecondaryContainerCharcoal else Color.Transparent,
                animationSpec = animationSpec,
                label = "packsBg"
            )
            val packsContentColor by animateColorAsState(
                targetValue = if (isPacksSelected) PrimaryFixedDimMint else TextOnSurfaceVariant,
                animationSpec = animationSpec,
                label = "packsContent"
            )

            val aboutBgColor by animateColorAsState(
                targetValue = if (isAboutSelected) SecondaryContainerCharcoal else Color.Transparent,
                animationSpec = animationSpec,
                label = "aboutBg"
            )
            val aboutContentColor by animateColorAsState(
                targetValue = if (isAboutSelected) PrimaryFixedDimMint else TextOnSurfaceVariant,
                animationSpec = animationSpec,
                label = "aboutContent"
            )

            // My Packs Tab
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(packsBgColor)
                    .clickable(enabled = !isPacksSelected, onClick = onNavigateToPacks)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = "My Packs",
                    tint = packsContentColor
                )
                Text(
                    text = "My Packs",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isPacksSelected) FontWeight.Bold else FontWeight.Normal,
                        color = packsContentColor
                    )
                )
            }

            // About Tab
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(aboutBgColor)
                    .clickable(enabled = !isAboutSelected, onClick = onNavigateToAbout)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About",
                    tint = aboutContentColor
                )
                Text(
                    text = "About",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isAboutSelected) FontWeight.Bold else FontWeight.Normal,
                        color = aboutContentColor
                    )
                )
            }
        }
    }
}
