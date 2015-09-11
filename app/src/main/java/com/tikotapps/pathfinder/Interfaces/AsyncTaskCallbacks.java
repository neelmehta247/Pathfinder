package com.tikotapps.pathfinder.Interfaces;

/**
 * Created by neel on 11/09/15.
 */
public interface AsyncTaskCallbacks {

    void asyncTaskOnPreExecute(String asyncTaskName);

    void asyncTaskOnProgressUpdate(String asyncTaskName, String progress);

    void asyncTaskOnPostUpdate(String asyncTaskName, Object result, Object otherParam, boolean isNewMarker);

}
