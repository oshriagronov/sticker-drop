package com.stickerpack.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.stickerpack.maker.data.StickerEntity
import com.stickerpack.maker.data.StickerPackWithStickers
import com.stickerpack.maker.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackDetailScreen(
    packWithStickers: StickerPackWithStickers?,
    onBackClick: () -> Unit,
    onAddStickerUriSelected: (Uri) -> Unit,
    onExportToWhatsApp: () -> Unit,
    onDeleteSticker: (StickerEntity) -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddStickerUriSelected(uri)
        }
    }

    if (packWithStickers == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBackgroundNavy),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SpringMint)
        }
        return
    }

    val pack = packWithStickers.pack
    val stickers = packWithStickers.stickers
    val trayFile = File(context.filesDir, "stickers/${pack.identifier}/${pack.trayImageFileName}")
    val isReadyForWhatsApp = stickers.size >= 3

    Scaffold(
        containerColor = SurfaceBackgroundNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pack.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextOnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBackgroundNavy)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onExportToWhatsApp,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = OnPrimaryContainerMint
                    )
                },
                text = {
                    Text(
                        text = "Sync to WhatsApp",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnPrimaryContainerMint
                        )
                    )
                },
                containerColor = SpringMint,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            // Pack Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, OutlineVariantGreen.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceCharcoal),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                            Icon(Icons.Default.Collections, contentDescription = null, tint = SpringMint)
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
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextOnSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stickers.size} / 30 Stickers" + if (stickers.size < 3) " (Min 3 required)" else "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                color = if (isReadyForWhatsApp) PrimaryFixedDimMint else ErrorPink,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sticker Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // First card is "+ Add Sticker" tile (directly launches photo picker)
                item {
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(2.dp, SpringMint, RoundedCornerShape(16.dp))
                            .clickable { photoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerNavy)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Sticker",
                                    tint = SpringMint,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "Add Sticker",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SpringMint
                                    )
                                )
                            }
                        }
                    }
                }

                // Grid items for existing stickers
                items(stickers, key = { it.id }) { sticker ->
                    val sFile = File(context.filesDir, "stickers/${pack.identifier}/${sticker.fileName}")
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(1.dp, OutlineVariantGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceCharcoal)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (sFile.exists()) {
                                Image(
                                    painter = rememberAsyncImagePainter(sFile),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Modern Delete Badge (Sticker Chip design)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ErrorPink.copy(alpha = 0.9f))
                                    .clickable { onDeleteSticker(sticker) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete Sticker",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
