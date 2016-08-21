package com.devop.tasker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.devop.tasker.db.TaskerContract.TaskGroupColumns;
import static com.devop.tasker.db.TaskerContract.TaskColumns;

/**
 * Created by Kong My on 11/7/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TaskerDatabase.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_TASK = "CREATE TABLE " + TaskColumns.TABLE_NAME + " (" +
            TaskColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TaskColumns.COLUMN_NAME_GROUP_ID + " INTEGER," +
            TaskColumns.COLUMN_NAME_TITLE + " VARCHAR(50) NOT NULL," +
            TaskColumns.COLUMN_NAME_DESCRIPTION + " VARCHAR(250) NOT NULL," +
            TaskColumns.COLUMN_NAME_DUE_TIME + " BIGINT NOT NULL," +
            TaskColumns.COLUMN_NAME_IMPORTANCE + " INTEGER NOT NULL," +
            TaskColumns.COLUMN_NAME_STATUS + " INTEGER NOT NULL" +
            ")";

    private static final String SQL_CREATE_TABLE_GROUP = "CREATE TABLE " + TaskGroupColumns.TABLE_NAME + " (" +
            TaskGroupColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TaskGroupColumns.COLUMN_NAME_GROUP_NAME + " VARCHAR(50) NOT NULL" +
            ")";

    private static final String SQL_INSERT_DEFAULT_GROUP = "INSERT INTO " + TaskGroupColumns.TABLE_NAME + "(" +
            TaskGroupColumns.COLUMN_NAME_GROUP_NAME + ") VALUES ('Default')";

    private static final String SQL_DROP_TABLE_TASK = "DROP TABLE " + TaskColumns.TABLE_NAME;
    private static final String SQL_DROP_TABLE_GROUP = "DROP TABLE " + TaskGroupColumns.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASK);
        db.execSQL(SQL_CREATE_TABLE_GROUP);
        db.execSQL(SQL_INSERT_DEFAULT_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply recreate database
        db.execSQL(SQL_DROP_TABLE_TASK);
        db.execSQL(SQL_DROP_TABLE_GROUP);
        onCreate(db);
    }
}
