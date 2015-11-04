package com.tikotapps.pathfinder.Setup;

import android.app.Application;

import com.tikotapps.pathfinder.Database.DbUtil;
import com.tikotapps.pathfinder.Database.Task;

import java.util.ArrayList;

/**
 * Created by neel on 15/09/15.
 */
public class Pathfinder extends Application {

    private ArrayList<Task> taskArrayList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void updateTaskArrayList(DbUtil db, String TABLE_NAME) {
        taskArrayList = db.getAllData(TABLE_NAME);
    }

    public ArrayList<Task> getTaskArrayList() {
        return taskArrayList;
    }
}
