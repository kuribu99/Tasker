package com.devop.tasker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.devop.tasker.models.Task;

public class ViewTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "com.devop.tasker.ViewTaskActivity.EXTRA.TASK";

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

        Task task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);

        if (task == null) {
            Toast.makeText(this, "Invalid task", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Toast.makeText(this, "JOSN: " + task.toJSON(), Toast.LENGTH_SHORT).show();
        }
    }
}
