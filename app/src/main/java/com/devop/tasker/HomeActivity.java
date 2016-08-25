package com.devop.tasker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.models.Group;
import com.devop.tasker.models.Task;
import com.devop.tasker.services.NotificationService;
import com.devop.tasker.views.AbstractViewHolder;
import com.devop.tasker.views.GroupNavigationAdapter;
import com.devop.tasker.views.TaskAdapter;

public class HomeActivity extends AppCompatActivity
        implements AbstractViewHolder.OnTaskActionPerformedListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    // Adapters
    private TaskAdapter taskAdapter;
    private GroupNavigationAdapter groupAdapter;

    // View elements
    private ListView groupListView;
    private DrawerLayout drawer;

    // Private field to store current viewed group
    private int groupID;

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initalize drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize groups in drawer
        groupAdapter = new GroupNavigationAdapter(this);
        groupAdapter.refresh();
        groupListView = (ListView) findViewById(R.id.navigation_list_view);
        groupListView.setAdapter(groupAdapter);

        // Set listeners
        groupListView.setOnItemClickListener(this);
        groupListView.setOnItemLongClickListener(this);

        // Initialize swipe layout to refreshAll when swiped
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTasks();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Initialize layout to add group
        LinearLayout addGroupLayout = (LinearLayout) findViewById(R.id.nav_add_group);
        addGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGroupDialog();
            }
        });

        // Initialize task list
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, this);
        recyclerView.setAdapter(taskAdapter);

        // Initialize current group to all tasks
        showGroupTasks(0, Group.ALL_TASK_GROUP_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTasks();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home_action, menu);

        MenuItem menuItem = menu.findItem(R.id.action_add_task);

        if (menuItem != null) {
            tintMenuIcon(HomeActivity.this, menuItem, android.R.color.white);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_task:
                startActivity(AddTaskActivity.newIntent(this, groupID));
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    public void onTaskCompleted(Task task) {
        // Update task status
        task.setStatus(Task.Status.COMPLETED);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        task.save(databaseHelper);
        databaseHelper.close();

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

    @Override
    public void onTaskClicked(Task task) {
        if (task == null)
            startActivity(AddTaskActivity.newIntent(this, groupID));
        else
            startActivity(ViewTaskActivity.newIntent(this, task));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showGroupTasks(position, id);
        closeDrawer();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteGroup(position);
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        };

        int message;
        if (position == 0)
            message = R.string.message_delete_all_task;
        else
            message = R.string.message_delete_group;

        builder.setMessage(message);
        builder.setPositiveButton(R.string.button_yes, listener);
        builder.setNegativeButton(R.string.button_cancel, listener);

        builder.show();
        return true;
    }

    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);

        editText.setMaxLines(1);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        String groupName = editText.getText().toString();
                        if (groupName.isEmpty())
                            Toast.makeText(HomeActivity.this, R.string.message_group_name_required, Toast.LENGTH_SHORT).show();
                        else if (groupAdapter.hasGroupName(groupName))
                            Toast.makeText(HomeActivity.this, R.string.message_group_name_exist, Toast.LENGTH_SHORT).show();
                        else
                            addGroup(groupName);
                        break;
                }
            }
        };

        builder.setMessage(R.string.message_add_group);
        builder.setView(editText);
        builder.setPositiveButton(R.string.button_add_group, listener);
        builder.setNegativeButton(R.string.button_cancel, listener);

        builder.show();
    }

    private void addGroup(String groupName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        Group group = new Group(groupName);
        group.save(databaseHelper);
        databaseHelper.close();

        groupAdapter.addGroup(group);
    }

    public void refreshTasks() {
        taskAdapter.refresh(groupID);
    }

    private void deleteGroup(int position) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Group group = (Group) groupAdapter.getItem(position);

        // Delete all task from this group
        for (Task task : group.getTasks(databaseHelper)) {
            startService(NotificationService.removeNotification(this, task.getId()));
            task.delete(databaseHelper);
        }

        // Delete the all group except Default
        if (group.getGroupName().equals(Group.DEFAULT_GROUP_NAME))
            Toast.makeText(this, R.string.message_removing_default_group, Toast.LENGTH_SHORT).show();
        else {
            group.delete(databaseHelper);
            groupAdapter.removeGroupAt(position);
        }

        databaseHelper.close();
        showGroupTasks(0, Group.ALL_TASK_GROUP_ID);
    }

    private void showGroupTasks(int position, long id) {
        groupID = (int) id;
        getSupportActionBar().setTitle(((Group) groupAdapter.getItem(position)).getGroupName());
        refreshTasks();
    }

}
