package com.devop.tasker.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devop.tasker.R;
import com.devop.tasker.db.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.devop.tasker.db.TaskerContract.TaskColumns;

/**
 * Created by Kong My on 11/7/2016.
 */
public class Task implements Serializable {

    // Default values
    public static final int NEW_ID = -1;
    public static final int NO_GROUP = -1;
    public static final long NO_DUE = -1;

    // SQL clauses
    private static final String WHERE_CLAUSE_BY_ID = TaskColumns._ID + " = ?";
    private static final String WHERE_CLAUSE_BY_GROUP_ID = TaskColumns.COLUMN_NAME_GROUP_ID + " = ?";

    // Fields
    private int id;
    private long groupID;
    private String title;
    private String description;
    private long dueTime;
    private int importance;
    private int status;

    public Task() {
        this(NO_GROUP, "", "", Importance.NORMAL);
    }

    public Task(int groupID, String title, String description, int importance) {
        this(groupID, title, description, importance, NO_DUE);
    }

    public Task(int groupID, String title, String description, int importance, long dueTime) {
        this.id = NEW_ID;
        this.groupID = groupID;
        this.title = title;
        this.description = description;
        this.importance = importance;
        this.dueTime = dueTime;
        this.status = Status.PENDING;
    }

    public static Task findByID(DatabaseHelper databaseHelper, int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(
                TaskColumns.TABLE_NAME,
                null,
                WHERE_CLAUSE_BY_ID,
                new String[]{String.valueOf(id)},
                null, null, null);

        Task task = null;
        if (cursor.moveToFirst()) {
            task = new Task(
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_GROUP_ID)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_IMPORTANCE)),
                    cursor.getLong(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DUE_TIME)));

            task.id = cursor.getInt(cursor.getColumnIndex(TaskColumns._ID));
            task.status = cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_STATUS));
        }
        cursor.close();

        return task;
    }

    public static List<Task> findByGroup(DatabaseHelper databaseHelper, int searchGroupID) {
        if (searchGroupID == Group.ALL_TASK_GROUP_ID)
            return findAll(databaseHelper);

        else {
            List<Task> tasks = new ArrayList<>();
            Task task;

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            Cursor cursor = db.query(
                    TaskColumns.TABLE_NAME,
                    null,
                    WHERE_CLAUSE_BY_GROUP_ID,
                    new String[]{String.valueOf(searchGroupID)},
                    null, null, null);

            while (cursor.moveToNext()) {
                task = new Task(
                        cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_GROUP_ID)),
                        cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_IMPORTANCE)),
                        cursor.getLong(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DUE_TIME)));

                task.id = cursor.getInt(cursor.getColumnIndex(TaskColumns._ID));
                task.status = cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_STATUS));

                tasks.add(task);
            }
            cursor.close();

            return tasks;
        }
    }

    public static List<Task> findAll(DatabaseHelper databaseHelper) {
        List<Task> tasks = new ArrayList<>();
        Task task;

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(TaskColumns.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            task = new Task(
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_GROUP_ID)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_IMPORTANCE)),
                    cursor.getLong(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DUE_TIME)));

            task.id = cursor.getInt(cursor.getColumnIndex(TaskColumns._ID));
            task.status = cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_STATUS));

            tasks.add(task);
        }
        cursor.close();

        return tasks;
    }

    public int getId() {
        return id;
    }

    public long getGroupID() {
        return groupID;
    }

    public String getTitle() {
        return title;
    }

    public long getDueTime() {
        return dueTime;
    }

    public String getDescription() {
        return description;
    }

    public int getImportance() {
        return importance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void save(DatabaseHelper databaseHelper) {
        ContentValues values = new ContentValues();
        values.put(TaskColumns.COLUMN_NAME_GROUP_ID, groupID);
        values.put(TaskColumns.COLUMN_NAME_TITLE, title);
        values.put(TaskColumns.COLUMN_NAME_DESCRIPTION, description);
        values.put(TaskColumns.COLUMN_NAME_DUE_TIME, dueTime);
        values.put(TaskColumns.COLUMN_NAME_IMPORTANCE, importance);
        values.put(TaskColumns.COLUMN_NAME_STATUS, status);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (id == NEW_ID) {
            id = (int) db.insert(TaskColumns.TABLE_NAME, TaskColumns.COLUMN_NAME_NULLABLE, values);
        } else {
            db.update(TaskColumns.TABLE_NAME, values, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)});
        }
    }

    public String toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("groupID", groupID);
            object.put("title", title);
            object.put("description", description);
            object.put("dueTime", dueTime);
            object.put("importance", importance);
            object.put("status", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    public void delete(DatabaseHelper databaseHelper) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TaskColumns.TABLE_NAME, WHERE_CLAUSE_BY_ID, new String[]{String.valueOf(id)});
    }

    public static abstract class Status {
        public static final int PENDING = 0;
        public static final int COMPLETED = 1;
        public static final int OVERDUE = 2;

        public static int getStringResource(int status) {
            switch (status) {
                case PENDING:
                    return R.string.task_status_pending;
                case COMPLETED:
                    return R.string.task_status_completed;
                case OVERDUE:
                    return R.string.task_importance_overdue;
            }
            return -1;
        }
    }

    public static abstract class Importance {
        public static final int IMPORTANT = 0;
        public static final int NORMAL = 1;
        public static final int LOW = 2;

        public static int getStringResource(int importance) {
            switch (importance) {
                case IMPORTANT:
                    return R.string.task_importance_important;
                case NORMAL:
                    return R.string.task_importance_normal;
                case LOW:
                    return R.string.task_importance_low;
            }
            return -1;
        }

    }
}
