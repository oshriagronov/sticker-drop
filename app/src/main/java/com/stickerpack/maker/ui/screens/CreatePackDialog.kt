package com.stickerpack.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.stickerpack.maker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePackDialog(
    onDismiss: () -> Unit,
    onCreatePack: (name: String, publisher: String, trayUri: Uri?) -> Unit
) {
    var packName by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var trayUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) trayUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurfaceCharcoal,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Create New Pack",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tray Icon Selection
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .border(2.dp, OutlineVariantGreen, CircleShape)
                        .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (trayUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(trayUri),
                            contentDescription = "Tray Icon",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick Icon",
                                tint = SpringMint
                            )
                            Text(
                                text = "Icon",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = PrimaryFixedDimMint
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = packName,
                    onValueChange = { packName = it },
                    label = { Text("Pack Name (e.g. Meme Royalty)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerNavy,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedBorderColor = SpringMint,
                        unfocusedBorderColor = OutlineVariantGreen,
                        focusedLabelColor = SpringMint,
                        unfocusedLabelColor = TextOnSurfaceVariant,
                        focusedTextColor = TextOnSurface,
                        unfocusedTextColor = TextOnSurface
                    )
                )

                OutlinedTextField(
                    value = publisher,
                    onValueChange = { publisher = it },
                    label = { Text("Publisher Name (e.g. Oshri)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerNavy,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedBorderColor = SpringMint,
                        unfocusedBorderColor = OutlineVariantGreen,
                        focusedLabelColor = SpringMint,
                        unfocusedLabelColor = TextOnSurfaceVariant,
                        focusedTextColor = TextOnSurface,
                        unfocusedTextColor = TextOnSurface
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (packName.isNotBlank() && publisher.isNotBlank()) {
                        onCreatePack(packName, publisher, trayUri)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpringMint)
            ) {
                Text(
                    text = "Create Pack",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryContainerMint
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge.copy(color = TextOnSurfaceVariant)
                )
            }
        }
    )
}
