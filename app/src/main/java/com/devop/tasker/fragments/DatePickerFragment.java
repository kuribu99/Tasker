package com.devop.tasker.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Kong My on 21/8/2016.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String TAG = "com.devop.tasker.fragments.DatePickerFragment.TAG";

    private Calendar startDate;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment() {
        super();
        startDate = Calendar.getInstance();
        listener = null;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = startDate.get(Calendar.YEAR);
        int month = startDate.get(Calendar.MONTH);
        int day = startDate.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

}
