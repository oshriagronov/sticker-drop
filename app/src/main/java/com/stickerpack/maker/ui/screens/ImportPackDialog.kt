package com.stickerpack.maker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stickerpack.maker.ui.theme.*

@Composable
fun ImportPackDialog(
    onDismiss: () -> Unit,
    onImportFile: (Uri) -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImportFile(uri)
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerHigh,
        title = {
            Text(
                text = "Load Sticker Pack",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextOnSurface,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select a .wastickers file from your device to load into StickerDrop.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnSurfaceVariant
                )

                Button(
                    onClick = { filePicker.launch("*/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpringMint,
                        contentColor = OnPrimaryContainerMint
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Import",
                        tint = OnPrimaryContainerMint,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Select .wastickers File",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = OnPrimaryContainerMint
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = TextOnSurfaceVariant
                )
            }
        }
    )
}
