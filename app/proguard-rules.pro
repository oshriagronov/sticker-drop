# Keep Room Entity & DAO classes
-keep class com.stickerpack.maker.data.** { *; }
-keep class androidx.room.** { *; }

# Keep Gson serialization models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep StickerContentProvider
-keep class com.stickerpack.maker.StickerContentProvider { *; }
