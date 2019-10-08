package com.example.reminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class TaskNotification {
    private static final String NOTIFICATION_TAG = "Task";
    private static final String CHANNEL_ID = "task_reminder_notification";

    public static void notify(final Context context, String taskTitle, String taskDescription, int taskId, int cancelAlarm) {
        final Resources res = context.getResources();

        Intent setTaskActivityIntent = new Intent(context, SetTaskActivity.class);
        setTaskActivityIntent.putExtra(SetTaskActivity.TASK_ID, taskId);
        setTaskActivityIntent.putExtra(AlarmReceiver.EXTRA_CANCEL_TASK_ALARM, cancelAlarm);

        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)

                .setDefaults(Notification.DEFAULT_ALL)

                .setSmallIcon(R.drawable.ic_stat_task)
                .setContentTitle(taskTitle)
                .setContentText(taskDescription)

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .setLargeIcon(picture)

                .setTicker("Review task")

                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                setTaskActivityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))

                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            nm.createNotificationChannel(channel);
        }
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
