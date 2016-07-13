package com.devop.tasker.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import com.devop.tasker.R;
import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.TaskDAO;

/**
 * Created by Kong My on 13/7/2016.
 */
public class NotificationService extends IntentService {

    public static final String NAME = "NotificationService";
    public static final String EXTRA_TASK_ID = "com.devop.tasker.services.NotificationService.extra.taskID";
    public static final String EXTRA_ACTION = "com.devop.tasker.services.NotificationService.extra.action";

    public static final int ACTION_SHOW_NOTIFICATION = 0;
    public static final int ACTION_COMPLETE = 1;
    public static final int ACTION_DELAY = 2;

    private static final int UNDEFINED = -1;

    public NotificationService() {
        super(NAME);
    }

    public static Intent newNotification(int taskID) {
        return newIntent(taskID, ACTION_DELAY);
    }

    protected static Intent newIntent(int taskID, int action) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TASK_ID, taskID);
        intent.putExtra(EXTRA_ACTION, action);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        int taskID = intent.getIntExtra(EXTRA_TASK_ID, UNDEFINED);

        if (taskID != UNDEFINED) {
            TaskDAO task = TaskDAO.findByID(databaseHelper, taskID);

            if (task != null) {

                // Handle intent accordingly
                switch (intent.getIntExtra(EXTRA_ACTION, UNDEFINED)) {

                    case ACTION_SHOW_NOTIFICATION:
                        ShowNotification(task);
                        break;

                    case ACTION_COMPLETE:
                        task.setStatus(TaskDAO.Status.COMPLETED);
                        task.save(databaseHelper);
                        RemoveNotification(task.getId());
                        break;

                    case ACTION_DELAY:
                        DelayTask(task, databaseHelper);
                        RemoveNotification(task.getId());
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

    protected void ShowNotification(TaskDAO task) {
        // Create intents
        Intent completeIntent = newIntent(task.getId(), ACTION_COMPLETE);
        Intent delayIntent = newIntent(task.getId(), ACTION_DELAY);

        // Convert intents to pendingIntents
        PendingIntent completePendingIntent = PendingIntent.getActivity(this, ACTION_COMPLETE, completeIntent, Intent.FILL_IN_DATA);
        PendingIntent delayPendingIntent = PendingIntent.getActivity(this, ACTION_DELAY, delayIntent, Intent.FILL_IN_DATA);

        // Build the notification
        Notification.Builder builder = new Notification.Builder(this)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription());

        // Add action based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.addAction(new Notification.Action.Builder(
                    Icon.createWithResource(getApplicationContext(), R.drawable.ic_menu_send),
                    "Completed",
                    completePendingIntent).build());
            builder.addAction(new Notification.Action.Builder(
                    Icon.createWithResource(getApplicationContext(), R.drawable.ic_menu_camera),
                    "Delay",
                    delayPendingIntent).build());
        } else {
            builder.addAction(new Notification.Action(R.drawable.ic_menu_send, "Completed", completePendingIntent));
            builder.addAction(new Notification.Action(R.drawable.ic_menu_camera, "Delay", delayPendingIntent));
        }

        // Show the notification
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    protected void DelayTask(TaskDAO task, DatabaseHelper databaseHelper) {
        Intent intent = newIntent(task.getId(), ACTION_SHOW_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, ACTION_COMPLETE, intent, Intent.FILL_IN_DATA);

        // Set the delay based on task status
        long notificationTime = 0;

        switch (task.getStatus()) {

            case TaskDAO.Status.OVERDUE:
                notificationTime = System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                break;

            case TaskDAO.Status.PENDING:
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
                    task.setStatus(TaskDAO.Status.OVERDUE);
                    task.save(databaseHelper);
                    notificationTime = System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                }
                break;

            case TaskDAO.Status.COMPLETED:
                Log.d("[Warning]", "Completed task shown notification");
                return;

        }

        // Set the alarm
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.ELAPSED_REALTIME, notificationTime, pendingIntent);
    }
}
