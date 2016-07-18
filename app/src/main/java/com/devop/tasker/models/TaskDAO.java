package com.devop.tasker.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devop.tasker.R;
import com.devop.tasker.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.devop.tasker.db.TaskerContract.TaskColumns;

/**
 * Created by Kong My on 11/7/2016.
 */
public class TaskDAO {

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

    public TaskDAO() {
        this(NO_GROUP, "", "", Importance.NORMAL);
    }

    public TaskDAO(int groupID, String title, String description, int importance) {
        this(NO_GROUP, "", "", Importance.NORMAL, NO_DUE);
    }

    public TaskDAO(int groupID, String title, String description, int importance, long dueTime) {
        this.id = NEW_ID;
        this.groupID = groupID;
        this.title = title;
        this.description = description;
        this.importance = importance;
        this.dueTime = dueTime;
        this.status = Status.PENDING;
    }

    public static TaskDAO findByID(DatabaseHelper databaseHelper, int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(
                TaskColumns.TABLE_NAME,
                null,
                WHERE_CLAUSE_BY_ID,
                new String[]{String.valueOf(id)},
                null, null, null);

        TaskDAO taskDAO = null;
        if (cursor.moveToFirst()) {
            taskDAO = new TaskDAO(
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_GROUP_ID)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_IMPORTANCE)),
                    cursor.getLong(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DUE_TIME)));

            taskDAO.id = cursor.getInt(cursor.getColumnIndex(TaskColumns._ID));
            taskDAO.status = cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_STATUS));
        }
        return taskDAO;
    }

    public static List<TaskDAO> findByGroup(DatabaseHelper databaseHelper, int searchGroupID) {
        List<TaskDAO> taskDAOs = new ArrayList<>();
        TaskDAO taskDAO = null;

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(
                TaskColumns.TABLE_NAME,
                null,
                WHERE_CLAUSE_BY_GROUP_ID,
                new String[]{String.valueOf(searchGroupID)},
                null, null, null);

        while (cursor.moveToNext()) {
            taskDAO = new TaskDAO(
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_GROUP_ID)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_IMPORTANCE)),
                    cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_DUE_TIME)));

            taskDAO.id = cursor.getInt(cursor.getColumnIndex(TaskColumns._ID));
            taskDAO.status = cursor.getInt(cursor.getColumnIndex(TaskColumns.COLUMN_NAME_STATUS));

            taskDAOs.add(taskDAO);
        }

        return taskDAOs;
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

    public static abstract class Status {
        public static final int PENDING = 0;
        public static final int COMPLETED = 1;
        public static final int OVERDUE = 2;
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
