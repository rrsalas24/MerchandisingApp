package com.lafilgroup.merchandisinginventory.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by RR SALAS on 6/26/2018.
 */

public class WaitingDialog
{
    Context context;
//  ProgressDialog dialog;
    SweetAlertDialog pDialog;

    public WaitingDialog(Context context) {
        this.context = context;
    }

    public void openDialog()
    {
//        dialog=new ProgressDialog(context);
//        dialog.setMessage("Please wait...");
//        dialog.show();
//        dialog.setCancelable(false);

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#4c68fa"));
        pDialog.setTitleText("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void openDialogWithMessage(String message)
    {
//        dialog=new ProgressDialog(context);
//        dialog.setMessage("Please wait...");
//        dialog.show();
//        dialog.setCancelable(false);

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#4c68fa"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void closeDialog()
    {
        //dialog.cancel();
        pDialog.cancel();
    }

}
