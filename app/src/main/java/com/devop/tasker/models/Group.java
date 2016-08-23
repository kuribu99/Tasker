package com.devop.tasker.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.db.TaskerContract;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kong My on 13/7/2016.
 */
public class Group implements Serializable {

    // Default values
    public static final int ALL_TASK_GROUP_ID = -2;
    public static final int NEW_ID = -1;

    // Static group that contains all task
    public static final Group ALL_TASK_GROUP;
    public static final String DEFAULT_GROUP_NAME = "Default";

    // SQL clauses
    private static final String WHERE_CLAUSE_BY_ID = TaskerContract.TaskGroupColumns._ID + " = ?";

    // Initialize ALL_TASK_GROUP
    static {
        ALL_TASK_GROUP = new Group("All Tasks");
        ALL_TASK_GROUP.id = ALL_TASK_GROUP_ID;
    }

    // Fields
    private int id;
    private String groupName;

    public Group() {
        this("");
    }

    public Group(String groupName) {
        this.id = NEW_ID;
        this.groupName = groupName;
    }

    public static Group findByID(DatabaseHelper databaseHelper, int id) {
        if (id == ALL_TASK_GROUP_ID)
            return ALL_TASK_GROUP;

        else {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            Cursor cursor = db.query(
                    TaskerContract.TaskGroupColumns.TABLE_NAME,
                    null,
                    WHERE_CLAUSE_BY_ID,
                    new String[]{String.valueOf(id)},
                    null, null, null);

            Group group = null;
            if (cursor.moveToFirst()) {
                group = new Group(cursor.getString(cursor.getColumnIndex(TaskerContract.TaskGroupColumns.COLUMN_NAME_GROUP_NAME)));
                group.id = cursor.getInt(cursor.getColumnIndex(TaskerContract.TaskGroupColumns._ID));
            }
            cursor.close();

            return group;
        }
    }

    public static List<Group> findAll(DatabaseHelper databaseHelper) {
        List<Group> groupList = new LinkedList<>();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(TaskerContract.TaskGroupColumns.TABLE_NAME, null, null, null, null, null, null);

        Group group;
        while (cursor.moveToNext()) {
            group = new Group(cursor.getString(cursor.getColumnIndex(TaskerContract.TaskGroupColumns.COLUMN_NAME_GROUP_NAME)));
            group.id = cursor.getInt(cursor.getColumnIndex(TaskerContract.TaskGroupColumns._ID));
            groupList.add(group);
        }
        cursor.close();

        return groupList;
    }

    public List<Task> getTasks(DatabaseHelper databaseHelper) {
        return Task.findByGroup(databaseHelper, id);
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void save(DatabaseHelper databaseHelper) {
        ContentValues values = new ContentValues();
        values.put(TaskerContract.TaskGroupColumns.COLUMN_NAME_GROUP_NAME, groupName);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (id == NEW_ID) {
            id = (int) db.insert(TaskerContract.TaskGroupColumns.TABLE_NAME, TaskerContract.TaskGroupColumns.COLUMN_NAME_NULLABLE, values);
        } else {
            db.update(TaskerContract.TaskGroupColumns.TABLE_NAME, values, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)});
        }
    }

    public void delete(DatabaseHelper databaseHelper) {
        if (id > 0) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.delete(TaskerContract.TaskGroupColumns.TABLE_NAME, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)});
        }
    }
}
