package com.lafilgroup.merchandisinginventory.config;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by RR SALAS on 6/21/2018.
 */

public class GPSLocator
{
    Context context;

    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest = new LocationRequest();
    Boolean mRequestingLocationUpdates = true;
    //public String lat,lng;

    public GPSLocator(Context context) {
        this.context = context;
    }

    public void getCoordinates(final dataCallback callback)
    {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    callback.location(location.getLatitude()+"",location.getLongitude() + "");
//                }
//            }
//        };
//        //createLocationRequest();
//        stopLocationUpdates();
//        startLocationUpdates();
        callback.location("100","100");
    }

    public void createLocationRequest() {
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public interface dataCallback
    {
        void location(String lat,String lng);
    }
}
