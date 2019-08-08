package com.huriyo.Ui;

import android.Manifest;
import android.Manifest.permission;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.huriyo.Utility.GPSTracker;
import com.huriyo.Utility.Globals;

public abstract class LocationBaseActivity extends BaseActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // LogCat tag
    private final String TAG = getClass().getName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public Location mLastLocation = null;

    Globals globals;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    public static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    public static int MIN_AUTO_DISPLACEMENT = 1; // 1 meters

    // GPSTracker class
    GPSTracker gps;

    Dialog errorDialog;

    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 1;

    private static final String[] LOCATION_PERMS = {
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (VERSION.SDK_INT >= VERSION_CODES.M && !canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Given");
                } else {
                    Log.d(TAG, "Permission not Given");
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called of Location base services");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globals == null) {
            globals = ((Globals) getApplicationContext());
        }

        // Resuming the periodic location updates
        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();

            if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
            getTrackerLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (gps != null)
            gps.stopUsingGPS();
    }

    public void getTrackerLocation() {
        // check if GPS enabled
        Log.d(TAG, "getTrackerLocation called");
        if (gps == null) {
            gps = new GPSTracker(this);
        }

        if (gps.canGetLocation() && gps.getLocation() != null) {
            mLastLocation = gps.location;
            if (globals != null && mLastLocation != null) {
                globals.setLatitude(mLastLocation.getLatitude());
                globals.setLongitude(mLastLocation.getLongitude());
            }
            getLocation(mLastLocation);
        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(permission.ACCESS_FINE_LOCATION)) && (hasPermission(permission.ACCESS_COARSE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        } else {
            return true;
        }
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {
        Log.d(TAG, "FusedLocationApi called");
        if (canAccessLocation()) {
            if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (globals != null && mLastLocation != null) {
                globals.setLatitude(mLastLocation.getLatitude());
                globals.setLongitude(mLastLocation.getLongitude());
                getLocation(mLastLocation);
            }
        }
    }

    public abstract void getLocation(Location mLastLocation);

    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
//			btnStartLocationUpdates.setText(getString(R.string.btn_stop_location_updates));
            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
//			btnStartLocationUpdates.setText(getString(R.string.btn_start_location_updates));
            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(MIN_AUTO_DISPLACEMENT);
        }
    }

    /**
     * Method to verify google play services on the device
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                if (errorDialog == null) {
                    errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
                    if (errorDialog != null) {
                        errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                errorDialog = null;
                            }
                        });

                        errorDialog.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                errorDialog = null;
                            }
                        });
                        errorDialog.show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    public boolean isPlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        if (canAccessLocation()) {
            if (mGoogleApiClient != null && mLocationRequest != null)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        Globals.mLastLocation = mLastLocation = location;
        Log.d(TAG, "onLocationChanged called : (" + location.getLatitude() + ", " + location.getLongitude() + ")");
        // Displaying the new location on UI
//        displayLocation();
    }
}
