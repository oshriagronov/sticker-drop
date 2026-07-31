package com.stickerpack.maker

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stickerpack.maker.data.StickerPackWithStickers
import com.stickerpack.maker.ui.screens.*
import com.stickerpack.maker.ui.theme.StickerPackMakerTheme
import com.stickerpack.maker.ui.viewmodel.StickerViewModel
import com.stickerpack.maker.util.StickerPackExporter

class MainActivity : ComponentActivity() {

    private val viewModel: StickerViewModel by viewModels()
    private val pendingImportUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Clear debug logs each time app opens to ensure fresh logs for session
        StickerContentProvider.clearDebugLogs(this)
        StickerContentProvider.log(this, "=== APP OPENED (MainActivity.onCreate) ===")

        handleIntent(intent)

        setContent {
            StickerPackMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StickerAppContent(
                        viewModel = viewModel,
                        pendingImportUri = pendingImportUri.value,
                        onImportHandled = { pendingImportUri.value = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        StickerContentProvider.log(this, "=== NEW INTENT RECEIVED (MainActivity.onNewIntent) ===")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) {
            StickerContentProvider.log(this, "handleIntent: Intent is null")
            return
        }

        val action = intent.action
        val data = intent.data
        val type = intent.type
        @Suppress("DEPRECATION")
        val stream = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val clipItemCount = intent.clipData?.itemCount ?: 0

        StickerContentProvider.log(
            this,
            "Intent received: action=$action, data=$data, type=$type, extra_stream=$stream, clipItemCount=$clipItemCount"
        )

        val uri = extractUriFromIntent(intent)
        if (uri != null) {
            StickerContentProvider.log(this, "Extracted valid target URI: $uri")
            pendingImportUri.value = uri
        } else {
            StickerContentProvider.log(this, "No valid file URI found in intent")
        }
    }

    private fun extractUriFromIntent(intent: Intent?): Uri? {
        if (intent == null) return null
        val action = intent.action
        if (Intent.ACTION_VIEW == action || Intent.ACTION_SEND == action || Intent.ACTION_SEND_MULTIPLE == action) {
            var uri: Uri? = intent.data
            if (uri == null) {
                @Suppress("DEPRECATION")
                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
            }
            if (uri == null && intent.clipData != null && intent.clipData!!.itemCount > 0) {
                uri = intent.clipData!!.getItemAt(0).uri
            }
            return uri
        }
        return null
    }
}

@Composable
fun StickerAppContent(
    viewModel: StickerViewModel,
    pendingImportUri: Uri? = null,
    onImportHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val packs by viewModel.packsState.collectAsState()
    val selectedPack by viewModel.selectedPackState.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showLogsDialog by remember { mutableStateOf(false) }
    var logsContent by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Fast & smooth 180ms screen transition specs
    val animDuration = 180
    val enterAnim = fadeIn(animationSpec = tween(animDuration, easing = FastOutSlowInEasing))
    val exitAnim = fadeOut(animationSpec = tween(animDuration, easing = FastOutSlowInEasing))

    // WhatsApp ActivityResult Launcher
    val whatsappAddLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Sticker pack added to WhatsApp!", Toast.LENGTH_SHORT).show()
        } else {
            val extras = result.data?.extras
            @Suppress("DEPRECATION")
            val isUserCancel = extras?.getBoolean("user_cancel", false) == true ||
                    extras?.getString("user_cancel") == "true" ||
                    (result.resultCode == Activity.RESULT_CANCELED && (extras == null || extras.isEmpty))

            if (!isUserCancel) {
                @Suppress("DEPRECATION")
                val errorMsg = if (extras != null && !extras.isEmpty) {
                    extras.keySet().joinToString("\n") { key -> "$key: ${extras.get(key)}" }
                } else {
                    "Could not add pack to WhatsApp"
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun launchExportToWhatsApp(packWithStickers: StickerPackWithStickers) {
        if (packWithStickers.stickers.size < 3) {
            Toast.makeText(context, "WhatsApp requires at least 3 stickers in a pack. Current count: ${packWithStickers.stickers.size}", Toast.LENGTH_LONG).show()
            return
        }

        // Self-test ContentProvider locally first
        val authority = "${context.packageName}.stickercontentprovider"
        val testUri = Uri.parse("content://$authority/metadata")
        try {
            val cursor = context.contentResolver.query(testUri, null, null, null, null)
            if (cursor != null) {
                Log.d("SelfTest", "Self-test metadata query count=${cursor.count}")
                cursor.close()
            } else {
                Log.e("SelfTest", "Self-test query returned NULL")
            }
        } catch (e: Exception) {
            Log.e("SelfTest", "Self-test query exception: ${e.localizedMessage}")
        }

        val intent = WhatsAppStickerHelper.createAddPackIntent(
            context,
            packWithStickers.pack.identifier,
            packWithStickers.pack.name
        )

        try {
            whatsappAddLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not launch WhatsApp: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    LaunchedEffect(pendingImportUri) {
        pendingImportUri?.let { uri ->
            viewModel.importPackFromZip(uri) { packId ->
                if (packId != null) {
                    navController.navigate("pack_detail/$packId")
                }
                onImportHandled()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "pack_list",
        enterTransition = { enterAnim },
        exitTransition = { exitAnim },
        popEnterTransition = { enterAnim },
        popExitTransition = { exitAnim }
    ) {
        composable("pack_list") {
            PackListScreen(
                packs = packs,
                onPackClick = { packId ->
                    viewModel.selectPack(packId)
                    navController.navigate("pack_detail/$packId")
                },
                onCreatePackClick = { showCreateDialog = true },
                onImportClick = { showImportDialog = true },
                onAboutClick = {
                    navController.navigate("about_settings")
                },
                onExportToWhatsApp = { pack ->
                    launchExportToWhatsApp(pack)
                },
                onDeletePack = { packId ->
                    viewModel.deletePack(packId)
                },
                onAddStickerToPack = { packId, imageUri ->
                    viewModel.addStickerToPack(packId, imageUri, emojis = "😀", cropCircle = false)
                },
                onSharePack = { pack ->
                    StickerPackExporter.sharePack(context, pack)
                },
                onExportAllPacks = {
                    StickerPackExporter.shareAllPacks(context, packs)
                }
            )
        }

        composable(
            route = "pack_detail/{packId}",
            arguments = listOf(navArgument("packId") { type = NavType.StringType })
        ) { backStackEntry ->
            val packId = backStackEntry.arguments?.getString("packId")
            LaunchedEffect(packId) {
                if (packId != null) viewModel.selectPack(packId)
            }

            PackDetailScreen(
                packWithStickers = selectedPack,
                onBackClick = { navController.popBackStack() },
                onAddStickerUriSelected = { imageUri ->
                    if (packId != null) {
                        viewModel.addStickerToPack(packId, imageUri, emojis = "😀", cropCircle = false)
                    }
                },
                onExportToWhatsApp = {
                    selectedPack?.let { pack ->
                        launchExportToWhatsApp(pack)
                    }
                },
                onDeleteSticker = { sticker ->
                    viewModel.deleteSticker(sticker)
                },
                onUpdatePackMetadata = { name, publisher ->
                    if (packId != null) {
                        viewModel.updatePackMetadata(packId, name, publisher)
                    }
                },
                onSharePack = {
                    selectedPack?.let { pack ->
                        StickerPackExporter.sharePack(context, pack)
                    }
                }
            )
        }

        composable("about_settings") {
            AboutSettingsScreen(
                onNavigateToPacks = {
                    navController.navigate("pack_list") {
                        popUpTo("pack_list") { inclusive = true }
                    }
                },
                onViewLogsClick = {
                    logsContent = StickerContentProvider.getDebugLogs(context)
                    showLogsDialog = true
                }
            )
        }
    }

    // Dialogs
    if (showCreateDialog) {
        CreatePackDialog(
            onDismiss = { showCreateDialog = false },
            onCreatePack = { name, publisher, trayUri ->
                viewModel.createPack(name, publisher, trayUri)
            }
        )
    }

    if (showImportDialog) {
        ImportPackDialog(
            onDismiss = { showImportDialog = false },
            onImportFile = { uri ->
                viewModel.importPackFromZip(uri) { packId ->
                    if (packId != null) {
                        navController.navigate("pack_detail/$packId")
                    }
                }
            }
        )
    }

    if (showLogsDialog) {
        AlertDialog(
            onDismissRequest = { showLogsDialog = false },
            title = { Text("Provider Debug Logs") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (logsContent.isBlank()) "No logs recorded." else logsContent,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Sticker App Logs", logsContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Copy")
                    }

                    Button(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, logsContent)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Debug Logs")
                        context.startActivity(shareIntent)
                    }) {
                        Text("Share")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
