package com.tikotapps.pathfinder.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.tikotapps.pathfinder.Interfaces.DatePickerDialogCallbacks;

import java.util.Calendar;

/**
 * Created by neel on 04/11/15 at 2:08 AM.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DatePickerDialogCallbacks mCallbacks;
    DatePickerDialog dialog;
    boolean isNew;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.setTitle("By when does the task have to be finished?");
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mCallbacks.onDatePickerCanceled();
    }

    public void setData(DatePickerDialogCallbacks callbacks, boolean isNew) {
        mCallbacks = callbacks;
        this.isNew = isNew;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mCallbacks.onDateSet(view, year, month, day, isNew);
    }
}