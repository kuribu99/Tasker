package com.devop.tasker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.Task;
import com.devop.tasker.services.NotificationService;
import com.devop.tasker.views.AbstractViewHolder;
import com.devop.tasker.views.TaskRecyclerViewAdapter;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AbstractViewHolder.OnTaskActionPerformedListener {

    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerViewAdapter = new TaskRecyclerViewAdapter(this, this);
        recyclerView.setAdapter(taskRecyclerViewAdapter);
        taskRecyclerViewAdapter.refresh();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_task:
                startActivityForResult(AddTaskActivity.newIntent(this), AddTaskActivity.REQUEST_CODE_ADD_TASK);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_task:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AddTaskActivity.REQUEST_CODE_ADD_TASK:
                handleAddTaskResult(resultCode, data);
                break;
        }
    }

    private void handleAddTaskResult(int resultCode, Intent data) {
        if (resultCode == AddTaskActivity.RESULT_CODE_SUCCESS) {
            Task task = (Task) data.getSerializableExtra(AddTaskActivity.EXTRA_NEW_TASK);
            taskRecyclerViewAdapter.addTask(task);

            if (task.getDueTime() != Task.NO_DUE)
                startService(NotificationService.newNotification(this, task.getId()));
        }
    }

    @Override
    public void onTaskCompleted(Task task) {
        // Remove remaining notification
        startService(NotificationService.removeNotification(this, task.getId()));
    }

    @Override
    public void onTaskDeleted(Task task) {
        // Remove remaining notification
        startService(NotificationService.removeNotification(this, task.getId()));

        // Remove from database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        task.delete(databaseHelper);
        databaseHelper.close();
    }
}
