package com.tikotapps.pathfinder.Interfaces;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by neel on 11/09/15.
 */
public interface AsyncTaskCallbacks {

    void asyncTaskOnPreExecute(String asyncTaskName);

    void asyncTaskOnProgressUpdate(String asyncTaskName, String progress);

    void asyncTaskOnPostUpdate(String asyncTaskName, Object result, Marker marker);

}
