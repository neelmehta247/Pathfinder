package com.tikotapps.pathfinder.AsyncTasks;

import android.location.Address;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by neel on 10/09/15.
 */
public class GeocoderLatLngTask extends AsyncTask<LatLng, Void, Address> {

    @Override
    protected Address doInBackground(LatLng... latLngs) {
        return null;
    }

}
