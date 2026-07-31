# Privacy Policy for StickerDrop

**Last Updated:** July 31, 2026

Welcome to **StickerDrop** ("we," "our," or "us"). We are committed to protecting your privacy and ensuring you have a secure experience when using our sticker creation and export application.

---

## 1. Overview & Core Privacy Principles

StickerDrop is an **open-source, privacy-first mobile application**. 
* **100% On-Device Data Storage:** All sticker packs, image edits, metadata, and app settings are stored locally on your device.
* **No External Servers or Telemetry:** We do not collect, transmit, track, or harvest any personal information, image contents, or analytics data to external servers.

---

## 2. Information Collection and Use

### Device Permissions Required:
To enable sticker creation and management, StickerDrop requests access to specific device capabilities:

1. **Photos & Media (`READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE`):**
   * **Purpose:** Allows you to select images from your gallery to turn into custom stickers.
   * **Usage:** Images selected by you are processed locally on your device (e.g., cropping, background removal). No images are ever uploaded to any third-party server.

2. **Camera Access (Optional):**
   * **Purpose:** Allows you to take a photo directly within the app to create a sticker.
   * **Usage:** Photos taken are processed locally on your device.

---

## 3. Data Storage and Third-Party Sharing

* **Local Database:** Sticker metadata and processed sticker images (512x512 PNG/WEBP files) are saved in your device's isolated application storage using SQLite (Room Database).
* **WhatsApp Integration:** When you click "Add to WhatsApp," StickerDrop exposes your created sticker pack to WhatsApp via a local `ContentProvider` on your device as specified by WhatsApp's Sticker API. No data is shared with any other third party.

---

## 4. Open Source Transparency

StickerDrop is 100% open source. You can inspect our full codebase, security implementation, and data flows on GitHub:
[https://github.com/oshriagronov/sticker-drop](https://github.com/oshriagronov/sticker-drop)

---

## 5. Contact Us

If you have any questions or concerns regarding this Privacy Policy or StickerDrop's privacy practices, please open an issue on our GitHub repository:
[https://github.com/oshriagronov/sticker-drop/issues](https://github.com/oshriagronov/sticker-drop/issues)
