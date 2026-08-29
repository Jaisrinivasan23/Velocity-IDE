package com.velocity.ide.data.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProjectMemoryDao_Impl implements ProjectMemoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProjectMemoryEntity> __insertionAdapterOfProjectMemoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMemoryByKey;

  public ProjectMemoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProjectMemoryEntity = new EntityInsertionAdapter<ProjectMemoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `project_memories` (`id`,`projectId`,`key`,`value`,`updatedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProjectMemoryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getProjectId());
        statement.bindString(3, entity.getKey());
        statement.bindString(4, entity.getValue());
        statement.bindLong(5, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfDeleteMemoryByKey = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM project_memories WHERE projectId = ? AND `key` = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMemory(final ProjectMemoryEntity memory,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProjectMemoryEntity.insert(memory);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMemoryByKey(final String projectId, final String key,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMemoryByKey.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, projectId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, key);
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
          __preparedStmtOfDeleteMemoryByKey.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProjectMemoryEntity>> getMemoriesForProjectFlow(final String projectId) {
    final String _sql = "SELECT * FROM project_memories WHERE projectId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"project_memories"}, new Callable<List<ProjectMemoryEntity>>() {
      @Override
      @NonNull
      public List<ProjectMemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfKey = CursorUtil.getColumnIndexOrThrow(_cursor, "key");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ProjectMemoryEntity> _result = new ArrayList<ProjectMemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProjectMemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpKey;
            _tmpKey = _cursor.getString(_cursorIndexOfKey);
            final String _tmpValue;
            _tmpValue = _cursor.getString(_cursorIndexOfValue);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ProjectMemoryEntity(_tmpId,_tmpProjectId,_tmpKey,_tmpValue,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMemoriesForProject(final String projectId,
      final Continuation<? super List<ProjectMemoryEntity>> $completion) {
    final String _sql = "SELECT * FROM project_memories WHERE projectId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProjectMemoryEntity>>() {
      @Override
      @NonNull
      public List<ProjectMemoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfKey = CursorUtil.getColumnIndexOrThrow(_cursor, "key");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ProjectMemoryEntity> _result = new ArrayList<ProjectMemoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProjectMemoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpKey;
            _tmpKey = _cursor.getString(_cursorIndexOfKey);
            final String _tmpValue;
            _tmpValue = _cursor.getString(_cursorIndexOfValue);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ProjectMemoryEntity(_tmpId,_tmpProjectId,_tmpKey,_tmpValue,_tmpUpdatedAt);
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

  @Override
  public Object getMemoryByKey(final String projectId, final String key,
      final Continuation<? super ProjectMemoryEntity> $completion) {
    final String _sql = "SELECT * FROM project_memories WHERE projectId = ? AND `key` = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    _argIndex = 2;
    _statement.bindString(_argIndex, key);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProjectMemoryEntity>() {
      @Override
      @Nullable
      public ProjectMemoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfKey = CursorUtil.getColumnIndexOrThrow(_cursor, "key");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ProjectMemoryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpKey;
            _tmpKey = _cursor.getString(_cursorIndexOfKey);
            final String _tmpValue;
            _tmpValue = _cursor.getString(_cursorIndexOfValue);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ProjectMemoryEntity(_tmpId,_tmpProjectId,_tmpKey,_tmpValue,_tmpUpdatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
