package com.stickerpack.maker.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stickerpack.maker.WhatsAppStickerHelper
import com.stickerpack.maker.data.StickerEntity
import com.stickerpack.maker.data.StickerPackWithStickers
import com.stickerpack.maker.data.StickerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StickerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StickerRepository(application)

    val packsState: StateFlow<List<StickerPackWithStickers>> = repository.packsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedPackId = MutableStateFlow<String?>(null)
    val selectedPackState: StateFlow<StickerPackWithStickers?> = _selectedPackId
        .combine(packsState) { id, packs ->
            packs.find { it.pack.identifier == id }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun selectPack(identifier: String?) {
        _selectedPackId.value = identifier
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun createPack(name: String, publisher: String, trayUri: Uri?) {
        if (name.isBlank() || publisher.isBlank()) {
            _userMessage.value = "Pack Name and Publisher cannot be empty"
            return
        }
        viewModelScope.launch {
            val newPackId = repository.createNewPack(name, publisher, trayUri)
            if (newPackId != null) {
                _selectedPackId.value = newPackId
                _userMessage.value = "Sticker pack created!"
            } else {
                _userMessage.value = "Failed to create sticker pack."
            }
        }
    }

    fun addStickerToPack(
        packIdentifier: String,
        imageUri: Uri,
        emojis: String = "😀",
        cropCircle: Boolean = false
    ) {
        viewModelScope.launch {
            val success = repository.addStickerToPack(
                packIdentifier = packIdentifier,
                imageUri = imageUri,
                emojis = emojis,
                cropCircle = cropCircle
            )
            if (success) {
                _userMessage.value = "Sticker added to pack! Tap 'Add to WhatsApp' to update."
            } else {
                _userMessage.value = "Failed to add sticker. Ensure image is valid."
            }
        }
    }

    fun deleteSticker(sticker: StickerEntity) {
        viewModelScope.launch {
            repository.deleteSticker(sticker)
            _userMessage.value = "Sticker deleted"
        }
    }

    fun deletePack(packIdentifier: String) {
        viewModelScope.launch {
            repository.deletePack(packIdentifier)
            if (_selectedPackId.value == packIdentifier) {
                _selectedPackId.value = null
            }
            _userMessage.value = "Sticker pack deleted"
        }
    }

    fun importPackFromJson(uri: Uri) {
        viewModelScope.launch {
            val packId = repository.importPackFromJson(uri)
            if (packId != null) {
                _selectedPackId.value = packId
                _userMessage.value = "Sticker pack imported successfully!"
            } else {
                _userMessage.value = "Failed to import pack from JSON."
            }
        }
    }

    fun importPackFromZip(uri: Uri) {
        viewModelScope.launch {
            val packId = repository.importPackFromZip(uri)
            if (packId != null) {
                _selectedPackId.value = packId
                _userMessage.value = "Sticker pack imported from file!"
            } else {
                _userMessage.value = "Failed to import pack. Ensure file is a valid .wastickers or zip package."
            }
        }
    }


}
