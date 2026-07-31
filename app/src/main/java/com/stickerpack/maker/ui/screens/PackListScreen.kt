package com.stickerpack.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.stickerpack.maker.data.StickerPackWithStickers
import com.stickerpack.maker.ui.components.AppBottomNavigationBar
import com.stickerpack.maker.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackListScreen(
    packs: List<StickerPackWithStickers>,
    onPackClick: (String) -> Unit,
    onCreatePackClick: () -> Unit,
    onImportClick: () -> Unit,
    onAboutClick: () -> Unit,
    onExportToWhatsApp: (StickerPackWithStickers) -> Unit,
    onDeletePack: (String) -> Unit,
    onAddStickerToPack: (packId: String, imageUri: Uri) -> Unit
) {
    var activePackIdForAdd by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val targetId = activePackIdForAdd
        if (uri != null && targetId != null) {
            onAddStickerToPack(targetId, uri)
            activePackIdForAdd = null
        }
    }

    Scaffold(
        containerColor = SurfaceBackgroundNavy,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "StickerDrop",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryFixedDimMint,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Create, load existing packs & sync to WhatsApp",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextOnSurfaceVariant)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onImportClick) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = "Load Pack",
                            tint = SpringMint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBackgroundNavy
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (packs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterFrames,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = SpringMint
                        )
                        Text(
                            text = "No Sticker Packs Yet",
                            style = MaterialTheme.typography.titleLarge.copy(color = TextOnSurface, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tap '+' at the bottom to create a new pack, or load an existing pack from your device.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextOnSurfaceVariant),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        OutlinedButton(
                            onClick = onImportClick,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, OutlineVariantGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileOpen, contentDescription = null, tint = PrimaryFixedDimMint)
                            Spacer(Modifier.width(8.dp))
                            Text("Load Pack", color = PrimaryFixedDimMint)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 16.dp,
                        bottom = 100.dp // Content scrolls cleanly under floating overlay
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(packs, key = { it.pack.identifier }) { packWithStickers ->
                        PackItemCard(
                            packWithStickers = packWithStickers,
                            onPackClick = { onPackClick(packWithStickers.pack.identifier) },
                            onExportToWhatsApp = { onExportToWhatsApp(packWithStickers) },
                            onDeletePack = { onDeletePack(packWithStickers.pack.identifier) },
                            onAddStickerClick = {
                                activePackIdForAdd = packWithStickers.pack.identifier
                                photoPickerLauncher.launch("image/*")
                            }
                        )
                    }
                }
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
                    // Navigation Pill Bar on the left/center
                    Box(modifier = Modifier.weight(1f)) {
                        AppBottomNavigationBar(
                            currentRoute = "pack_list",
                            onNavigateToPacks = {},
                            onNavigateToAbout = onAboutClick
                        )
                    }

                    // "+" Button positioned directly to the right of the navigation pill
                    Surface(
                        onClick = onCreatePackClick,
                        shape = CircleShape,
                        color = SpringMint,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Pack",
                                tint = OnPrimaryContainerMint,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackItemCard(
    packWithStickers: StickerPackWithStickers,
    onPackClick: () -> Unit,
    onExportToWhatsApp: () -> Unit,
    onDeletePack: () -> Unit,
    onAddStickerClick: () -> Unit
) {
    val context = LocalContext.current
    val pack = packWithStickers.pack
    val stickerCount = packWithStickers.stickers.size
    val isWhatsAppReady = stickerCount >= 3

    val trayFile = File(context.filesDir, "stickers/${pack.identifier}/${pack.trayImageFileName}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPackClick)
            .border(1.dp, OutlineVariantGreen.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceCharcoal)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tray Icon Preview
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .border(2.dp, OutlineVariantGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (trayFile.exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(trayFile),
                            contentDescription = "Tray Icon",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = SpringMint
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pack.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    )
                    Text(
                        text = "By ${pack.publisher}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextOnSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Sticker Counter Pill (JetBrains Mono style)
                    Surface(
                        color = SurfaceContainerLow,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariantGreen.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "$stickerCount / 30 Stickers" + if (stickerCount < 3) " (Min 3)" else "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryFixedDimMint,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDeletePack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Pack",
                        tint = ErrorPink
                    )
                }
            }

            // Preview Row of First 5 Stickers
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                packWithStickers.stickers.take(5).forEach { sticker ->
                    val sFile = File(context.filesDir, "stickers/${pack.identifier}/${sticker.fileName}")
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerNavy)
                            .border(1.dp, OutlineVariantGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    ) {
                        if (sFile.exists()) {
                            Image(
                                painter = rememberAsyncImagePainter(sFile),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                if (stickerCount > 5) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${stickerCount - 5}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextOnSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Row: Add Sticker & WhatsApp Export
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAddStickerClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, OutlineVariantGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = PrimaryFixedDimMint
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Sticker",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryFixedDimMint
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onExportToWhatsApp,
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpringMint,
                        disabledContainerColor = SpringMint.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = OnPrimaryContainerMint
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to WhatsApp",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnPrimaryContainerMint
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
