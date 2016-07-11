package com.devop.tasker.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devop.tasker.db.DatabaseHelper;

import static com.devop.tasker.db.TaskerContract.TaskColumns;

/**
 * Created by Kong My on 11/7/2016.
 */
public class TaskDAO {

    private static final int NEW_ID = -1;

    private static final String WHERE_CLAUSE_BY_ID = TaskColumns._ID + " = ?";

    private int id;
    private String title;
    private String description;
    private String status;

    public TaskDAO(String title, String description, String status) {
        this.id = NEW_ID;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public static TaskDAO findByID(DatabaseHelper databaseHelper, int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(TaskColumns.TABLE_NAME, null, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)}, null, null, null);

        TaskDAO taskDAO = null;
        if (cursor.moveToFirst()) {
            taskDAO = new TaskDAO(
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_STATUS)));
            taskDAO.id = cursor.getInt(cursor.getColumnIndex(TaskColumns._ID));
        }
        return taskDAO;
    }

    public void save(DatabaseHelper databaseHelper) {
        ContentValues values = new ContentValues();
        values.put(TaskColumns.COLUMN_NAME_TITLE, title);
        values.put(TaskColumns.COLUMN_NAME_DESCRIPTION, description);
        values.put(TaskColumns.COLUMN_NAME_STATUS, status);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (id == NEW_ID) {
            id = (int) db.insert(TaskColumns.TABLE_NAME, TaskColumns.COLUMN_NAME_NULLABLE, values);
        } else {
            db.update(TaskColumns.TABLE_NAME, values, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)});
        }
    }

}
