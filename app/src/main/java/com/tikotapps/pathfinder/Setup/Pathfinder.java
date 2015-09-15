package com.tikotapps.pathfinder.Setup;

import android.app.Application;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by neel on 15/09/15.
 */
public class Pathfinder extends Application {

    private ArrayList<Marker> markerList;
    private ArrayList<String> markerAddressList;

    @Override
    public void onCreate() {
        super.onCreate();
        markerList = new ArrayList<>(); //Fetch these from the database
        markerAddressList = new ArrayList<>();
    }

    public ArrayList<Marker> getMarkerList() {
        return markerList;
    }

    public ArrayList<String> getMarkerAddressList() {
        return markerAddressList;
    }

}
