package com.example.reminder;

import android.provider.BaseColumns;

public final class ReminderDataBaseContract {
    private ReminderDataBaseContract(){}

    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task_info";
        public static final String COLUMN_TASK_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TASK_TITLE + " TEXT NOT NULL, " +
                        COLUMN_DATE + " TEXT NOT NULL, " +
                        COLUMN_TIME + " TEXT NOT NULL, " +
                        COLUMN_DESCRIPTION + " TEXT NOT NULL)";
    }
}
