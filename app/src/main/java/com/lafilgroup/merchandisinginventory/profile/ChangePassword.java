package com.lafilgroup.merchandisinginventory.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity
{

    EditText txtCurrentPassword, txtNewPassword, txtConfirmPassword;
    Button btnConfirm;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        this.setTitle("Change Password");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtCurrentPassword=findViewById(R.id.txtCurrentPassword);
        txtNewPassword=findViewById(R.id.txtNewPassword);
        txtConfirmPassword=findViewById(R.id.txtConfirmPassword);
        btnConfirm=findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (isValidated())
                {
                    updatePassword(getString(R.string.host)+getString(R.string.change_password)+GlobalVar.api_token);
                }
            }
        });
    }

    public void updatePassword(String url)
    {
        openDialog();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                dialog.cancel();
                if (response.toString().equals("success"))
                {
                    Profile.password_updated = true;
                    finish();
                    return;
                }

                else if (response.toString().equals("password_not_matched"))
                {
                    wrongMessage("Error", "Password not matched.");
                }

                else if (response.toString().equals("short_password"))
                {
                    wrongMessage("Error", "New password must be at least 6 characters.");
                }

                else if (response.toString().equals("wrong_password"))
                {
                    wrongMessage("Error", "Current password is incorrect.");
                }

                else
                {
                    wrongMessage("Error", "Failed to update.");
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                dialog.cancel();
                Toast.makeText(ChangePassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map<String, String> params =new HashMap<>();
                params.put("merchandiser_id", GlobalVar.user_id);
                params.put("current_password",txtCurrentPassword.getText().toString());
                params.put("new_password",txtNewPassword.getText().toString() );
                params.put("confirm_password",txtConfirmPassword.getText().toString() );
                return params;
            }
        };
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }


    private void openDialog()
    {
        dialog=new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.show();
        dialog.setCancelable(false);
    }

    public void wrongMessage(String title, String message)
    {
        final AlertDialog.Builder alert =new AlertDialog.Builder(ChangePassword.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alert.create().show();
    }


    public boolean onOptionsItemSelected(MenuItem item)
    {
        Profile.password_updated = false;
        finish();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Profile.password_updated = false;
        return;
    }


    private boolean isValidated()
    {
        String current_password =txtConfirmPassword.getText().toString();
        String new_password =txtConfirmPassword.getText().toString();
        String confirm_password =txtConfirmPassword.getText().toString();

        if (!current_password.equals("") && !new_password.equals("") && !confirm_password.equals(""))
        {
           return true;
        }
        else
        {
            wrongMessage("Error", "Fill up all the fields.");
            return false;
        }
    }

}
