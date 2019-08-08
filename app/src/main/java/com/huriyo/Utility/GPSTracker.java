package com.huriyo.Utility;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;


import com.huriyo.R;
import com.huriyo.Ui.LocationBaseActivity;

import java.util.List;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    private String TAG = getClass().getName();

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    public Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = LocationBaseActivity.MIN_AUTO_DISPLACEMENT; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = LocationBaseActivity.UPDATE_INTERVAL; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.d(TAG, "no network provider is enabled");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d(TAG, "isNetworkEnabled : " + isNetworkEnabled);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Log.d(TAG, "locationManager != null");
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Log.d(TAG, "Network latitude : " + latitude);
                            Log.d(TAG, "Network longitude : " + longitude);
                        } else {
                            Log.d(TAG, "location == null");
                        }
                    } else {
                        Log.d(TAG, "locationManager == null");
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.d(TAG, "isGPSEnabled : " + isGPSEnabled);
                    if (location == null) {
                        Log.d(TAG, "location == null");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            Log.d(TAG, "locationManager != null");
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                Log.d(TAG, "GPS latitude : " + latitude);
                                Log.d(TAG, "GPS longitude : " + longitude);
                            } else {
                                Log.d(TAG, "location == null");
                            }
                        } else {
                            Log.d(TAG, "locationManager == null");
                        }
                    }
                }

                if (location == null) {
                    // List all providers:
                    List<String> providers = locationManager.getAllProviders();
                    for (String provider : providers) {
                        Log.d(TAG, provider);
                    }

                    Criteria criteria = new Criteria();
                    String bestProvider = locationManager.getBestProvider(criteria, false);
                    Log.d(TAG, "bestProvider : " + bestProvider);

                    location = locationManager.getLastKnownLocation(bestProvider);
                    if (location != null) {
                        Log.d(TAG, "location != null");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Log.d(TAG, "bestProvider latitude : " + latitude);
                        Log.d(TAG, "bestProvider longitude : " + longitude);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public static boolean isGPSSettingEnable(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static void showGPSSettingsAlertIfRequired(Activity activity) {
        if (!isGPSSettingEnable(activity)) {
            showGPSSettingsAlert(activity);
        }
    }

    public static void showGPSSettingsAlert(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("GPS is not enabled. Do you want to go to settings menu?")
                .setCancelable(false)
                    .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                activity.startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
//                .setNegativeButton(activity.getString(R.string.location_cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged latitude : " + location.getLatitude());
        Log.d(TAG, "onLocationChanged longitude : " + location.getLongitude());
        this.location = location;
        getLatitude();
        getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
