package com.lafilgroup.merchandisinginventory.config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.lafilgroup.merchandisinginventory.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by RR SALAS on 6/26/2018.
 */

public class CheckGPSDevice
{
    Context context;
    LocationManager locationManager ;
    boolean gpsStatus;
    SweetAlertDialog sweetAlertDialog;

    public CheckGPSDevice(Context context) {
        this.context = context;
    }

    public void CheckGpsStatus()
    {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gpsStatus == true)
        {

        }
        else
        {
            sweetAlertDialog= new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitleText("Error");
            sweetAlertDialog.setContentText("Location service is disabled. Kindly open the GPS.");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
            btn.setBackgroundColor(ContextCompat.getColor(context,R.color.dialog_button));
            sweetAlertDialog .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    ((Activity)context).finish();
                }
            });
        }
    }
}
