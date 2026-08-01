# AGENTS.md - Developer & Agent Guide for StickerDrop

Welcome to **StickerDrop**! (Google Stitch Project: **StickerDrop**). This file provides project context, architectural guidelines, design system rules, and engineering instructions for human developers and AI assistants.

---

## 🎯 Project Overview & Goal

**StickerDrop** is an Android application designed for creating, editing, managing, and exporting custom sticker packs directly to **WhatsApp**.

### Key Features:
1. **My Packs Dashboard:** Browse active sticker packs with live preview rows, sticker counts, and instant "Add to WhatsApp" integration.
2. **Pack Editor:** Add stickers (via gallery or camera), crop/remove background, edit metadata, reorder, or remove stickers.
3. **Pack Creator:** Quickly initialize new sticker packs with custom title, author, and tray icon.
4. **WhatsApp Provider Integration:** Conforms to WhatsApp Sticker API specifications (3-30 stickers per pack, 512x512 PNG/WEBP format, tray icon 96x96 WEBP, custom ContentProvider).

---

## 📐 Architecture & Tech Stack

- **Platform:** Native Android (Kotlin, JDK 17, minSdk 24, targetSdk 35)
- **UI Framework:** Jetpack Compose (Material 3)
- **Database / Persistence:** Room Database (`StickerDatabase`, `StickerPackDao`, `StickerDao`)
- **Image Loading:** Coil (`io.coil-kt:coil-compose`)
- **Content Provider:** `StickerContentProvider` for WhatsApp IPC integration
- **JSON Serialization:** Gson (`com.google.code.gson:gson`)

---

## 🎨 Design System: Vibrant Noir

All UI components must strictly adhere to the **Vibrant Noir** design specification defined in [`design.md`](file:///Users/oshriagronov/Documents/Projects/sticker-pack/design.md).

### Core Design Rules:
1. **Dark Theme First:** Base background `#051424`, Card surface `#2A3439`, Sub-containers `#122131`.
2. **Accent Color:** `#00F5A0` (Spring Mint) for primary buttons, active states, and highlights.
3. **Typography Pair:**
   - Headings & UI text: **Plus Jakarta Sans**
   - Labels, counters, & badges: **JetBrains Mono**
4. **Shapes & Radii:**
   - Cards: `24.dp` rounded corners (`RoundedCornerShape(24.dp)`).
   - Thumbnails & Buttons: `12.dp` to `16.dp` rounded corners.
   - FABs & Nav Pills: Pill-shaped (`CircleShape` / `RoundedCornerShape(50)`).
5. **No Generic Placeholders:** All UI components should display realistic, styled content or graceful empty states matching the Google Stitch **StickerDrop** project mockups.

---

## 📂 Package Structure

```
com.stickerpack.maker/
├── MainActivity.kt               # Entrypoint & Compose Navigation Host
├── StickerContentProvider.kt     # WhatsApp Sticker IPC Provider
├── WhatsAppStickerHelper.kt      # WhatsApp export intent utility
├── data/
│   ├── db/                      # Room entities & DAOs
│   ├── model/                   # StickerPack & Sticker data models
│   └── repository/              # Data repository layer
├── ui/
│   ├── theme/                   # Color.kt, Theme.kt, Type.kt
│   ├── components/              # Shared UI components (Pills, Cards, Dialogs)
│   └── screens/                 # Compose Screen Composables
│       ├── PackListScreen.kt        # My Packs Dashboard
│       ├── PackDetailScreen.kt      # Pack Editor View
│       ├── CreatePackDialog.kt      # Create New Pack Screen
│       └── AboutSettingsScreen.kt   # About & Settings Screen
└── util/                        # Image processing & file utilities
```

---

## 🛠️ Verification & Build Commands

Before submitting or completing any changes, ensure the Android project compiles cleanly:

```bash
./gradlew assembleDebug
```

If adding unit or UI tests, run:
```bash
./gradlew test
```

---

## 🤖 Instructions for AI Agents

- **Always verify code build:** Never declare success without running `./gradlew assembleDebug`.
- **Maintain design consistency:** Always reference color tokens from `Color.kt` and styling guidelines in `design.md`. Do not hardcode random hex values in composables.
- **Preserve WhatsApp Compatibility:** Ensure sticker dimensions (512x512) and tray icon constraints (96x96) remain valid when updating sticker logic.
