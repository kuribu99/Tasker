package com.devop.tasker.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.db.TaskerContract;

import java.util.List;

/**
 * Created by Kong My on 13/7/2016.
 */
public class GroupDAO {

    // Default values
    public static final int NEW_ID = -1;

    // SQL clauses
    private static final String WHERE_CLAUSE_BY_ID = TaskerContract.GroupColumns._ID + " = ?";

    // Fields
    private int id;
    private String groupName;

    public GroupDAO() {
        this("");
    }

    public GroupDAO(String groupName) {
        this.id = NEW_ID;
        this.groupName = groupName;
    }

    public static GroupDAO findByID(DatabaseHelper databaseHelper, int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(
                TaskerContract.GroupColumns.TABLE_NAME,
                null,
                WHERE_CLAUSE_BY_ID,
                new String[]{String.valueOf(id)},
                null, null, null);

        GroupDAO groupDAO = null;
        if (cursor.moveToFirst()) {
            groupDAO = new GroupDAO(cursor.getString(cursor.getColumnIndex(TaskerContract.GroupColumns.COLUMN_NAME_GROUP_NAME)));
            groupDAO.id = cursor.getInt(cursor.getColumnIndex(TaskerContract.GroupColumns._ID));
        }
        return groupDAO;
    }

    public List<TaskDAO> getTasks(DatabaseHelper databaseHelper) {
        return TaskDAO.findByGroup(databaseHelper, id);
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void save(DatabaseHelper databaseHelper) {
        ContentValues values = new ContentValues();
        values.put(TaskerContract.GroupColumns.COLUMN_NAME_GROUP_NAME, groupName);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (id == NEW_ID) {
            id = (int) db.insert(TaskerContract.GroupColumns.TABLE_NAME, TaskerContract.GroupColumns.COLUMN_NAME_NULLABLE, values);
        } else {
            db.update(TaskerContract.GroupColumns.TABLE_NAME, values, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)});
        }
    }

}
