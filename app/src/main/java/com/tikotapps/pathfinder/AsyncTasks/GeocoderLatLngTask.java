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
public class GeocoderLatLngTask extends AsyncTask<Void, Void, Address> {

    AsyncTaskCallbacks mCallbacks;
    Geocoder geocoder;
    Marker marker;
    LatLng latLng;

    public GeocoderLatLngTask(AsyncTaskCallbacks callbacks, Context context, Marker marker) {
        mCallbacks = callbacks;
        geocoder = new Geocoder(context);
        this.marker = marker;
        latLng = marker.getPosition();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallbacks.asyncTaskOnPreExecute(getClass().getName());
    }

    @Override
    protected Address doInBackground(Void... voids) {
        Address location;
        try {
            location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
        } catch (IOException | IndexOutOfBoundsException e) {
            location = null;
        }
        return location;
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        mCallbacks.asyncTaskOnPostUpdate(getClass().getName(), address, marker);
    }
}
