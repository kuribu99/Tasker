package com.devop.tasker.db;

import android.provider.BaseColumns;

/**
 * Created by Kong My on 11/7/2016.
 */
public final class TaskerContract {

    public TaskerContract() {
        // Empty constructor
    }

    public static abstract class GrouoColumns implements BaseColumns {
        public static final String TABLE_NAME = "group";
        public static final String COLUMN_NAME_GROUP_NAME = "group_name";
    }

    public static abstract class TaskColumns implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_GROUP_ID = "group_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_TIME = "due_time";
        public static final String COLUMN_NAME_IMPORTANCE = "importance";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_NULLABLE = "description";
    }

}