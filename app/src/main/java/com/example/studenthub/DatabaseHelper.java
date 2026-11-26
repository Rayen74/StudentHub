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
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_CREATED_AT = "created_at";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOM + " TEXT NOT NULL, " +
                    COLUMN_PRENOM + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            if (oldVersion < 3) {
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
                } else {
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                    onCreate(db);
                    db.setTransactionSuccessful();
                    return;
                }

                db.execSQL("DROP TABLE " + TABLE_USERS);
                db.execSQL("ALTER TABLE users_new RENAME TO " + TABLE_USERS);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")");
    }

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
        // NEVER retrieve password or password_hash! Select only id, nom, prenom, email
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
        // NEVER retrieve password or password_hash! Select only id, nom, prenom, email
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
}
