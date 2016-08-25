package com.devop.tasker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.Task;
import com.devop.tasker.services.NotificationService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "com.devop.tasker.ViewTaskActivity.EXTRA.TASK";

    private Task task;
    private TextView statusTextView;
    private MenuItem menuItemDone;

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);

        if (task == null) {
            Toast.makeText(this, R.string.message_invalid_task, Toast.LENGTH_SHORT).show();
            finish();

        } else {
            String dueDateStr = getResources().getString(R.string.message_no_due_date);
            if (task.getDueTime() != Task.NO_DUE) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(task.getDueTime());
                dueDateStr = dateFormat.format(calendar.getTime());
            }
            statusTextView = (TextView) findViewById(R.id.task_status);

            ((TextView) findViewById(R.id.task_title)).setText(task.getTitle());
            ((TextView) findViewById(R.id.task_description)).setText(task.getDescription());
            ((TextView) findViewById(R.id.task_importance)).setText(Task.Importance.getStringResource(task.getImportance()));
            ((TextView) findViewById(R.id.task_due_date)).setText(dueDateStr);

            statusTextView.setText(Task.Status.getStringResource(task.getStatus()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_view_task_action, menu);

        menuItemDone = menu.findItem(R.id.action_done);
        menuItemDone.setVisible(task.getStatus() != Task.Status.COMPLETED);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                item.setVisible(false);
                completeTask();
                break;
            case R.id.action_delete:
                deleteTask();
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    private void deleteTask() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        task.delete(databaseHelper);
        databaseHelper.close();

        // Remove remaining notification
        startService(NotificationService.removeNotification(this, task.getId()));
    }

    private void completeTask() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        task.setStatus(Task.Status.COMPLETED);
        task.save(databaseHelper);
        statusTextView.setText(Task.Status.getStringResource(task.getStatus()));
        databaseHelper.close();

        // Remove remaining notification
        startService(NotificationService.removeNotification(this, task.getId()));
    }

}
