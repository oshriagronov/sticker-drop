package com.stickerpack.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.stickerpack.maker.data.StickerEntity
import com.stickerpack.maker.data.StickerPackWithStickers
import com.stickerpack.maker.ui.theme.GreenWhatsApp
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val pack = packWithStickers.pack
    val stickers = packWithStickers.stickers
    val trayFile = File(context.filesDir, "stickers/${pack.identifier}/${pack.trayImageFileName}")
    val isReadyForWhatsApp = stickers.size >= 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pack.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onExportToWhatsApp,
                icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White) },
                text = { Text("Update in WhatsApp", color = Color.White) },
                containerColor = if (isReadyForWhatsApp) GreenWhatsApp else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Pack Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
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
                            Icon(Icons.Default.Collections, contentDescription = null, tint = GreenWhatsApp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pack.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Publisher: ${pack.publisher}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Stickers: ${stickers.size} / 30" + if (stickers.size < 3) " (Requires min 3 for WhatsApp)" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isReadyForWhatsApp) GreenWhatsApp else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sticker Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // First card is "+ Add Sticker" tile (directly launches photo picker)
                item {
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenWhatsApp.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Sticker",
                                    tint = GreenWhatsApp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Add Sticker",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GreenWhatsApp
                                )
                            }
                        }
                    }
                }

                // Grid items for existing stickers
                items(stickers, key = { it.id }) { sticker ->
                    val sFile = File(context.filesDir, "stickers/${pack.identifier}/${sticker.fileName}")
                    Card(
                        modifier = Modifier.aspectRatio(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (sFile.exists()) {
                                Image(
                                    painter = rememberAsyncImagePainter(sFile),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Modern Delete Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF3B30))
                                    .clickable { onDeleteSticker(sticker) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete Sticker",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
