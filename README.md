# StickerDrop 🎨📱

**StickerDrop** is a modern, native Android application built with **Jetpack Compose** and **Room** for creating, customizing, managing, and exporting custom sticker packs directly to **WhatsApp**.

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE.txt)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

---

## ✨ Features

- 🗂️ **My Packs Dashboard**: Browse active sticker packs with live preview rows, sticker counts, and one-tap "Add to WhatsApp" integration.
- 🎨 **Pack Editor**: Add stickers from gallery or camera, crop/edit photos, rename packs, reorder stickers, and customize metadata.
- ➕ **Pack Creator**: Quickly initialize new sticker packs with custom titles, author metadata, and tray icons.
- 📦 **Custom File Import & Export**: Export packs to `.stickerpack` files to share directly with friends or backup locally.
- ⚡ **WhatsApp ContentProvider Integration**: Fully compliant with the WhatsApp Sticker API specification (3–30 stickers per pack, 512x512 WEBP/PNG format, 96x96 WEBP tray icon, custom `ContentProvider` IPC).
- 🌙 **Vibrant Noir Design System**: Sleek dark-mode aesthetic with Spring Mint (`#00F5A0`) highlights, modern typography, and responsive micro-interactions.
- 🔓 **100% Open Source & Private**: Completely free, zero ads, and local-first image processing on your device.

---

## 📐 Architecture & Tech Stack

- **Language**: 100% Kotlin
- **Min SDK**: 24 (Android 7.0 Nougat) | **Target SDK**: 35 (Android 15)
- **UI Framework**: Jetpack Compose (Material 3)
- **Database / Persistence**: Room (`StickerDatabase`, `StickerPackDao`, `StickerDao`)
- **Image Processing & Loading**: Coil (`io.coil-kt:coil-compose`), Android Bitmap / Canvas utilities
- **WhatsApp IPC Integration**: `StickerContentProvider`
- **JSON Parsing**: Gson

---

## 📂 Project Structure

```text
com.stickerpack.maker/
├── MainActivity.kt               # Navigation Host & App Entrypoint
├── StickerContentProvider.kt     # Custom ContentProvider for WhatsApp IPC
├── WhatsAppStickerHelper.kt      # Export Intent Helper
├── data/
│   ├── db/                       # Room Database & DAOs
│   └── model/                    # Data models (StickerPack, Sticker)
├── ui/
│   ├── components/               # Shared UI Components (Pills, Dialogs, Cards)
│   ├── screens/                  # Compose Screens (Dashboard, Editor, About)
│   └── theme/                    # Color, Type, and Theme definitions
└── util/                         # Image processing, crop & file helpers
```

---

## 🚀 Getting Started

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (Jellyfish or newer)
- JDK 17+
- Android Device or Emulator running Android 7.0+ (API 24+) with WhatsApp installed

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/oshriagronov/sticker-drop.git
   cd sticker-drop
   ```
2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
3. Install on your connected device:
   ```bash
   ./gradlew installDebug
   ```

---

## 📱 Exporting to WhatsApp

1. Open **StickerDrop**.
2. Tap **+** to create a new pack (minimum 3 stickers required for WhatsApp).
3. Add images from gallery or capture photos.
4. Tap **Add to WhatsApp**.
5. Accept the prompt in WhatsApp, and enjoy sending your stickers!

---

## ⚙️ CI/CD & Automated Signed APKs

**StickerDrop** includes a GitHub Actions workflow (`.github/workflows/cd.yml`) that automatically builds and outputs a signed release APK (`app-release.apk`) on every commit pushed to GitHub.

### Setting up GitHub Repository Secrets
To enable release key signing in GitHub Actions, navigate to **Settings → Secrets and variables → Actions** in your GitHub repository and add the following secrets:

| Secret Name | Description | Command to Generate |
| :--- | :--- | :--- |
| `KEYSTORE_BASE64` | Base64 representation of your `.jks` file | `base64 -i release-key.jks \| pbcopy` |
| `KEYSTORE_PASSWORD` | Password for the keystore | e.g. `your_keystore_password` |
| `KEY_ALIAS` | Key alias inside the keystore | e.g. `stickerdrop_key` |
| `KEY_PASSWORD` | Password for the key alias | e.g. `your_key_password` |

After pushing code or triggering the workflow manually via `workflow_dispatch`, download your signed APK from the **Actions** tab under **Artifacts → stickerdrop-release-apk**.

---

## 🤝 Contributing

Contributions are welcome! Feel free to open issues, submit pull requests, or suggest new features on [GitHub](https://github.com/oshriagronov/sticker-drop).

---

## 📄 License

This project is licensed under the [MIT License](LICENSE.txt).
