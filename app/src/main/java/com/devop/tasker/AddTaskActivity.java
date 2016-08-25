package com.devop.tasker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.fragments.DatePickerFragment;
import com.devop.tasker.fragments.TimePickerFragment;
import com.devop.tasker.models.Group;
import com.devop.tasker.models.Task;
import com.devop.tasker.services.NotificationService;
import com.devop.tasker.views.GroupDropdownAdapter;
import com.devop.tasker.views.ImportanceLevelAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String EXTRA_SELECTED_GROUP = "com.devop.tasker.AddTaskActivity.EXTRA.SELECTED_GROUP";

    private Spinner groupSpinner;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner importanceSpinner;
    private Switch dueDateSwitch;
    private LinearLayout dueDateLayout;
    private Button dateButton;
    private Button timeButton;
    private Calendar reminderCalendar;

    public static Intent newIntent(Context context, int groupID) {
        Intent intent = new Intent(context, AddTaskActivity.class);
        intent.putExtra(EXTRA_SELECTED_GROUP, groupID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get selected group id from intent
        int selectedGroupID = getIntent().getIntExtra(EXTRA_SELECTED_GROUP, Group.ALL_TASK_GROUP_ID);

        // find view items
        dateButton = (Button) findViewById(R.id.button_date);
        timeButton = (Button) findViewById(R.id.button_time);
        groupSpinner = (Spinner) findViewById(R.id.group_spinner);
        importanceSpinner = (Spinner) findViewById(R.id.importance_spinner);
        titleEditText = (EditText) findViewById(R.id.task_title);
        descriptionEditText = (EditText) findViewById(R.id.task_description);
        dueDateSwitch = (Switch) findViewById(R.id.switch_due_date);
        dueDateLayout = (LinearLayout) findViewById(R.id.layout_due_date);

        // Add listener to buttons and switches
        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);
        dueDateSwitch.setOnCheckedChangeListener(this);

        // Initialize group dropdown list
        GroupDropdownAdapter groupDropdownAdapter = new GroupDropdownAdapter(this);
        groupSpinner.setAdapter(groupDropdownAdapter);

        // Set selected from group id
        groupSpinner.setSelection(groupDropdownAdapter.getPositionFromID(selectedGroupID));

        // Initialize importance spinner
        importanceSpinner.setAdapter(new ImportanceLevelAdapter(this));

        // Select"Normal" as default
        importanceSpinner.setSelection(1);

        // Initialize calender
        reminderCalendar = Calendar.getInstance();

        // Update button to show current date/timeF
        updateButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_task_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                saveTask();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(dateButton)) {
            showDatePicker();

        } else if (v.equals(timeButton)) {
            showTimePicker();
        }
    }

    private void showDatePicker() {
        DatePickerFragment fragment = new DatePickerFragment();

        fragment.setStartDate(reminderCalendar);
        fragment.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update reminder calender first
                reminderCalendar.set(Calendar.YEAR, year);
                reminderCalendar.set(Calendar.MONTH, monthOfYear);
                reminderCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateButtons();
            }
        });

        fragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    private void showTimePicker() {
        TimePickerFragment fragment = new TimePickerFragment();

        fragment.setStartTime(reminderCalendar);
        fragment.setListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update reminder calender first
                reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                reminderCalendar.set(Calendar.MINUTE, minute);

                updateButtons();
            }
        });

        fragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
    }

    private void updateButtons() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        dateButton.setText(dateFormat.format(reminderCalendar.getTime()));
        timeButton.setText(timeFormat.format(reminderCalendar.getTime()));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        dueDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private void saveTask() {
        boolean hasDue = dueDateSwitch.isChecked();

        if (titleEditText.getText().toString().isEmpty())
            Toast.makeText(this, R.string.message_title_required, Toast.LENGTH_SHORT).show();

        else if (hasDue && reminderCalendar.getTimeInMillis() < System.currentTimeMillis())
            Toast.makeText(this, R.string.message_due_date_past, Toast.LENGTH_SHORT).show();

        else {
            Task task;

            if (hasDue) {
                task = new Task(
                        (int) groupSpinner.getSelectedItemId(),
                        titleEditText.getText().toString(),
                        descriptionEditText.getText().toString(),
                        (int) importanceSpinner.getSelectedItemId(),
                        reminderCalendar.getTimeInMillis());

            } else {
                task = new Task(
                        (int) groupSpinner.getSelectedItemId(),
                        titleEditText.getText().toString(),
                        descriptionEditText.getText().toString(),
                        (int) importanceSpinner.getSelectedItemId());
            }

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            task.save(databaseHelper);
            databaseHelper.close();

            // Start scheduling notification
            if (hasDue)
                startService(NotificationService.newNotification(this, task.getId()));

            finish();
        }
    }

}
