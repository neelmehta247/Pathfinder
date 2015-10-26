package com.tikotapps.pathfinder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by neel on 16/09/15 at 10:20 AM.
 */
public class DbUtil {

    private DbHelper mDbHelper;

    private String[] allColumns = {DbHelper._ID, DbHelper.COLUMN_NAME, DbHelper.COLUMN_TASK, DbHelper.COLUMN_DEADLINE,
            DbHelper.COLUMN_TIME, DbHelper.COLUMN_LATITUDE, DbHelper.COLUMN_LONGITUDE};

    public DbUtil(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    public void addData(String TABLE_NAME, Task task) {
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_NAME, task.name);
        values.put(DbHelper.COLUMN_TASK, task.task);
        values.put(DbHelper.COLUMN_DEADLINE, task.deadline);
        values.put(DbHelper.COLUMN_TIME, task.time_required);
        values.put(DbHelper.COLUMN_LATITUDE, task.latitude);
        values.put(DbHelper.COLUMN_LONGITUDE, task.longitude);

        mDbHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public ArrayList<Task> getAllData(String TABLE_NAME) {
        ArrayList<Task> list = new ArrayList<>();

        Cursor cursor = mDbHelper.getReadableDatabase().query(TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Task task = new Task();

            task.id = cursor.getInt(0);
            task.name = cursor.getString(1);
            task.task = cursor.getString(2);
            task.deadline = cursor.getLong(3);
            task.time_required = cursor.getLong(4);
            task.latitude = cursor.getDouble(5);
            task.longitude = cursor.getDouble(6);

            list.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public void updateData(String TABLE_NAME, Task task) {
        ContentValues values = new ContentValues();

        values.put(DbHelper._ID, task.id);
        values.put(DbHelper.COLUMN_NAME, task.name);
        values.put(DbHelper.COLUMN_TASK, task.task);
        values.put(DbHelper.COLUMN_DEADLINE, task.deadline);
        values.put(DbHelper.COLUMN_TIME, task.time_required);
        values.put(DbHelper.COLUMN_LATITUDE, task.latitude);
        values.put(DbHelper.COLUMN_LONGITUDE, task.longitude);

        mDbHelper.getWritableDatabase().update(TABLE_NAME, values, DbHelper._ID + " = " + task.id, null);
    }

    public Task getData(String TABLE_NAME, int id) {
        Task task = new Task();

        Cursor cursor = mDbHelper.getReadableDatabase().query(TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (cursor.getInt(0) == id) {
                task.id = cursor.getInt(0);
                task.name = cursor.getString(1);
                task.task = cursor.getString(2);
                task.deadline = cursor.getLong(3);
                task.time_required = cursor.getLong(4);
                task.latitude = cursor.getDouble(5);
                task.longitude = cursor.getDouble(6);

                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return task;
    }
}
