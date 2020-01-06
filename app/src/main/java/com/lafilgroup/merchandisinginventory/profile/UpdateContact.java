package com.lafilgroup.merchandisinginventory.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.Login;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.chrono.MinguoDate;
import java.util.HashMap;
import java.util.Map;

public class UpdateContact extends AppCompatActivity {
    Bundle extras;
    EditText txtEmail, txtContactNumber, txtAddress;
    Button btnConfirm, btnRequestChangeAddress;
    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);
        this.setTitle("Update Contact Information");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtEmail=findViewById(R.id.txtEmail);
        txtContactNumber=findViewById(R.id.txtContactNumber);
        txtAddress=findViewById(R.id.txtAddress);
        btnConfirm=findViewById(R.id.btnConfirm);
        btnRequestChangeAddress=findViewById(R.id.btnRequestChangeAddress);

        extras=getIntent().getExtras();
        txtEmail.setText(extras.getString("email"));
        txtContactNumber.setText(extras.getString("contactnumber"));
        txtAddress.setText(extras.getString("address"));


        btnConfirm.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                if (isEmailValid(txtEmail.getText().toString()))
                {
                    if(isAddress(txtAddress.getText().toString()))
                    {
                        updateContact(getString(R.string.host)+ getString(R.string.update_contact_info) +GlobalVar.api_token);
                    }
                    else
                    {
                        messageDialog=new MessageDialog(UpdateContact.this,"Error","Address must be valid.");
                        messageDialog.messageError();
                }
                }
                else
                {
                    messageDialog=new MessageDialog(UpdateContact.this,"Error","Please input valid email.");
                    messageDialog.messageError();
                }
            }
        });

        btnRequestChangeAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater layoutInflater = LayoutInflater.from(UpdateContact.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_change_address, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateContact.this);
                alertDialogBuilder.setView(promptView);

                final EditText txtAddress = promptView.findViewById(R.id.txtAddress);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Toast.makeText(UpdateContact.this, txtAddress.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

    }
    
    public void updateContact(String url)
    {
        final WaitingDialog waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                waitingDialog.closeDialog();
                if (response.toString().equals("success"))
                {
                    Profile.contact_updated = true;
                    finish();
                }

                else if (response.toString().equals("failure"))
                {
                    messageDialog=new MessageDialog(UpdateContact.this,"Error","Fill the required fields.");
                    messageDialog.messageError();
                }
                else
                {
                    messageDialog=new MessageDialog(UpdateContact.this,"Error","Failed to update.");
                    messageDialog.messageError();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(UpdateContact.this,"Error","No network connectivity.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map<String, String> params =new HashMap<>();
                params.put("merchandiser_id",GlobalVar.user_id);
                params.put("email",txtEmail.getText().toString());
                params.put("contact_number",txtContactNumber.getText().toString() );
                params.put("address",txtAddress.getText().toString() );
                return params;
            }
        };
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        Profile.contact_updated = false;
        finish();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Profile.contact_updated = false;
        return;
    }


    private boolean isEmailValid(String email)
    {
        if (email.contains("@"))
        {
            return true;
        }
        return false;
    }

    private boolean isAddress(String address)
    {
        if (address.length() > 4)
        {
            return true;
        }
        return false;
    }
}
