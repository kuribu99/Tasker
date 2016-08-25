package com.devop.tasker.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    public static final String TAG = "com.devop.tasker.fragments.TimePickerFragment.TAG";

    private Calendar startTime;
    private TimePickerDialog.OnTimeSetListener listener;

    public TimePickerFragment() {
        super();
        startTime = Calendar.getInstance();
        listener = null;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = startTime.get(Calendar.HOUR_OF_DAY);
        int minute = startTime.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

}
