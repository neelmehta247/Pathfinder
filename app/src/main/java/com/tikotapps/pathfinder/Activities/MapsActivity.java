package com.tikotapps.pathfinder.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.shell.fab.ActionButton;
import com.tikotapps.pathfinder.AsyncTasks.GeocoderLatLngTask;
import com.tikotapps.pathfinder.Interfaces.AsyncTaskCallbacks;
import com.tikotapps.pathfinder.R;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements AsyncTaskCallbacks {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ActionButton showListButton;
    private ViewFlipper viewFlipper;
    private ArrayList<Marker> markerList;
    private ArrayList<Address> markerAdressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        showListButton = (ActionButton) findViewById(R.id.showList);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        markerList = new ArrayList<>();
        markerAdressList = new ArrayList<>();

        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(Math.abs(viewFlipper.getDisplayedChild() - 1));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setBuildingsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        @SuppressWarnings("ResourceType") Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            CameraPosition position = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(17).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                markerAdressList.remove(markerList.indexOf(marker));
                markerAdressList.add(markerList.indexOf(marker), null);
                runAsyncTask(marker);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                markerList.add(marker);
                markerAdressList.add(null);
                runAsyncTask(marker);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                View dialogView = View.inflate(MapsActivity.this, R.layout.marker_popup_view, null);
                final Address location = markerAdressList.get(markerList.indexOf(marker));
                TextView titleText = (TextView) dialogView.findViewById(R.id.titleText);
                Button deleteButton = (Button) dialogView.findViewById(R.id.deleteButton);
                Button doneButton = (Button) dialogView.findViewById(R.id.doneButton);

                final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).setView(dialogView).create();
                alertDialog.show();

                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        markerList.remove(markerList.indexOf(marker));
                        markerAdressList.remove(markerAdressList.indexOf(location));
                        marker.remove();
                    }
                });

                if (location != null) {
                    titleText.setText(location.getAddressLine(0) + ", " + location.getAddressLine(1));
                } else {
                    titleText.setText(marker.getPosition().latitude + ", " + marker.getPosition().longitude);
                    runAsyncTask(marker);
                }
                return true;
            }
        });
    }

    @Override
    public void asyncTaskOnPreExecute(String asyncTaskName) {
        if (asyncTaskName.equals(GeocoderLatLngTask.class.getName())) {
            Toast.makeText(this, this.getResources().getText(R.string.fetchingText), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void asyncTaskOnProgressUpdate(String asyncTaskName, String progress) {

    }

    @Override
    public void asyncTaskOnPostUpdate(String asyncTaskName, Object result, Marker marker) {
        if (asyncTaskName.equals(GeocoderLatLngTask.class.getName())) {
            Address location = (Address) result;
            if (location != null) {
                markerAdressList.remove(markerList.indexOf(marker));
                markerAdressList.add(markerList.indexOf(marker), location);
            }
        }
    }

    private void runAsyncTask(Marker marker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GeocoderLatLngTask(this, this, marker).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GeocoderLatLngTask(this, this, marker).execute();
        }
    }
}
