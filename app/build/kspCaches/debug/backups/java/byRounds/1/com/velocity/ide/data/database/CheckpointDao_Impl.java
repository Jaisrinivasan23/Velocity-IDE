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
public final class CheckpointDao_Impl implements CheckpointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CheckpointEntity> __insertionAdapterOfCheckpointEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCheckpointById;

  public CheckpointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCheckpointEntity = new EntityInsertionAdapter<CheckpointEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `checkpoints` (`id`,`projectId`,`title`,`timestamp`,`backupPath`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CheckpointEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getProjectId());
        statement.bindString(3, entity.getTitle());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindString(5, entity.getBackupPath());
      }
    };
    this.__preparedStmtOfDeleteCheckpointById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM checkpoints WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCheckpoint(final CheckpointEntity checkpoint,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCheckpointEntity.insert(checkpoint);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCheckpointById(final String checkpointId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCheckpointById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, checkpointId);
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
          __preparedStmtOfDeleteCheckpointById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CheckpointEntity>> getCheckpointsForProjectFlow(final String projectId) {
    final String _sql = "SELECT * FROM checkpoints WHERE projectId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"checkpoints"}, new Callable<List<CheckpointEntity>>() {
      @Override
      @NonNull
      public List<CheckpointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfBackupPath = CursorUtil.getColumnIndexOrThrow(_cursor, "backupPath");
          final List<CheckpointEntity> _result = new ArrayList<CheckpointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CheckpointEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpBackupPath;
            _tmpBackupPath = _cursor.getString(_cursorIndexOfBackupPath);
            _item = new CheckpointEntity(_tmpId,_tmpProjectId,_tmpTitle,_tmpTimestamp,_tmpBackupPath);
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
  public Object getCheckpointById(final String checkpointId,
      final Continuation<? super CheckpointEntity> $completion) {
    final String _sql = "SELECT * FROM checkpoints WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, checkpointId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CheckpointEntity>() {
      @Override
      @Nullable
      public CheckpointEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfBackupPath = CursorUtil.getColumnIndexOrThrow(_cursor, "backupPath");
          final CheckpointEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpBackupPath;
            _tmpBackupPath = _cursor.getString(_cursorIndexOfBackupPath);
            _result = new CheckpointEntity(_tmpId,_tmpProjectId,_tmpTitle,_tmpTimestamp,_tmpBackupPath);
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
