package com.devop.tasker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.devop.tasker.db.DatabaseHelper;
import com.devop.tasker.fragments.DatePickerFragment;
import com.devop.tasker.fragments.TimePickerFragment;
import com.devop.tasker.models.Task;
import com.devop.tasker.views.GroupAdapter;
import com.devop.tasker.views.ImportanceLevelAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final int REQUEST_CODE_ADD_TASK = 1;
    public static final int RESULT_CODE_SUCCESS = 1;
    public static final String EXTRA_NEW_TASK = "com.devop.tasker.AddTaskActivity.EXTRA.NEW_TASK";

    private Button saveButton;
    private Spinner groupSpinner;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner importanceSpinner;
    private Switch dueDateSwitch;
    private LinearLayout dueDateLayout;
    private Button dateButton;
    private Button timeButton;
    private Calendar reminderCalendar;

    public static Intent newIntent(Context context) {
        return new Intent(context, AddTaskActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (Button) findViewById(R.id.button_save);
        dateButton = (Button) findViewById(R.id.button_date);
        timeButton = (Button) findViewById(R.id.button_time);
        groupSpinner = (Spinner) findViewById(R.id.group_spinner);
        importanceSpinner = (Spinner) findViewById(R.id.importance_spinner);
        titleEditText = (EditText) findViewById(R.id.task_title);
        descriptionEditText = (EditText) findViewById(R.id.task_description);
        dueDateSwitch = (Switch) findViewById(R.id.switch_due_date);
        dueDateLayout = (LinearLayout) findViewById(R.id.layout_due_date);

        saveButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);

        groupSpinner.setAdapter(new GroupAdapter(this));
        importanceSpinner.setAdapter(new ImportanceLevelAdapter(this));

        dueDateSwitch.setOnCheckedChangeListener(this);

        reminderCalendar = Calendar.getInstance();
        updateButtons();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(saveButton)) {
            save();

        } else if (v.equals(dateButton)) {
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

        dateButton.setText(String.format(dateFormat.format(reminderCalendar.getTime())));
        timeButton.setText(String.format(timeFormat.format(reminderCalendar.getTime())));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        dueDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private void save() {
        Task task;

        if (dueDateSwitch.isChecked()) {
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

        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_TASK, task);
        setResult(RESULT_CODE_SUCCESS, data);

        finish();
    }

}
