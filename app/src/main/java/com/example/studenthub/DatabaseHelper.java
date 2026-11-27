package com.example.studenthub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gestion_comptes.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String TABLE_SUBJECTS = "subjects";
    public static final String SUB_ID = "id";
    public static final String SUB_NAME = "name";
    public static final String SUB_CODE = "code";
    public static final String SUB_TEACHER = "teacher";
    public static final String SUB_DESC = "description";
    public static final String SUB_USER_ID = "user_id";

    public static final String TABLE_TASKS = "tasks";
    public static final String TASK_ID = "id";
    public static final String TASK_TITLE = "title";
    public static final String TASK_DESC = "description";
    public static final String TASK_DUE_DATE = "due_date";
    public static final String TASK_PRIORITY = "priority";
    public static final String TASK_STATUS = "status";
    public static final String TASK_SUBJECT_ID = "subject_id";
    public static final String TASK_USER_ID = "user_id";

    private static final String TABLE_USERS_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOM + " TEXT NOT NULL, " +
                    COLUMN_PRENOM + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    private static final String TABLE_SUBJECTS_CREATE =
            "CREATE TABLE " + TABLE_SUBJECTS + " (" +
                    SUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_NAME + " TEXT NOT NULL, " +
                    SUB_CODE + " TEXT NOT NULL, " +
                    SUB_TEACHER + " TEXT, " +
                    SUB_DESC + " TEXT, " +
                    SUB_USER_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + SUB_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");";

    private static final String TABLE_TASKS_CREATE =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_TITLE + " TEXT NOT NULL, " +
                    TASK_DESC + " TEXT, " +
                    TASK_DUE_DATE + " TEXT, " +
                    TASK_PRIORITY + " TEXT, " +
                    TASK_STATUS + " TEXT, " +
                    TASK_SUBJECT_ID + " INTEGER NOT NULL, " +
                    TASK_USER_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + TASK_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + SUB_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USERS_CREATE);
        db.execSQL(TABLE_SUBJECTS_CREATE);
        db.execSQL(TABLE_TASKS_CREATE);
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_subjects_user_id ON " + TABLE_SUBJECTS + "(" + SUB_USER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON " + TABLE_TASKS + "(" + TASK_USER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tasks_subject_id ON " + TABLE_TASKS + "(" + TASK_SUBJECT_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            if (oldVersion < 3) {
                // v1/v2 to v3 migration logic
                db.execSQL("CREATE TABLE IF NOT EXISTS users_new (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOM + " TEXT NOT NULL, " +
                        COLUMN_PRENOM + " TEXT NOT NULL, " +
                        COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
                        COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");");

                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_USERS + ")", null);
                boolean hasPasswordColumn = false;
                boolean hasPasswordHashColumn = false;
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String colName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        if ("password".equals(colName)) hasPasswordColumn = true;
                        if ("password_hash".equals(colName)) hasPasswordHashColumn = true;
                    }
                    cursor.close();
                }

                if (hasPasswordHashColumn) {
                    db.execSQL("INSERT OR IGNORE INTO users_new (" + COLUMN_ID + ", " + COLUMN_NOM + ", " + COLUMN_PRENOM + ", " + COLUMN_EMAIL + ", " + COLUMN_PASSWORD_HASH + ", " + COLUMN_CREATED_AT + ") " +
                            "SELECT " + COLUMN_ID + ", " + COLUMN_NOM + ", " + COLUMN_PRENOM + ", " + COLUMN_EMAIL + ", " + COLUMN_PASSWORD_HASH + ", " + COLUMN_CREATED_AT + " FROM " + TABLE_USERS);
                } else if (hasPasswordColumn) {
                    Cursor userCursor = db.rawQuery("SELECT id, nom, prenom, email, password, created_at FROM " + TABLE_USERS, null);
                    if (userCursor != null) {
                        while (userCursor.moveToNext()) {
                            int id = userCursor.getInt(0);
                            String nom = userCursor.getString(1);
                            String prenom = userCursor.getString(2);
                            String email = userCursor.getString(3);
                            String plainPassword = userCursor.getString(4);
                            String createdAt = userCursor.getString(5);

                            String hash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());

                            ContentValues cv = new ContentValues();
                            cv.put(COLUMN_ID, id);
                            cv.put(COLUMN_NOM, nom);
                            cv.put(COLUMN_PRENOM, prenom);
                            cv.put(COLUMN_EMAIL, email);
                            cv.put(COLUMN_PASSWORD_HASH, hash);
                            cv.put(COLUMN_CREATED_AT, createdAt != null ? createdAt : "CURRENT_TIMESTAMP");
                            db.insertWithOnConflict("users_new", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                        }
                        userCursor.close();
                    }
                }
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                db.execSQL("ALTER TABLE users_new RENAME TO " + TABLE_USERS);
            }

            if (oldVersion < 4) {
                // Add subjects table for version 4 upgrade without dropping users
                db.execSQL(TABLE_SUBJECTS_CREATE);
            }

            if (oldVersion < 5) {
                // Add tasks table for version 5 upgrade without dropping users or subjects
                db.execSQL(TABLE_TASKS_CREATE);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_subjects_user_id ON " + TABLE_SUBJECTS + "(" + SUB_USER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON " + TABLE_TASKS + "(" + TASK_USER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tasks_subject_id ON " + TABLE_TASKS + "(" + TASK_SUBJECT_ID + ")");
    }

    // --- User CRUD methods ---

    public boolean addUser(String nom, String prenom, String email, String plainPassword) {
        if (checkEmailExists(email)) {
            return false;
        }
        String passwordHash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD_HASH, passwordHash);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    public User checkUser(String email, String plainPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NOM, COLUMN_PRENOM, COLUMN_EMAIL, COLUMN_PASSWORD_HASH},
                COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH));
            BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash);

            if (result.verified) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));

                user = new User(id, nom, prenom, userEmail);
            }
            cursor.close();
        }
        db.close();
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NOM, COLUMN_PRENOM, COLUMN_EMAIL},
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));

            user = new User(id, nom, prenom, userEmail);
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean verifyCurrentPassword(int userId, String plainPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_PASSWORD_HASH},
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        boolean verified = false;
        if (cursor != null && cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH));
            verified = BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash).verified;
            cursor.close();
        }
        db.close();
        return verified;
    }

    public boolean updateUserProfile(int userId, String nom, String prenom) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);

        int rows = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean updateUserPassword(int userId, String newPlainPassword) {
        String newHash = BCrypt.withDefaults().hashToString(12, newPlainPassword.toCharArray());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD_HASH, newHash);

        int rows = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NOM, COLUMN_PRENOM, COLUMN_EMAIL},
                null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));

                userList.add(new User(id, nom, prenom, email));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return userList;
    }

    public List<User> searchUsersByEmail(String emailQuery) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NOM, COLUMN_PRENOM, COLUMN_EMAIL},
                COLUMN_EMAIL + " LIKE ?",
                new String[]{"%" + emailQuery + "%"},
                null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));

                userList.add(new User(id, nom, prenom, email));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return userList;
    }

    // --- Subject CRUD methods (with mandatory ownership protection) ---

    public long insertSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUB_NAME, subject.getName());
        values.put(SUB_CODE, subject.getCode());
        values.put(SUB_TEACHER, subject.getTeacher());
        values.put(SUB_DESC, subject.getDescription());
        values.put(SUB_USER_ID, subject.getUserId());

        long id = db.insert(TABLE_SUBJECTS, null, values);
        db.close();
        return id;
    }

    public List<Subject> getSubjectsByUser(int userId) {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUBJECTS,
                new String[]{SUB_ID, SUB_NAME, SUB_CODE, SUB_TEACHER, SUB_DESC, SUB_USER_ID},
                SUB_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, SUB_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(SUB_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(SUB_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(SUB_CODE));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(SUB_TEACHER));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(SUB_DESC));
                int uId = cursor.getInt(cursor.getColumnIndexOrThrow(SUB_USER_ID));

                subjects.add(new Subject(id, name, code, teacher, description, uId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return subjects;
    }

    public Subject getSubjectByIdAndUser(int subjectId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUBJECTS,
                new String[]{SUB_ID, SUB_NAME, SUB_CODE, SUB_TEACHER, SUB_DESC, SUB_USER_ID},
                SUB_ID + " = ? AND " + SUB_USER_ID + " = ?",
                new String[]{String.valueOf(subjectId), String.valueOf(userId)},
                null, null, null);

        Subject subject = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(SUB_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SUB_NAME));
            String code = cursor.getString(cursor.getColumnIndexOrThrow(SUB_CODE));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow(SUB_TEACHER));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(SUB_DESC));
            int uId = cursor.getInt(cursor.getColumnIndexOrThrow(SUB_USER_ID));

            subject = new Subject(id, name, code, teacher, description, uId);
            cursor.close();
        }
        db.close();
        return subject;
    }

    public boolean updateSubject(Subject subject, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUB_NAME, subject.getName());
        values.put(SUB_CODE, subject.getCode());
        values.put(SUB_TEACHER, subject.getTeacher());
        values.put(SUB_DESC, subject.getDescription());

        int rows = db.update(TABLE_SUBJECTS, values,
                SUB_ID + " = ? AND " + SUB_USER_ID + " = ?",
                new String[]{String.valueOf(subject.getId()), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean deleteSubject(int subjectId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_SUBJECTS,
                SUB_ID + " = ? AND " + SUB_USER_ID + " = ?",
                new String[]{String.valueOf(subjectId), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    // --- Task CRUD methods (with mandatory ownership protection) ---

    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, task.getTitle());
        values.put(TASK_DESC, task.getDescription());
        values.put(TASK_DUE_DATE, task.getDueDate());
        values.put(TASK_PRIORITY, task.getPriority());
        values.put(TASK_STATUS, task.getStatus());
        values.put(TASK_SUBJECT_ID, task.getSubjectId());
        values.put(TASK_USER_ID, task.getUserId());

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{TASK_ID, TASK_TITLE, TASK_DESC, TASK_DUE_DATE, TASK_PRIORITY, TASK_STATUS, TASK_SUBJECT_ID, TASK_USER_ID},
                TASK_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, TASK_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(TASK_PRIORITY));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(TASK_STATUS));
                int subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_SUBJECT_ID));
                int uId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_USER_ID));

                tasks.add(new Task(id, title, description, dueDate, priority, status, subjectId, uId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    public List<Task> getTasksBySubject(int subjectId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{TASK_ID, TASK_TITLE, TASK_DESC, TASK_DUE_DATE, TASK_PRIORITY, TASK_STATUS, TASK_SUBJECT_ID, TASK_USER_ID},
                TASK_SUBJECT_ID + " = ?",
                new String[]{String.valueOf(subjectId)},
                null, null, TASK_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(TASK_PRIORITY));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(TASK_STATUS));
                int sId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_SUBJECT_ID));
                int uId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_USER_ID));

                tasks.add(new Task(id, title, description, dueDate, priority, status, sId, uId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    public List<Task> getTasksBySubject(int subjectId, int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{TASK_ID, TASK_TITLE, TASK_DESC, TASK_DUE_DATE, TASK_PRIORITY, TASK_STATUS, TASK_SUBJECT_ID, TASK_USER_ID},
                TASK_SUBJECT_ID + " = ? AND " + TASK_USER_ID + " = ?",
                new String[]{String.valueOf(subjectId), String.valueOf(userId)},
                null, null, TASK_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(TASK_PRIORITY));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(TASK_STATUS));
                int sId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_SUBJECT_ID));
                int uId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_USER_ID));

                tasks.add(new Task(id, title, description, dueDate, priority, status, sId, uId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    public Task getTaskByIdAndUser(int taskId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{TASK_ID, TASK_TITLE, TASK_DESC, TASK_DUE_DATE, TASK_PRIORITY, TASK_STATUS, TASK_SUBJECT_ID, TASK_USER_ID},
                TASK_ID + " = ? AND " + TASK_USER_ID + " = ?",
                new String[]{String.valueOf(taskId), String.valueOf(userId)},
                null, null, null);

        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE));
            String priority = cursor.getString(cursor.getColumnIndexOrThrow(TASK_PRIORITY));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(TASK_STATUS));
            int subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_SUBJECT_ID));
            int uId = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_USER_ID));

            task = new Task(id, title, description, dueDate, priority, status, subjectId, uId);
            cursor.close();
        }
        db.close();
        return task;
    }

    public boolean updateTask(Task task, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, task.getTitle());
        values.put(TASK_DESC, task.getDescription());
        values.put(TASK_DUE_DATE, task.getDueDate());
        values.put(TASK_PRIORITY, task.getPriority());
        values.put(TASK_STATUS, task.getStatus());
        values.put(TASK_SUBJECT_ID, task.getSubjectId());

        int rows = db.update(TABLE_TASKS, values,
                TASK_ID + " = ? AND " + TASK_USER_ID + " = ?",
                new String[]{String.valueOf(task.getId()), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, task.getTitle());
        values.put(TASK_DESC, task.getDescription());
        values.put(TASK_DUE_DATE, task.getDueDate());
        values.put(TASK_PRIORITY, task.getPriority());
        values.put(TASK_STATUS, task.getStatus());
        values.put(TASK_SUBJECT_ID, task.getSubjectId());
        values.put(TASK_USER_ID, task.getUserId());

        int rows = db.update(TABLE_TASKS, values,
                TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteTask(int taskId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS,
                TASK_ID + " = ? AND " + TASK_USER_ID + " = ?",
                new String[]{String.valueOf(taskId), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS,
                TASK_ID + " = ?",
                new String[]{String.valueOf(taskId)});
        db.close();
        return rows > 0;
    }
}
