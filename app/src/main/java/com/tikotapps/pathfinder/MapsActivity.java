package com.tikotapps.pathfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.shell.fab.ActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ActionButton showListButton;
    private ViewFlipper viewFlipper;
    private Geocoder geocoder;
    private ArrayList<Marker> markerList;
    private ArrayList<Address> markerAdressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        showListButton = (ActionButton) findViewById(R.id.showList);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        geocoder = new Geocoder(this);
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
                Address location = getLocation(marker.getPosition());
                if (location != null) {
                    markerAdressList.remove(markerList.indexOf(marker));
                    markerAdressList.add(markerList.indexOf(marker), location);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Address location = getLocation(latLng);
                if (location != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    markerList.add(marker);
                    markerAdressList.add(location);
                }
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

                titleText.setText(location.getAddressLine(0));
                return true;
            }
        });
    }

    private Address getLocation(LatLng latLng) {
        try {
            return geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
