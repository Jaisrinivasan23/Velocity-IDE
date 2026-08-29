package com.velocity.ide.data.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
public final class AgentRunDao_Impl implements AgentRunDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AgentRunEntity> __insertionAdapterOfAgentRunEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRunsForProject;

  public AgentRunDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAgentRunEntity = new EntityInsertionAdapter<AgentRunEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `agent_runs` (`id`,`projectId`,`chatId`,`request`,`startedAt`,`durationMs`,`result`,`filesChanged`,`commands`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AgentRunEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getProjectId());
        statement.bindString(3, entity.getChatId());
        statement.bindString(4, entity.getRequest());
        statement.bindLong(5, entity.getStartedAt());
        statement.bindLong(6, entity.getDurationMs());
        statement.bindString(7, entity.getResult());
        statement.bindLong(8, entity.getFilesChanged());
        statement.bindString(9, entity.getCommands());
      }
    };
    this.__preparedStmtOfDeleteRunsForProject = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM agent_runs WHERE projectId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRun(final AgentRunEntity run, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAgentRunEntity.insert(run);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRunsForProject(final String projectId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRunsForProject.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, projectId);
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
          __preparedStmtOfDeleteRunsForProject.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AgentRunEntity>> getRunsForProjectFlow(final String projectId) {
    final String _sql = "SELECT * FROM agent_runs WHERE projectId = ? ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"agent_runs"}, new Callable<List<AgentRunEntity>>() {
      @Override
      @NonNull
      public List<AgentRunEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfChatId = CursorUtil.getColumnIndexOrThrow(_cursor, "chatId");
          final int _cursorIndexOfRequest = CursorUtil.getColumnIndexOrThrow(_cursor, "request");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfFilesChanged = CursorUtil.getColumnIndexOrThrow(_cursor, "filesChanged");
          final int _cursorIndexOfCommands = CursorUtil.getColumnIndexOrThrow(_cursor, "commands");
          final List<AgentRunEntity> _result = new ArrayList<AgentRunEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AgentRunEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpChatId;
            _tmpChatId = _cursor.getString(_cursorIndexOfChatId);
            final String _tmpRequest;
            _tmpRequest = _cursor.getString(_cursorIndexOfRequest);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpResult;
            _tmpResult = _cursor.getString(_cursorIndexOfResult);
            final int _tmpFilesChanged;
            _tmpFilesChanged = _cursor.getInt(_cursorIndexOfFilesChanged);
            final String _tmpCommands;
            _tmpCommands = _cursor.getString(_cursorIndexOfCommands);
            _item = new AgentRunEntity(_tmpId,_tmpProjectId,_tmpChatId,_tmpRequest,_tmpStartedAt,_tmpDurationMs,_tmpResult,_tmpFilesChanged,_tmpCommands);
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
  public Object getRecentRuns(final String projectId, final int limit,
      final Continuation<? super List<AgentRunEntity>> $completion) {
    final String _sql = "SELECT * FROM agent_runs WHERE projectId = ? ORDER BY startedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AgentRunEntity>>() {
      @Override
      @NonNull
      public List<AgentRunEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfChatId = CursorUtil.getColumnIndexOrThrow(_cursor, "chatId");
          final int _cursorIndexOfRequest = CursorUtil.getColumnIndexOrThrow(_cursor, "request");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfFilesChanged = CursorUtil.getColumnIndexOrThrow(_cursor, "filesChanged");
          final int _cursorIndexOfCommands = CursorUtil.getColumnIndexOrThrow(_cursor, "commands");
          final List<AgentRunEntity> _result = new ArrayList<AgentRunEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AgentRunEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpChatId;
            _tmpChatId = _cursor.getString(_cursorIndexOfChatId);
            final String _tmpRequest;
            _tmpRequest = _cursor.getString(_cursorIndexOfRequest);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpResult;
            _tmpResult = _cursor.getString(_cursorIndexOfResult);
            final int _tmpFilesChanged;
            _tmpFilesChanged = _cursor.getInt(_cursorIndexOfFilesChanged);
            final String _tmpCommands;
            _tmpCommands = _cursor.getString(_cursorIndexOfCommands);
            _item = new AgentRunEntity(_tmpId,_tmpProjectId,_tmpChatId,_tmpRequest,_tmpStartedAt,_tmpDurationMs,_tmpResult,_tmpFilesChanged,_tmpCommands);
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
}
