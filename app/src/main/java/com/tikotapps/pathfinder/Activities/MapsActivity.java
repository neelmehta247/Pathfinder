package com.tikotapps.pathfinder.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.software.shell.fab.ActionButton;
import com.tikotapps.pathfinder.Adapters.DisplayTaskAdapter;
import com.tikotapps.pathfinder.AsyncTasks.GeocoderLatLngTask;
import com.tikotapps.pathfinder.Database.DbHelper;
import com.tikotapps.pathfinder.Database.DbUtil;
import com.tikotapps.pathfinder.Database.Task;
import com.tikotapps.pathfinder.Fragments.DatePickerFragment;
import com.tikotapps.pathfinder.Interfaces.AsyncTaskCallbacks;
import com.tikotapps.pathfinder.Interfaces.DatePickerDialogCallbacks;
import com.tikotapps.pathfinder.R;
import com.tikotapps.pathfinder.Setup.CustomMapTileProvider;
import com.tikotapps.pathfinder.Setup.Pathfinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements AsyncTaskCallbacks, DatePickerDialogCallbacks {

    private AlertDialog basicInfoAlertDialog;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ActionButton showListButton;
    private ViewFlipper viewFlipper;
    private DbUtil db;
    private ArrayList<Marker> markerList;
    private Task task;
    private int spinnerSelection;
    private RecyclerView taskRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        task = new Task();

        db = new DbUtil(this);
        ((Pathfinder) getApplication()).updateTaskArrayList(db, DbHelper.TABLE_TASKS);

        markerList = new ArrayList<>();

        setUpMapIfNeeded();

        showListButton = (ActionButton) findViewById(R.id.showList);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        taskRecyclerView = (RecyclerView) findViewById(R.id.taskRecyclerView);

        taskRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        taskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        taskRecyclerView.setAdapter(new DisplayTaskAdapter(this, getTaskList()));

        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(Math.abs(viewFlipper.getDisplayedChild() - 1));
            }
        });

        QuickReturnRecyclerViewOnScrollListener scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(showListButton).minFooterTranslation(430).isSnappable(true).build();

        taskRecyclerView.setOnScrollListener(scrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onBackPressed() {
        if (viewFlipper.getDisplayedChild() == 1) {
            viewFlipper.setDisplayedChild(0);
        } else {
            super.onBackPressed();
        }
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

        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new CustomMapTileProvider(getResources().getAssets())));

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        @SuppressWarnings("ResourceType") Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            CameraPosition position = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(17).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }

        for (int i = 0; i < getTaskList().size(); i++) {
            LatLng latLng = new LatLng(getTaskList().get(i).latitude, getTaskList().get(i).longitude);
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
            markerList.add(marker);
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
                getTaskList().get(markerList.indexOf(marker)).name = null;
                getTaskList().get(markerList.indexOf(marker)).latitude = marker.getPosition().latitude;
                getTaskList().get(markerList.indexOf(marker)).longitude = marker.getPosition().longitude;
                db.updateData(DbHelper.TABLE_TASKS, getTaskList().get(markerList.indexOf(marker)));
                ((Pathfinder) getApplication()).updateTaskArrayList(db, DbHelper.TABLE_TASKS);
                new GeocoderLatLngTask(MapsActivity.this, MapsActivity.this, marker).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                ((DisplayTaskAdapter) taskRecyclerView.getAdapter()).updateAdapter(getTaskList());
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng latLng) {
                View basicInfoDialog = View.inflate(MapsActivity.this, R.layout.input_marker_info, null);

                final EditText textTask = (EditText) basicInfoDialog.findViewById(R.id.editTextTask);
                final EditText textTime = (EditText) basicInfoDialog.findViewById(R.id.editTextTimeRequired);
                Button buttonNext = (Button) basicInfoDialog.findViewById(R.id.buttonNext);
                Button cancelButton = (Button) basicInfoDialog.findViewById(R.id.buttonCancel);
                final Spinner spinnerTime = (Spinner) basicInfoDialog.findViewById(R.id.spinnerTime);

                if (task.task != null && task.time_required != 0) {
                    textTask.setText(task.task);
                    textTime.setText(String.valueOf(task.time_required));
                    spinnerTime.setSelection(spinnerSelection);
                }

                basicInfoAlertDialog = new AlertDialog.Builder(MapsActivity.this).setView(basicInfoDialog).setCancelable(false).create();
                basicInfoAlertDialog.show();

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        basicInfoAlertDialog.dismiss();

                        task.task = null;
                        task.time_required = 0;
                        task.deadline = 0;
                        task.name = null;
                        spinnerSelection = 0;
                    }
                });

                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (textTask.getText().toString().equals("") || textTime.getText().toString().equals("")) {
                            Toast.makeText(MapsActivity.this, "Please Enter The Information", Toast.LENGTH_SHORT).show();
                        } else {
                            task.name = null;
                            task.task = textTask.getText().toString();
                            task.time_required = Long.parseLong(textTime.getText().toString());
                            task.latitude = latLng.latitude;
                            task.longitude = latLng.longitude;
                            spinnerSelection = spinnerTime.getSelectedItemPosition();
                            basicInfoAlertDialog.dismiss();
                            DatePickerFragment datePickerFragment = new DatePickerFragment();
                            datePickerFragment.setCallbacks(MapsActivity.this);
                            datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                        }
                    }
                });
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                View dialogView = View.inflate(MapsActivity.this, R.layout.marker_popup_view, null);

                TextView titleText = (TextView) dialogView.findViewById(R.id.titleText);
                TextView informationText = (TextView) dialogView.findViewById(R.id.informationText);
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
                        db.deleteData(DbHelper.TABLE_TASKS, getTaskList().get(markerList.indexOf(marker)).id);
                        markerList.remove(markerList.indexOf(marker));
                        marker.remove();

                        ((DisplayTaskAdapter) taskRecyclerView.getAdapter()).updateAdapter(getTaskList());
                    }
                });

                titleText.setText(getTaskList().get(markerList.indexOf(marker)).task);

                String address = getTaskList().get(markerList.indexOf(marker)).name;
                String time = "";
                if (getTaskList().get(markerList.indexOf(marker)).time_required > 3600) {
                    if (getTaskList().get(markerList.indexOf(marker)).time_required % 3600 == 0) {
                        if (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 == 1) {
                            time += (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 + " hour");
                        } else {
                            time += (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 + " hours");
                        }
                    } else {
                        if (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 == 1) {
                            if ((getTaskList().get(markerList.indexOf(marker)).time_required % 3600) / 60 == 1) {
                                time += (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 + " hour " + (getTaskList().get(markerList.indexOf(marker)).time_required % 3600) / 60 + " minute");
                            } else {
                                time += (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 + " hour " + (getTaskList().get(markerList.indexOf(marker)).time_required % 3600) / 60 + " minutes");
                            }
                        } else {
                            if ((getTaskList().get(markerList.indexOf(marker)).time_required % 3600) / 60 == 1) {
                                time += (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 + " hours " + (getTaskList().get(markerList.indexOf(marker)).time_required % 3600) / 60 + " minute");
                            } else {
                                time += (getTaskList().get(markerList.indexOf(marker)).time_required / 3600 + " hours " + (getTaskList().get(markerList.indexOf(marker)).time_required % 3600) / 60 + " minutes");
                            }
                        }
                    }
                } else if (getTaskList().get(markerList.indexOf(marker)).time_required > 60) {
                    if (getTaskList().get(markerList.indexOf(marker)).time_required / 60 == 1) {
                        time += getTaskList().get(markerList.indexOf(marker)).time_required / 60 + " minute";
                    } else {
                        time += getTaskList().get(markerList.indexOf(marker)).time_required / 60 + " minutes";
                    }
                } else {
                    if (getTaskList().get(markerList.indexOf(marker)).time_required == 1) {
                        time += getTaskList().get(markerList.indexOf(marker)).time_required + " second";
                    } else {
                        time += getTaskList().get(markerList.indexOf(marker)).time_required + " seconds";
                    }
                }

                if (address != null) {
                    informationText.setText(address + " - " + time);
                } else {
                    informationText.setText(time);
                    new GeocoderLatLngTask(MapsActivity.this, MapsActivity.this, marker).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            }
        });
    }

    private ArrayList<Task> getTaskList() {
        return ((Pathfinder) getApplication()).getTaskArrayList();
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
                try {
                    getTaskList().get(markerList.indexOf(marker)).name = location.getAddressLine(0) + ", " + location.getAddressLine(1);
                    db.updateData(DbHelper.TABLE_TASKS, getTaskList().get(markerList.indexOf(marker)));
                    ((Pathfinder) getApplication()).updateTaskArrayList(db, DbHelper.TABLE_TASKS);

                    ((DisplayTaskAdapter) taskRecyclerView.getAdapter()).updateAdapter(getTaskList());
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int date) {
        switch (spinnerSelection) {
            case 0:
                break;
            case 1:
                task.time_required = task.time_required * 60;
                break;
            case 2:
                task.time_required = task.time_required * 3600;
                break;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date day = sdf.parse(year + "-" + month + "-" + date);
            task.deadline = day.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        task.name = null;
        LatLng latLng = new LatLng(task.latitude, task.longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        markerList.add(marker);
        getTaskList().add(task);
        db.addData(DbHelper.TABLE_TASKS, task);
        ((Pathfinder) getApplication()).updateTaskArrayList(db, DbHelper.TABLE_TASKS);
        new GeocoderLatLngTask(MapsActivity.this, MapsActivity.this, marker).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ((DisplayTaskAdapter) taskRecyclerView.getAdapter()).updateAdapter(getTaskList());

        task.task = null;
        task.time_required = 0;
        task.deadline = 0;
        task.name = null;
        spinnerSelection = 0;
    }

    @Override
    public void onDatePickerCanceled() {
        basicInfoAlertDialog.show();
    }
}
