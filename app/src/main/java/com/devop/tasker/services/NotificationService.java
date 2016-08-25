package com.devop.tasker.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import com.devop.tasker.R;
import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.Task;

/**
 * Created by Kong My on 13/7/2016.
 */
public class NotificationService extends IntentService {

    public static final String NAME = "NotificationService";
    public static final String EXTRA_TASK_ID = "com.devop.tasker.services.NotificationService.EXTRA.TASK_ID";
    public static final String EXTRA_ACTION = "com.devop.tasker.services.NotificationService.EXTRA.ACTION";

    public static final int ACTION_SHOW_NOTIFICATION = 0;
    public static final int ACTION_COMPLETE = 1;
    public static final int ACTION_DELAY = 2;
    public static final int ACTION_REMOVE_NOTIFICATION = 3;

    private static final int UNDEFINED = -1;

    public NotificationService() {
        super(NAME);
    }

    public static Intent newNotification(Context context, int taskID) {
        return newIntent(context, taskID, ACTION_DELAY);
    }

    public static Intent removeNotification(Context context, int taskID) {
        return newIntent(context, taskID, ACTION_REMOVE_NOTIFICATION);
    }

    public static Intent newIntent(Context context, int taskID, int action) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(EXTRA_TASK_ID, taskID);
        intent.putExtra(EXTRA_ACTION, action);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        int taskID = intent.getIntExtra(EXTRA_TASK_ID, UNDEFINED);

        if (taskID != UNDEFINED) {
            Task task = Task.findByID(databaseHelper, taskID);

            if (task == null)
                Log.d("[Warning]", "Task deleted from database");

            else if (task.getStatus() == Task.Status.COMPLETED)
                Log.d("[Warning]", "Completed task show notification");

            else {
                // Handle actions accordingly
                switch (intent.getIntExtra(EXTRA_ACTION, UNDEFINED)) {

                    case ACTION_SHOW_NOTIFICATION:
                        ShowNotification(task);
                        break;

                    case ACTION_COMPLETE:
                        task.setStatus(Task.Status.COMPLETED);
                        task.save(databaseHelper);
                        RemoveNotification(taskID);
                        break;

                    case ACTION_DELAY:
                        DelayTask(task, databaseHelper);

                        RemoveNotification(taskID);
                        break;

                    case ACTION_REMOVE_NOTIFICATION:
                        RemoveNotification(taskID);
                        break;

                }
            }
        }
        databaseHelper.close();
    }

    protected void RemoveNotification(int taskID) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(taskID);
    }

    protected void ShowNotification(Task task) {
        // Create intents
        Intent completeIntent = newIntent(getApplicationContext(), task.getId(), ACTION_COMPLETE);
        Intent delayIntent = newIntent(getApplicationContext(), task.getId(), ACTION_DELAY);

        // Convert intents to pendingIntents
        PendingIntent completePendingIntent = PendingIntent.getService(this, task.getId(), completeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent delayPendingIntent = PendingIntent.getService(this, -1 * task.getId(), delayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        Notification.Builder builder = new Notification.Builder(this)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_assignment_turned_in_black_24dp)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription());

        // Add action based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            builder.addAction(new Notification.Action.Builder(
                    Icon.createWithResource(getApplicationContext(), R.drawable.ic_done_black_24dp),
                    getResources().getString(R.string.action_complete_task),
                    completePendingIntent).build());

            builder.addAction(new Notification.Action.Builder(
                    Icon.createWithResource(getApplicationContext(), R.drawable.ic_schedule_black_24dp),
                    getResources().getString(R.string.action_delay_task),
                    delayPendingIntent).build());
        } else {

            builder.addAction(new Notification.Action(
                    R.drawable.ic_done_black_24dp,
                    getResources().getString(R.string.action_complete_task),
                    completePendingIntent));

            builder.addAction(new Notification.Action(
                    R.drawable.ic_schedule_black_24dp,
                    getResources().getString(R.string.action_delay_task),
                    delayPendingIntent));
        }

        // Show the notification
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    protected void DelayTask(Task task, DatabaseHelper databaseHelper) {
        Intent intent = newIntent(getApplicationContext(), task.getId(), ACTION_SHOW_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getService(this, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the delay based on task status
        long notificationTime = 0;

        switch (task.getStatus()) {

            case Task.Status.OVERDUE:
                notificationTime = System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                break;

            case Task.Status.PENDING:
                // The first notification not yet happened
                if (task.getDueTime() - System.currentTimeMillis() > AlarmManager.INTERVAL_HOUR) {
                    notificationTime = task.getDueTime() - AlarmManager.INTERVAL_HOUR;
                }

                // The first already happened, but second not yet happened
                else if (task.getDueTime() - System.currentTimeMillis() > AlarmManager.INTERVAL_HALF_HOUR) {
                    notificationTime = task.getDueTime() - AlarmManager.INTERVAL_HALF_HOUR;
                }

                // The second already happened but not overdue
                else if (task.getDueTime() - System.currentTimeMillis() > 0) {
                    notificationTime = task.getDueTime();
                }

                // The task is now overdue
                else {
                    task.setStatus(Task.Status.OVERDUE);
                    task.save(databaseHelper);
                    notificationTime = System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                }
                break;

            case Task.Status.COMPLETED:
                Log.d("[Warning]", "Completed task shown notification");
                RemoveNotification(task.getId());
                return;
        }

        // Set the alarm
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Comment the following line in real usage
        notificationTime = System.currentTimeMillis() + 3000;

        manager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

}
