package com.stickerpack.maker.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StickerDao_Impl implements StickerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StickerPackEntity> __insertionAdapterOfStickerPackEntity;

  private final EntityInsertionAdapter<StickerEntity> __insertionAdapterOfStickerEntity;

  private final EntityDeletionOrUpdateAdapter<StickerEntity> __deletionAdapterOfStickerEntity;

  private final EntityDeletionOrUpdateAdapter<StickerPackEntity> __updateAdapterOfStickerPackEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteStickerById;

  private final SharedSQLiteStatement __preparedStmtOfDeletePackById;

  private final SharedSQLiteStatement __preparedStmtOfIncrementImageDataVersion;

  public StickerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStickerPackEntity = new EntityInsertionAdapter<StickerPackEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sticker_packs` (`identifier`,`name`,`publisher`,`trayImageFileName`,`publisherEmail`,`publisherWebsite`,`privacyPolicyWebsite`,`licenseAgreementWebsite`,`imageDataVersion`,`avoidCache`,`animatedStickerPack`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StickerPackEntity entity) {
        statement.bindString(1, entity.getIdentifier());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getPublisher());
        statement.bindString(4, entity.getTrayImageFileName());
        statement.bindString(5, entity.getPublisherEmail());
        statement.bindString(6, entity.getPublisherWebsite());
        statement.bindString(7, entity.getPrivacyPolicyWebsite());
        statement.bindString(8, entity.getLicenseAgreementWebsite());
        statement.bindLong(9, entity.getImageDataVersion());
        final int _tmp = entity.getAvoidCache() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.getAnimatedStickerPack() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
      }
    };
    this.__insertionAdapterOfStickerEntity = new EntityInsertionAdapter<StickerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `stickers` (`id`,`packIdentifier`,`fileName`,`emojis`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StickerEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackIdentifier());
        statement.bindString(3, entity.getFileName());
        statement.bindString(4, entity.getEmojis());
      }
    };
    this.__deletionAdapterOfStickerEntity = new EntityDeletionOrUpdateAdapter<StickerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `stickers` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StickerEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfStickerPackEntity = new EntityDeletionOrUpdateAdapter<StickerPackEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sticker_packs` SET `identifier` = ?,`name` = ?,`publisher` = ?,`trayImageFileName` = ?,`publisherEmail` = ?,`publisherWebsite` = ?,`privacyPolicyWebsite` = ?,`licenseAgreementWebsite` = ?,`imageDataVersion` = ?,`avoidCache` = ?,`animatedStickerPack` = ? WHERE `identifier` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StickerPackEntity entity) {
        statement.bindString(1, entity.getIdentifier());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getPublisher());
        statement.bindString(4, entity.getTrayImageFileName());
        statement.bindString(5, entity.getPublisherEmail());
        statement.bindString(6, entity.getPublisherWebsite());
        statement.bindString(7, entity.getPrivacyPolicyWebsite());
        statement.bindString(8, entity.getLicenseAgreementWebsite());
        statement.bindLong(9, entity.getImageDataVersion());
        final int _tmp = entity.getAvoidCache() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.getAnimatedStickerPack() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindString(12, entity.getIdentifier());
      }
    };
    this.__preparedStmtOfDeleteStickerById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM stickers WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePackById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sticker_packs WHERE identifier = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementImageDataVersion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sticker_packs SET imageDataVersion = imageDataVersion + 1 WHERE identifier = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPack(final StickerPackEntity pack,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStickerPackEntity.insert(pack);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSticker(final StickerEntity sticker,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStickerEntity.insert(sticker);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSticker(final StickerEntity sticker,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfStickerEntity.handle(sticker);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePack(final StickerPackEntity pack,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfStickerPackEntity.handle(pack);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteStickerById(final long stickerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteStickerById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, stickerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteStickerById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePackById(final String identifier,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePackById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, identifier);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePackById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementImageDataVersion(final String identifier,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementImageDataVersion.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, identifier);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementImageDataVersion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StickerPackWithStickers>> getAllPacksWithStickersFlow() {
    final String _sql = "SELECT * FROM sticker_packs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"stickers",
        "sticker_packs"}, new Callable<List<StickerPackWithStickers>>() {
      @Override
      @NonNull
      public List<StickerPackWithStickers> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "identifier");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
            final int _cursorIndexOfTrayImageFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "trayImageFileName");
            final int _cursorIndexOfPublisherEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherEmail");
            final int _cursorIndexOfPublisherWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherWebsite");
            final int _cursorIndexOfPrivacyPolicyWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "privacyPolicyWebsite");
            final int _cursorIndexOfLicenseAgreementWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseAgreementWebsite");
            final int _cursorIndexOfImageDataVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "imageDataVersion");
            final int _cursorIndexOfAvoidCache = CursorUtil.getColumnIndexOrThrow(_cursor, "avoidCache");
            final int _cursorIndexOfAnimatedStickerPack = CursorUtil.getColumnIndexOrThrow(_cursor, "animatedStickerPack");
            final ArrayMap<String, ArrayList<StickerEntity>> _collectionStickers = new ArrayMap<String, ArrayList<StickerEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfIdentifier);
              if (!_collectionStickers.containsKey(_tmpKey)) {
                _collectionStickers.put(_tmpKey, new ArrayList<StickerEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipstickersAscomStickerpackMakerDataStickerEntity(_collectionStickers);
            final List<StickerPackWithStickers> _result = new ArrayList<StickerPackWithStickers>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final StickerPackWithStickers _item;
              final StickerPackEntity _tmpPack;
              final String _tmpIdentifier;
              _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpPublisher;
              _tmpPublisher = _cursor.getString(_cursorIndexOfPublisher);
              final String _tmpTrayImageFileName;
              _tmpTrayImageFileName = _cursor.getString(_cursorIndexOfTrayImageFileName);
              final String _tmpPublisherEmail;
              _tmpPublisherEmail = _cursor.getString(_cursorIndexOfPublisherEmail);
              final String _tmpPublisherWebsite;
              _tmpPublisherWebsite = _cursor.getString(_cursorIndexOfPublisherWebsite);
              final String _tmpPrivacyPolicyWebsite;
              _tmpPrivacyPolicyWebsite = _cursor.getString(_cursorIndexOfPrivacyPolicyWebsite);
              final String _tmpLicenseAgreementWebsite;
              _tmpLicenseAgreementWebsite = _cursor.getString(_cursorIndexOfLicenseAgreementWebsite);
              final int _tmpImageDataVersion;
              _tmpImageDataVersion = _cursor.getInt(_cursorIndexOfImageDataVersion);
              final boolean _tmpAvoidCache;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfAvoidCache);
              _tmpAvoidCache = _tmp != 0;
              final boolean _tmpAnimatedStickerPack;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfAnimatedStickerPack);
              _tmpAnimatedStickerPack = _tmp_1 != 0;
              _tmpPack = new StickerPackEntity(_tmpIdentifier,_tmpName,_tmpPublisher,_tmpTrayImageFileName,_tmpPublisherEmail,_tmpPublisherWebsite,_tmpPrivacyPolicyWebsite,_tmpLicenseAgreementWebsite,_tmpImageDataVersion,_tmpAvoidCache,_tmpAnimatedStickerPack);
              final ArrayList<StickerEntity> _tmpStickersCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfIdentifier);
              _tmpStickersCollection = _collectionStickers.get(_tmpKey_1);
              _item = new StickerPackWithStickers(_tmpPack,_tmpStickersCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllPacksWithStickersDirect(
      final Continuation<? super List<StickerPackWithStickers>> $completion) {
    final String _sql = "SELECT * FROM sticker_packs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<List<StickerPackWithStickers>>() {
      @Override
      @NonNull
      public List<StickerPackWithStickers> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "identifier");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
            final int _cursorIndexOfTrayImageFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "trayImageFileName");
            final int _cursorIndexOfPublisherEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherEmail");
            final int _cursorIndexOfPublisherWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherWebsite");
            final int _cursorIndexOfPrivacyPolicyWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "privacyPolicyWebsite");
            final int _cursorIndexOfLicenseAgreementWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseAgreementWebsite");
            final int _cursorIndexOfImageDataVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "imageDataVersion");
            final int _cursorIndexOfAvoidCache = CursorUtil.getColumnIndexOrThrow(_cursor, "avoidCache");
            final int _cursorIndexOfAnimatedStickerPack = CursorUtil.getColumnIndexOrThrow(_cursor, "animatedStickerPack");
            final ArrayMap<String, ArrayList<StickerEntity>> _collectionStickers = new ArrayMap<String, ArrayList<StickerEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfIdentifier);
              if (!_collectionStickers.containsKey(_tmpKey)) {
                _collectionStickers.put(_tmpKey, new ArrayList<StickerEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipstickersAscomStickerpackMakerDataStickerEntity(_collectionStickers);
            final List<StickerPackWithStickers> _result = new ArrayList<StickerPackWithStickers>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final StickerPackWithStickers _item;
              final StickerPackEntity _tmpPack;
              final String _tmpIdentifier;
              _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpPublisher;
              _tmpPublisher = _cursor.getString(_cursorIndexOfPublisher);
              final String _tmpTrayImageFileName;
              _tmpTrayImageFileName = _cursor.getString(_cursorIndexOfTrayImageFileName);
              final String _tmpPublisherEmail;
              _tmpPublisherEmail = _cursor.getString(_cursorIndexOfPublisherEmail);
              final String _tmpPublisherWebsite;
              _tmpPublisherWebsite = _cursor.getString(_cursorIndexOfPublisherWebsite);
              final String _tmpPrivacyPolicyWebsite;
              _tmpPrivacyPolicyWebsite = _cursor.getString(_cursorIndexOfPrivacyPolicyWebsite);
              final String _tmpLicenseAgreementWebsite;
              _tmpLicenseAgreementWebsite = _cursor.getString(_cursorIndexOfLicenseAgreementWebsite);
              final int _tmpImageDataVersion;
              _tmpImageDataVersion = _cursor.getInt(_cursorIndexOfImageDataVersion);
              final boolean _tmpAvoidCache;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfAvoidCache);
              _tmpAvoidCache = _tmp != 0;
              final boolean _tmpAnimatedStickerPack;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfAnimatedStickerPack);
              _tmpAnimatedStickerPack = _tmp_1 != 0;
              _tmpPack = new StickerPackEntity(_tmpIdentifier,_tmpName,_tmpPublisher,_tmpTrayImageFileName,_tmpPublisherEmail,_tmpPublisherWebsite,_tmpPrivacyPolicyWebsite,_tmpLicenseAgreementWebsite,_tmpImageDataVersion,_tmpAvoidCache,_tmpAnimatedStickerPack);
              final ArrayList<StickerEntity> _tmpStickersCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfIdentifier);
              _tmpStickersCollection = _collectionStickers.get(_tmpKey_1);
              _item = new StickerPackWithStickers(_tmpPack,_tmpStickersCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPackWithStickersById(final String identifier,
      final Continuation<? super StickerPackWithStickers> $completion) {
    final String _sql = "SELECT * FROM sticker_packs WHERE identifier = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, identifier);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<StickerPackWithStickers>() {
      @Override
      @Nullable
      public StickerPackWithStickers call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "identifier");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
            final int _cursorIndexOfTrayImageFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "trayImageFileName");
            final int _cursorIndexOfPublisherEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherEmail");
            final int _cursorIndexOfPublisherWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherWebsite");
            final int _cursorIndexOfPrivacyPolicyWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "privacyPolicyWebsite");
            final int _cursorIndexOfLicenseAgreementWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseAgreementWebsite");
            final int _cursorIndexOfImageDataVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "imageDataVersion");
            final int _cursorIndexOfAvoidCache = CursorUtil.getColumnIndexOrThrow(_cursor, "avoidCache");
            final int _cursorIndexOfAnimatedStickerPack = CursorUtil.getColumnIndexOrThrow(_cursor, "animatedStickerPack");
            final ArrayMap<String, ArrayList<StickerEntity>> _collectionStickers = new ArrayMap<String, ArrayList<StickerEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfIdentifier);
              if (!_collectionStickers.containsKey(_tmpKey)) {
                _collectionStickers.put(_tmpKey, new ArrayList<StickerEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipstickersAscomStickerpackMakerDataStickerEntity(_collectionStickers);
            final StickerPackWithStickers _result;
            if (_cursor.moveToFirst()) {
              final StickerPackEntity _tmpPack;
              final String _tmpIdentifier;
              _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpPublisher;
              _tmpPublisher = _cursor.getString(_cursorIndexOfPublisher);
              final String _tmpTrayImageFileName;
              _tmpTrayImageFileName = _cursor.getString(_cursorIndexOfTrayImageFileName);
              final String _tmpPublisherEmail;
              _tmpPublisherEmail = _cursor.getString(_cursorIndexOfPublisherEmail);
              final String _tmpPublisherWebsite;
              _tmpPublisherWebsite = _cursor.getString(_cursorIndexOfPublisherWebsite);
              final String _tmpPrivacyPolicyWebsite;
              _tmpPrivacyPolicyWebsite = _cursor.getString(_cursorIndexOfPrivacyPolicyWebsite);
              final String _tmpLicenseAgreementWebsite;
              _tmpLicenseAgreementWebsite = _cursor.getString(_cursorIndexOfLicenseAgreementWebsite);
              final int _tmpImageDataVersion;
              _tmpImageDataVersion = _cursor.getInt(_cursorIndexOfImageDataVersion);
              final boolean _tmpAvoidCache;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfAvoidCache);
              _tmpAvoidCache = _tmp != 0;
              final boolean _tmpAnimatedStickerPack;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfAnimatedStickerPack);
              _tmpAnimatedStickerPack = _tmp_1 != 0;
              _tmpPack = new StickerPackEntity(_tmpIdentifier,_tmpName,_tmpPublisher,_tmpTrayImageFileName,_tmpPublisherEmail,_tmpPublisherWebsite,_tmpPrivacyPolicyWebsite,_tmpLicenseAgreementWebsite,_tmpImageDataVersion,_tmpAvoidCache,_tmpAnimatedStickerPack);
              final ArrayList<StickerEntity> _tmpStickersCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfIdentifier);
              _tmpStickersCollection = _collectionStickers.get(_tmpKey_1);
              _result = new StickerPackWithStickers(_tmpPack,_tmpStickersCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPackById(final String identifier,
      final Continuation<? super StickerPackEntity> $completion) {
    final String _sql = "SELECT * FROM sticker_packs WHERE identifier = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, identifier);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StickerPackEntity>() {
      @Override
      @Nullable
      public StickerPackEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "identifier");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfTrayImageFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "trayImageFileName");
          final int _cursorIndexOfPublisherEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherEmail");
          final int _cursorIndexOfPublisherWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherWebsite");
          final int _cursorIndexOfPrivacyPolicyWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "privacyPolicyWebsite");
          final int _cursorIndexOfLicenseAgreementWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseAgreementWebsite");
          final int _cursorIndexOfImageDataVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "imageDataVersion");
          final int _cursorIndexOfAvoidCache = CursorUtil.getColumnIndexOrThrow(_cursor, "avoidCache");
          final int _cursorIndexOfAnimatedStickerPack = CursorUtil.getColumnIndexOrThrow(_cursor, "animatedStickerPack");
          final StickerPackEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpIdentifier;
            _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPublisher;
            _tmpPublisher = _cursor.getString(_cursorIndexOfPublisher);
            final String _tmpTrayImageFileName;
            _tmpTrayImageFileName = _cursor.getString(_cursorIndexOfTrayImageFileName);
            final String _tmpPublisherEmail;
            _tmpPublisherEmail = _cursor.getString(_cursorIndexOfPublisherEmail);
            final String _tmpPublisherWebsite;
            _tmpPublisherWebsite = _cursor.getString(_cursorIndexOfPublisherWebsite);
            final String _tmpPrivacyPolicyWebsite;
            _tmpPrivacyPolicyWebsite = _cursor.getString(_cursorIndexOfPrivacyPolicyWebsite);
            final String _tmpLicenseAgreementWebsite;
            _tmpLicenseAgreementWebsite = _cursor.getString(_cursorIndexOfLicenseAgreementWebsite);
            final int _tmpImageDataVersion;
            _tmpImageDataVersion = _cursor.getInt(_cursorIndexOfImageDataVersion);
            final boolean _tmpAvoidCache;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAvoidCache);
            _tmpAvoidCache = _tmp != 0;
            final boolean _tmpAnimatedStickerPack;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAnimatedStickerPack);
            _tmpAnimatedStickerPack = _tmp_1 != 0;
            _result = new StickerPackEntity(_tmpIdentifier,_tmpName,_tmpPublisher,_tmpTrayImageFileName,_tmpPublisherEmail,_tmpPublisherWebsite,_tmpPrivacyPolicyWebsite,_tmpLicenseAgreementWebsite,_tmpImageDataVersion,_tmpAvoidCache,_tmpAnimatedStickerPack);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getStickersForPack(final String identifier,
      final Continuation<? super List<StickerEntity>> $completion) {
    final String _sql = "SELECT * FROM stickers WHERE packIdentifier = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, identifier);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StickerEntity>>() {
      @Override
      @NonNull
      public List<StickerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "packIdentifier");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfEmojis = CursorUtil.getColumnIndexOrThrow(_cursor, "emojis");
          final List<StickerEntity> _result = new ArrayList<StickerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StickerEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackIdentifier;
            _tmpPackIdentifier = _cursor.getString(_cursorIndexOfPackIdentifier);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpEmojis;
            _tmpEmojis = _cursor.getString(_cursorIndexOfEmojis);
            _item = new StickerEntity(_tmpId,_tmpPackIdentifier,_tmpFileName,_tmpEmojis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipstickersAscomStickerpackMakerDataStickerEntity(
      @NonNull final ArrayMap<String, ArrayList<StickerEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshipstickersAscomStickerpackMakerDataStickerEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`packIdentifier`,`fileName`,`emojis` FROM `stickers` WHERE `packIdentifier` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      _stmt.bindString(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "packIdentifier");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfPackIdentifier = 1;
      final int _cursorIndexOfFileName = 2;
      final int _cursorIndexOfEmojis = 3;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        _tmpKey = _cursor.getString(_itemKeyIndex);
        final ArrayList<StickerEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final StickerEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpPackIdentifier;
          _tmpPackIdentifier = _cursor.getString(_cursorIndexOfPackIdentifier);
          final String _tmpFileName;
          _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
          final String _tmpEmojis;
          _tmpEmojis = _cursor.getString(_cursorIndexOfEmojis);
          _item_1 = new StickerEntity(_tmpId,_tmpPackIdentifier,_tmpFileName,_tmpEmojis);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
