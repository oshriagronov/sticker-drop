package com.stickerpack.maker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickerpack.maker.ui.components.AppBottomNavigationBar
import com.stickerpack.maker.ui.theme.*

@Composable
fun AboutSettingsScreen(
    onNavigateToPacks: () -> Unit,
    onViewLogsClick: () -> Unit
) {
    Scaffold(
        containerColor = SurfaceBackgroundNavy
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // Main Content Canvas with bottom padding so content scrolls under overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Screen Header Title & Subtitle matching Stitch mockup
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryFixedDimMint,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Everything you need to know about StickerDrop.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextOnSurfaceVariant
                        )
                    )
                }

                // Feature Cards Grid / List
                FeatureCard(
                    icon = Icons.Default.MoneyOff,
                    title = "No Cost",
                    description = "StickerDrop is completely free to use. Create and share custom sticker packs without limits."
                )

                FeatureCard(
                    icon = Icons.Default.Block,
                    title = "No Ads",
                    description = "Enjoy a premium, uninterrupted creative experience. We value your focus."
                )

                FeatureCard(
                    icon = Icons.Default.VerifiedUser,
                    title = "Secure & Fast",
                    description = "Local processing ensures your packs are created instantly and securely on your device."
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Links Section Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariantGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerNavy),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        LinkRow(
                            icon = Icons.Default.Policy,
                            title = "Privacy Policy",
                            onClick = {}
                        )
                        HorizontalDivider(color = OutlineVariantGreen.copy(alpha = 0.3f))
                        LinkRow(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            title = "Terms of Service",
                            onClick = {}
                        )
                        HorizontalDivider(color = OutlineVariantGreen.copy(alpha = 0.3f))
                        LinkRow(
                            icon = Icons.Default.BugReport,
                            title = "View App & WhatsApp Sync Logs",
                            onClick = onViewLogsClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Version Footer
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Version 2.4.1",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = TextOnSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Floating Overlay Bottom Bar Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SurfaceBackgroundNavy.copy(alpha = 0.85f),
                                SurfaceBackgroundNavy
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Navigation Pill Bar on the left/center (exact same size as Dashboard)
                    Box(modifier = Modifier.weight(1f)) {
                        AppBottomNavigationBar(
                            currentRoute = "about_settings",
                            onNavigateToPacks = onNavigateToPacks,
                            onNavigateToAbout = {}
                        )
                    }

                    // Placeholder space matching the 56.dp '+' button size on Dashboard
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariantGreen.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceCharcoal),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SpringMint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryFixedDimMint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextOnSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun LinkRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextOnSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextOnSurface
                )
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextOnSurfaceVariant
        )
    }
}
