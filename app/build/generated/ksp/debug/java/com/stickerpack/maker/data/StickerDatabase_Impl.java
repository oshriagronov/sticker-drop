package com.stickerpack.maker.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StickerDatabase_Impl extends StickerDatabase {
  private volatile StickerDao _stickerDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `sticker_packs` (`identifier` TEXT NOT NULL, `name` TEXT NOT NULL, `publisher` TEXT NOT NULL, `trayImageFileName` TEXT NOT NULL, `publisherEmail` TEXT NOT NULL, `publisherWebsite` TEXT NOT NULL, `privacyPolicyWebsite` TEXT NOT NULL, `licenseAgreementWebsite` TEXT NOT NULL, `imageDataVersion` INTEGER NOT NULL, `avoidCache` INTEGER NOT NULL, `animatedStickerPack` INTEGER NOT NULL, PRIMARY KEY(`identifier`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `stickers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packIdentifier` TEXT NOT NULL, `fileName` TEXT NOT NULL, `emojis` TEXT NOT NULL, FOREIGN KEY(`packIdentifier`) REFERENCES `sticker_packs`(`identifier`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_stickers_packIdentifier` ON `stickers` (`packIdentifier`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'adc6a65abea5da8a62b6df4edf227760')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `sticker_packs`");
        db.execSQL("DROP TABLE IF EXISTS `stickers`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsStickerPacks = new HashMap<String, TableInfo.Column>(11);
        _columnsStickerPacks.put("identifier", new TableInfo.Column("identifier", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("publisher", new TableInfo.Column("publisher", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("trayImageFileName", new TableInfo.Column("trayImageFileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("publisherEmail", new TableInfo.Column("publisherEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("publisherWebsite", new TableInfo.Column("publisherWebsite", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("privacyPolicyWebsite", new TableInfo.Column("privacyPolicyWebsite", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("licenseAgreementWebsite", new TableInfo.Column("licenseAgreementWebsite", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("imageDataVersion", new TableInfo.Column("imageDataVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("avoidCache", new TableInfo.Column("avoidCache", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickerPacks.put("animatedStickerPack", new TableInfo.Column("animatedStickerPack", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStickerPacks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStickerPacks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStickerPacks = new TableInfo("sticker_packs", _columnsStickerPacks, _foreignKeysStickerPacks, _indicesStickerPacks);
        final TableInfo _existingStickerPacks = TableInfo.read(db, "sticker_packs");
        if (!_infoStickerPacks.equals(_existingStickerPacks)) {
          return new RoomOpenHelper.ValidationResult(false, "sticker_packs(com.stickerpack.maker.data.StickerPackEntity).\n"
                  + " Expected:\n" + _infoStickerPacks + "\n"
                  + " Found:\n" + _existingStickerPacks);
        }
        final HashMap<String, TableInfo.Column> _columnsStickers = new HashMap<String, TableInfo.Column>(4);
        _columnsStickers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickers.put("packIdentifier", new TableInfo.Column("packIdentifier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickers.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStickers.put("emojis", new TableInfo.Column("emojis", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStickers = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysStickers.add(new TableInfo.ForeignKey("sticker_packs", "CASCADE", "NO ACTION", Arrays.asList("packIdentifier"), Arrays.asList("identifier")));
        final HashSet<TableInfo.Index> _indicesStickers = new HashSet<TableInfo.Index>(1);
        _indicesStickers.add(new TableInfo.Index("index_stickers_packIdentifier", false, Arrays.asList("packIdentifier"), Arrays.asList("ASC")));
        final TableInfo _infoStickers = new TableInfo("stickers", _columnsStickers, _foreignKeysStickers, _indicesStickers);
        final TableInfo _existingStickers = TableInfo.read(db, "stickers");
        if (!_infoStickers.equals(_existingStickers)) {
          return new RoomOpenHelper.ValidationResult(false, "stickers(com.stickerpack.maker.data.StickerEntity).\n"
                  + " Expected:\n" + _infoStickers + "\n"
                  + " Found:\n" + _existingStickers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "adc6a65abea5da8a62b6df4edf227760", "678047ae026a38c701b799de31ebafc6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "sticker_packs","stickers");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `sticker_packs`");
      _db.execSQL("DELETE FROM `stickers`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(StickerDao.class, StickerDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public StickerDao stickerDao() {
    if (_stickerDao != null) {
      return _stickerDao;
    } else {
      synchronized(this) {
        if(_stickerDao == null) {
          _stickerDao = new StickerDao_Impl(this);
        }
        return _stickerDao;
      }
    }
  }
}
