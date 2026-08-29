package com.velocity.ide.data.database;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ProjectDao _projectDao;

  private volatile ChatDao _chatDao;

  private volatile MessageDao _messageDao;

  private volatile ProjectMemoryDao _projectMemoryDao;

  private volatile CheckpointDao _checkpointDao;

  private volatile AgentRunDao _agentRunDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `projects` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `path` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chats` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `title` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `chatId` TEXT NOT NULL, `role` TEXT NOT NULL, `content` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `project_memories` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `key` TEXT NOT NULL, `value` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `checkpoints` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `title` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `backupPath` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `agent_runs` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `chatId` TEXT NOT NULL, `request` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `durationMs` INTEGER NOT NULL, `result` TEXT NOT NULL, `filesChanged` INTEGER NOT NULL, `commands` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8c3966d3996c66ce4d8ad6af53845ffb')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `projects`");
        db.execSQL("DROP TABLE IF EXISTS `chats`");
        db.execSQL("DROP TABLE IF EXISTS `messages`");
        db.execSQL("DROP TABLE IF EXISTS `project_memories`");
        db.execSQL("DROP TABLE IF EXISTS `checkpoints`");
        db.execSQL("DROP TABLE IF EXISTS `agent_runs`");
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
        final HashMap<String, TableInfo.Column> _columnsProjects = new HashMap<String, TableInfo.Column>(4);
        _columnsProjects.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("path", new TableInfo.Column("path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProjects = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProjects = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProjects = new TableInfo("projects", _columnsProjects, _foreignKeysProjects, _indicesProjects);
        final TableInfo _existingProjects = TableInfo.read(db, "projects");
        if (!_infoProjects.equals(_existingProjects)) {
          return new RoomOpenHelper.ValidationResult(false, "projects(com.velocity.ide.data.database.ProjectEntity).\n"
                  + " Expected:\n" + _infoProjects + "\n"
                  + " Found:\n" + _existingProjects);
        }
        final HashMap<String, TableInfo.Column> _columnsChats = new HashMap<String, TableInfo.Column>(4);
        _columnsChats.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("projectId", new TableInfo.Column("projectId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChats = new TableInfo("chats", _columnsChats, _foreignKeysChats, _indicesChats);
        final TableInfo _existingChats = TableInfo.read(db, "chats");
        if (!_infoChats.equals(_existingChats)) {
          return new RoomOpenHelper.ValidationResult(false, "chats(com.velocity.ide.data.database.ChatEntity).\n"
                  + " Expected:\n" + _infoChats + "\n"
                  + " Found:\n" + _existingChats);
        }
        final HashMap<String, TableInfo.Column> _columnsMessages = new HashMap<String, TableInfo.Column>(5);
        _columnsMessages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("chatId", new TableInfo.Column("chatId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMessages = new TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages);
        final TableInfo _existingMessages = TableInfo.read(db, "messages");
        if (!_infoMessages.equals(_existingMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "messages(com.velocity.ide.data.database.MessageEntity).\n"
                  + " Expected:\n" + _infoMessages + "\n"
                  + " Found:\n" + _existingMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsProjectMemories = new HashMap<String, TableInfo.Column>(5);
        _columnsProjectMemories.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjectMemories.put("projectId", new TableInfo.Column("projectId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjectMemories.put("key", new TableInfo.Column("key", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjectMemories.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjectMemories.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProjectMemories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProjectMemories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProjectMemories = new TableInfo("project_memories", _columnsProjectMemories, _foreignKeysProjectMemories, _indicesProjectMemories);
        final TableInfo _existingProjectMemories = TableInfo.read(db, "project_memories");
        if (!_infoProjectMemories.equals(_existingProjectMemories)) {
          return new RoomOpenHelper.ValidationResult(false, "project_memories(com.velocity.ide.data.database.ProjectMemoryEntity).\n"
                  + " Expected:\n" + _infoProjectMemories + "\n"
                  + " Found:\n" + _existingProjectMemories);
        }
        final HashMap<String, TableInfo.Column> _columnsCheckpoints = new HashMap<String, TableInfo.Column>(5);
        _columnsCheckpoints.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCheckpoints.put("projectId", new TableInfo.Column("projectId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCheckpoints.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCheckpoints.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCheckpoints.put("backupPath", new TableInfo.Column("backupPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCheckpoints = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCheckpoints = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCheckpoints = new TableInfo("checkpoints", _columnsCheckpoints, _foreignKeysCheckpoints, _indicesCheckpoints);
        final TableInfo _existingCheckpoints = TableInfo.read(db, "checkpoints");
        if (!_infoCheckpoints.equals(_existingCheckpoints)) {
          return new RoomOpenHelper.ValidationResult(false, "checkpoints(com.velocity.ide.data.database.CheckpointEntity).\n"
                  + " Expected:\n" + _infoCheckpoints + "\n"
                  + " Found:\n" + _existingCheckpoints);
        }
        final HashMap<String, TableInfo.Column> _columnsAgentRuns = new HashMap<String, TableInfo.Column>(9);
        _columnsAgentRuns.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("projectId", new TableInfo.Column("projectId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("chatId", new TableInfo.Column("chatId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("request", new TableInfo.Column("request", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("result", new TableInfo.Column("result", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("filesChanged", new TableInfo.Column("filesChanged", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAgentRuns.put("commands", new TableInfo.Column("commands", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAgentRuns = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAgentRuns = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAgentRuns = new TableInfo("agent_runs", _columnsAgentRuns, _foreignKeysAgentRuns, _indicesAgentRuns);
        final TableInfo _existingAgentRuns = TableInfo.read(db, "agent_runs");
        if (!_infoAgentRuns.equals(_existingAgentRuns)) {
          return new RoomOpenHelper.ValidationResult(false, "agent_runs(com.velocity.ide.data.database.AgentRunEntity).\n"
                  + " Expected:\n" + _infoAgentRuns + "\n"
                  + " Found:\n" + _existingAgentRuns);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8c3966d3996c66ce4d8ad6af53845ffb", "5e8ede4dcc09fdb841c3398b252ea11b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "projects","chats","messages","project_memories","checkpoints","agent_runs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `projects`");
      _db.execSQL("DELETE FROM `chats`");
      _db.execSQL("DELETE FROM `messages`");
      _db.execSQL("DELETE FROM `project_memories`");
      _db.execSQL("DELETE FROM `checkpoints`");
      _db.execSQL("DELETE FROM `agent_runs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(ProjectDao.class, ProjectDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatDao.class, ChatDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MessageDao.class, MessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProjectMemoryDao.class, ProjectMemoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CheckpointDao.class, CheckpointDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AgentRunDao.class, AgentRunDao_Impl.getRequiredConverters());
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
  public ProjectDao projectDao() {
    if (_projectDao != null) {
      return _projectDao;
    } else {
      synchronized(this) {
        if(_projectDao == null) {
          _projectDao = new ProjectDao_Impl(this);
        }
        return _projectDao;
      }
    }
  }

  @Override
  public ChatDao chatDao() {
    if (_chatDao != null) {
      return _chatDao;
    } else {
      synchronized(this) {
        if(_chatDao == null) {
          _chatDao = new ChatDao_Impl(this);
        }
        return _chatDao;
      }
    }
  }

  @Override
  public MessageDao messageDao() {
    if (_messageDao != null) {
      return _messageDao;
    } else {
      synchronized(this) {
        if(_messageDao == null) {
          _messageDao = new MessageDao_Impl(this);
        }
        return _messageDao;
      }
    }
  }

  @Override
  public ProjectMemoryDao projectMemoryDao() {
    if (_projectMemoryDao != null) {
      return _projectMemoryDao;
    } else {
      synchronized(this) {
        if(_projectMemoryDao == null) {
          _projectMemoryDao = new ProjectMemoryDao_Impl(this);
        }
        return _projectMemoryDao;
      }
    }
  }

  @Override
  public CheckpointDao checkpointDao() {
    if (_checkpointDao != null) {
      return _checkpointDao;
    } else {
      synchronized(this) {
        if(_checkpointDao == null) {
          _checkpointDao = new CheckpointDao_Impl(this);
        }
        return _checkpointDao;
      }
    }
  }

  @Override
  public AgentRunDao agentRunDao() {
    if (_agentRunDao != null) {
      return _agentRunDao;
    } else {
      synchronized(this) {
        if(_agentRunDao == null) {
          _agentRunDao = new AgentRunDao_Impl(this);
        }
        return _agentRunDao;
      }
    }
  }
}
