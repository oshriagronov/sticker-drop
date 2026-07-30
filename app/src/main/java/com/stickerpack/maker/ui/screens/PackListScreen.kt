package com.stickerpack.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.stickerpack.maker.data.StickerPackWithStickers
import com.stickerpack.maker.ui.theme.GreenWhatsApp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackListScreen(
    packs: List<StickerPackWithStickers>,
    onPackClick: (String) -> Unit,
    onCreatePackClick: () -> Unit,
    onImportClick: () -> Unit,
    onViewLogsClick: () -> Unit,
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
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "StickerDrop",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Create, load existing packs & sync to WhatsApp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onViewLogsClick) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "View Logs",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onImportClick) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = "Load Pack",
                            tint = GreenWhatsApp
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreatePackClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Pack") },
                containerColor = GreenWhatsApp,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        if (packs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "No Sticker Packs Yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Tap '+ New Pack' at the bottom right to create a new pack, or load an existing pack from your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(onClick = onImportClick) {
                        Icon(Icons.Default.FileOpen, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Load Pack")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
            .clickable(onClick = onPackClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tray Icon Preview
                Box(
                    modifier = Modifier
                        .size(56.dp)
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
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = GreenWhatsApp
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pack.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "By ${pack.publisher}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "$stickerCount / 30 Stickers" + if (stickerCount < 3) " (Min 3 req)" else ""
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isWhatsAppReady) GreenWhatsApp.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        )
                    )
                }

                IconButton(onClick = onDeletePack) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Pack",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Preview Row of First 5 Stickers
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                packWithStickers.stickers.take(5).forEach { sticker ->
                    val sFile = File(context.filesDir, "stickers/${pack.identifier}/${sticker.fileName}")
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
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
                    Text(
                        text = "+${stickerCount - 5}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))

            // Action Row: Add Sticker & WhatsApp Export
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAddStickerClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Sticker",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onExportToWhatsApp,
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWhatsAppReady) GreenWhatsApp else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add to WhatsApp",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
