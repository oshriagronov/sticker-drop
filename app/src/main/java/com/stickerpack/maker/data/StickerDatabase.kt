package com.stickerpack.maker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StickerPackEntity::class, StickerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StickerDatabase : RoomDatabase() {
    abstract fun stickerDao(): StickerDao

    companion object {
        @Volatile
        private var INSTANCE: StickerDatabase? = null

        fun getDatabase(context: Context): StickerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StickerDatabase::class.java,
                    "sticker_pack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
