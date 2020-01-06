package com.lafilgroup.merchandisinginventory.config;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.lafilgroup.merchandisinginventory.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by RR SALAS on 6/26/2018.
 */

public class MessageDialog
{
    Context context;
    String title;
    String message;
    SweetAlertDialog sweetAlertDialog;

    public MessageDialog(Context context, String title, String message) {
        this.context = context;
        this.title = title;
        this.message = message;
    }

//    public void messageDetails()
//    {
//        final AlertDialog.Builder alert =new AlertDialog.Builder(context);
//        alert.setTitle(title);
//        alert.setMessage(message);
//        alert.setCancelable(false);
//        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.dismiss();
//            }
//        });
//        alert.create().show();
//    }

    public void messageInformation()
    {
        sweetAlertDialog= new SweetAlertDialog(context);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(context,R.color.dialog_button));
    }

    public void messageError()
    {
        sweetAlertDialog= new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(context,R.color.dialog_button));
    }

    public void messageSuccess()
    {
        sweetAlertDialog= new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(context,R.color.dialog_button));
    }

}
