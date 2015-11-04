package com.tikotapps.pathfinder.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by neel on 16/09/15 at 10:01 AM.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Pathfinder.db";
    public static final int VERSION_NUMBER = 1;

    public static final String TABLE_TASKS = "Tasks";

    public static final String _ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_TIME = "time_required";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static DbHelper singleton = null;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    public static DbHelper getInstance(Context context) {
        if (singleton == null)
            singleton = new DbHelper(context.getApplicationContext());
        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TASKS + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, "
                + COLUMN_TASK + " TEXT, " + COLUMN_DEADLINE + " BIGINT, " + COLUMN_TIME + " BIGINT, " + COLUMN_LATITUDE + " TEXT, "
                + COLUMN_LONGITUDE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS");
        onCreate(db);
    }
}
