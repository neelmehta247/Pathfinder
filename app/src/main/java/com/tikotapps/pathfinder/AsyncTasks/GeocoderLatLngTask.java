package com.tikotapps.pathfinder.AsyncTasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tikotapps.pathfinder.Interfaces.AsyncTaskCallbacks;

import java.io.IOException;

/**
 * Created by neel on 10/09/15.
 */
public class GeocoderLatLngTask extends AsyncTask<LatLng, Void, Address> {

    AsyncTaskCallbacks mCallbacks;
    Geocoder geocoder;
    boolean isNewMarker;
    Marker marker;
    LatLng latLng;

    public GeocoderLatLngTask(AsyncTaskCallbacks callbacks, Context context, boolean isNewMarker, Marker marker) {
        mCallbacks = callbacks;
        geocoder = new Geocoder(context);
        this.isNewMarker = isNewMarker;
        this.marker = marker;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallbacks.asyncTaskOnPreExecute(getClass().getName());
    }

    @Override
    protected Address doInBackground(LatLng... latLngs) {
        Address location;
        latLng = latLngs[0];
        try {
            location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
        } catch (IOException e) {
            location = null;
        }
        return location;
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        if (isNewMarker) {
            mCallbacks.asyncTaskOnPostUpdate(getClass().getName(), address, latLng, true);
        } else {
            mCallbacks.asyncTaskOnPostUpdate(getClass().getName(), address, marker, false);
        }
    }
}
