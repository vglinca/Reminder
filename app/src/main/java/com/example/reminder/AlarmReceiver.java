package com.example.reminder;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_TITLE = "com.example.reminder.extra.TASK_TITLE";
    public static final String EXTRA_TASK_DESCRIPTION = "com.example.reminder.extra.TASK_DESCRIPTION";
    public static final String EXTRA_CANCEL_TASK_ALARM = "com.example.reminder.extra.CANCEL_TASK_ALARM";

    public AlarmReceiver(){}
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(EXTRA_TASK_TITLE);
        String description = intent.getStringExtra(EXTRA_TASK_DESCRIPTION);
        int taskId = intent.getIntExtra(SetTaskActivity.TASK_ID, -1);

        TaskNotification.notify(context, title, description, taskId, 0);
    }
}
