package com.lafilgroup.merchandisinginventory;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.CheckGPSDevice;

import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.DBHelper;
import com.lafilgroup.merchandisinginventory.storelogin.SelectSchedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RR Salas on 3/12/2018.
 */

public class Login extends AppCompatActivity implements View.OnClickListener
{
    EditText txtUsername, txtPassword;
    Button btnStoreLogin, btnOutdoorLogin;
    TextView lblVersion;
    public static SharedPreferences sharedPreferences;
    public static String SHARED_PREF = "splogin";
    MessageDialog messageDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkGpsStatus();
        castObj();
        lblVersion.setText("Version " +BuildConfig.VERSION_NAME);
    }

    private void castObj()
    {
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword =findViewById(R.id.txtPassword);
        btnStoreLogin=findViewById(R.id.btnStoreLogin);
        btnOutdoorLogin=findViewById(R.id.btnOutdoorLogin);
        lblVersion=findViewById(R.id.lblVersion);
        sharedPreferences=getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        btnStoreLogin.setOnClickListener(this);
        btnOutdoorLogin.setOnClickListener(this);
    }

    protected void onResume()
    {
        super.onResume();
        boolean logged =sharedPreferences.getBoolean("logged",false);
        if (logged)
        {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
        checkGpsStatus();
    }

    public void checkGpsStatus()
    {
        CheckGPSDevice checkGPSDevice=new CheckGPSDevice(this);
        checkGPSDevice.CheckGpsStatus();
    }


    public static void sharedLoginPref(String user_id, String login_type, String full_name,String api_token, String customer_name, String customer_code,String schedule_id, String customer_id, String time_in)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("logged",true);
        editor.putString("user_id",user_id);
        editor.putString("login_type",login_type);
        editor.putString("full_name",full_name);
        editor.putString("api_token",api_token);
        editor.putString("customer_name",customer_name);
        editor.putString("customer_code",customer_code);
        editor.putString("schedule_id",schedule_id);
        editor.putString("customer_id",customer_id);
        editor.putString("time_in",time_in);
        editor.commit();
        editor.apply();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnStoreLogin:
                login(getString(R.string.host) + getString(R.string.login_url),txtUsername.getText().toString(),txtPassword.getText().toString(),"Store");
                break;
            case R.id.btnOutdoorLogin:
                login(getString(R.string.host) + getString(R.string.login_url),txtUsername.getText().toString(),txtPassword.getText().toString(),"Outdoor");
                break;
        }
    }

    public void login(String url, final String username, final String password, final String login_type)
    {
        final WaitingDialog waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (!response.toString().equals("failed"))
                {
                    if (!response.toString().equals("wrong_password"))
                    {
                        try
                        {
                            JSONObject parentJSonObject =new JSONObject(response);
                            JSONArray jsonArray = parentJSonObject.getJSONArray("items");
                            JSONObject jsonObject =jsonArray.getJSONObject(0);
                            String account_status =jsonObject.getString("account_status");
                            if (account_status.equals("ACTIVE"))
                            {
                                GlobalVar.user_id=jsonObject.getString("merchandiser_id");
                                GlobalVar.api_token=jsonObject.getString("api_token");
                                GlobalVar.full_name=jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                                deleteImage();
                                deleteImageAll();
                                if (login_type.equals("Outdoor"))
                                {
                                    GlobalVar.login_type="Outdoor";
                                    sharedLoginPref(GlobalVar.user_id,GlobalVar.login_type, GlobalVar.full_name,GlobalVar.api_token,"","","","","");
                                    //Toast.makeText(Login.this, "Login successfully.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    GlobalVar.login_type="Store";
                                    startActivity(new Intent(Login.this, SelectSchedule.class));
                                    finish();
                                }
                            }
                            else
                            {
                                messageDialog = new MessageDialog(Login.this,"Error","Your account is deactivated.");
                                messageDialog.messageError();
                            }
                        }
                        catch (JSONException e)
                        {
                            messageDialog = new MessageDialog(Login.this,"Error",e.getMessage().toString());
                            messageDialog.messageError();
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        messageDialog = new MessageDialog(Login.this,"Error","Incorrect credentials.");
                        messageDialog.messageError();
                        txtUsername.setText("");
                        txtPassword.setText("");
                    }
                }
                else
                {
                    messageDialog = new MessageDialog(Login.this,"Error","Incorrect credentials.");
                    messageDialog.messageError();
                    txtUsername.setText("");
                    txtPassword.setText("");
                }
                waitingDialog.closeDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                waitingDialog.closeDialog();
                //messageDialog = new MessageDialog(Login.this,"Error","Network is unreachable.");
                messageDialog = new MessageDialog(Login.this,"Error","Network is unreachable.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map <String, String> params =new HashMap<>();
                params.put("username",username );
                params.put("password",password );
                return params;
            }
        };
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }

    public void deleteImage()
    {
//        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
//        if (dir.isDirectory())
//        {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++)
//            {
//                new File(dir, children[i]).delete();
//            }
//        }
    }

    public void deleteImageAll()
    {
//        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "Pictures");
//        if (dir.isDirectory())
//        {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++)
//            {
//                new File(dir, children[i]).delete();
//            }
//        }
    }
}
