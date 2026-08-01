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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.stickerpack.maker.ui.theme.GreenWhatsApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerAddDialog(
    packName: String,
    onDismiss: () -> Unit,
    onAddSticker: (imageUri: Uri, emojis: String, cropCircle: Boolean) -> Unit
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var emojis by remember { mutableStateOf("😀") }
    var cropCircle by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) selectedUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Sticker to \"$packName\"",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image preview & picker area
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(if (cropCircle) CircleShape else RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 2.dp,
                            color = if (selectedUri != null) GreenWhatsApp else Color.Transparent,
                            shape = if (cropCircle) CircleShape else RoundedCornerShape(16.dp)
                        )
                        .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedUri),
                            contentDescription = "Sticker Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick Image",
                                tint = GreenWhatsApp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select Image",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                // Framing options: Fit Canvas vs Circular Crop
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = !cropCircle,
                        onClick = { cropCircle = false },
                        label = { Text("Fit 512x512") },
                        leadingIcon = if (!cropCircle) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )

                    FilterChip(
                        selected = cropCircle,
                        onClick = { cropCircle = true },
                        label = { Text("Circle Crop") },
                        leadingIcon = if (cropCircle) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }

                // Emoji Tagging
                OutlinedTextField(
                    value = emojis,
                    onValueChange = { emojis = it },
                    label = { Text("Sticker Emojis (e.g. 😀,🔥)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val uri = selectedUri
                    if (uri != null) {
                        onAddSticker(uri, emojis, cropCircle)
                        onDismiss()
                    }
                },
                enabled = selectedUri != null,
                colors = ButtonDefaults.buttonColors(containerColor = GreenWhatsApp)
            ) {
                Text("Add Sticker", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
