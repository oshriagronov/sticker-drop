package com.stickerpack.maker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stickerpack.maker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPackDialog(
    initialName: String,
    initialPublisher: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, publisher: String) -> Unit
) {
    var packName by remember(initialName) { mutableStateOf(initialName) }
    var publisher by remember(initialPublisher) { mutableStateOf(initialPublisher) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurfaceCharcoal,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Rename Pack Details",
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
                OutlinedTextField(
                    value = packName,
                    onValueChange = { packName = it },
                    label = { Text("Pack Name") },
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
                    label = { Text("Author Name") },
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
                        onConfirm(packName, publisher)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpringMint)
            ) {
                Text(
                    text = "Save",
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
