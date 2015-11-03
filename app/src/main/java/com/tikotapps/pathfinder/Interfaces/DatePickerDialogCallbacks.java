package com.tikotapps.pathfinder.Interfaces;

import android.widget.DatePicker;

/**
 * Created by neel on 04/11/15 at 2:26 AM.
 */
public interface DatePickerDialogCallbacks {

    void onDateSet(DatePicker view, int year, int month, int date);

    void onDatePickerCanceled();

}
