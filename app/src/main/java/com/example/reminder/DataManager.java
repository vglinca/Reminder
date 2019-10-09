package com.example.reminder;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.reminder.ReminderDataBaseContract.TaskEntry;

public class DataManager {
    private static DataManager ourInstance = null;

    public static DataManager getInstance(){
        if(ourInstance == null)
            ourInstance = new DataManager();

        return ourInstance;
    }

    public static void loadFromDatabase(ReminderDbOpenHelper openHelper){
        SQLiteDatabase db = openHelper.getReadableDatabase();

        final String[] TaskColumns = new String[]{
                TaskEntry.COLUMN_TASK_TITLE,
                TaskEntry.COLUMN_DATE,
                TaskEntry.COLUMN_TIME,
                TaskEntry.COLUMN_DESCRIPTION,
                TaskEntry._ID};
        String taskOrderBy = TaskEntry.COLUMN_DATE + "," + TaskEntry.COLUMN_TIME;
        final Cursor taskCursor = db.query(TaskEntry.TABLE_NAME, TaskColumns,
                null, null, null, null, taskOrderBy);
        loadTasksFromDatabase(taskCursor);
    }

    public static void loadFromDbWithFilter(ReminderDbOpenHelper openHelper, String filter){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        final String[] TaskColumns = new String[]{
                TaskEntry.COLUMN_TASK_TITLE,
                TaskEntry.COLUMN_DATE,
                TaskEntry.COLUMN_TIME,
                TaskEntry.COLUMN_DESCRIPTION,
                TaskEntry._ID};
        String selection = TaskEntry.COLUMN_DATE + " LIKE ?";
        String[] selectionArgs = new String[]{filter + "%"};
        final Cursor taskCursor = db.query(TaskEntry.TABLE_NAME, TaskColumns, selection, selectionArgs, null, null, null);
        loadTasksFromDatabase(taskCursor);
    }

    private static void loadTasksFromDatabase(Cursor cursor) {
        int taskTitlePos = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_TITLE);
        int taskDatePos = cursor.getColumnIndex(TaskEntry.COLUMN_DATE);
        int taskTimePos = cursor.getColumnIndex(TaskEntry.COLUMN_TIME);
        int taskDescriptionPos = cursor.getColumnIndex(TaskEntry.COLUMN_DESCRIPTION);
        int idPos = cursor.getColumnIndex(TaskEntry._ID);

        DataManager dm = getInstance();
        while (cursor.moveToNext()){
            String taskTitle = cursor.getString(taskTitlePos);
            String taskDate = cursor.getString(taskDatePos);
            String taskTime = cursor.getString(taskTimePos);
            String taskDescription = cursor.getString(taskDescriptionPos);
            String courseId = cursor.getString(idPos);
            int id = cursor.getInt(idPos);

        }
        cursor.close();
    }
}
