package com.lafilgroup.merchandisinginventory.storelogin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.Login;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.CircleDisplay;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.BORemarksSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.InventoryTypeSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialBalanceSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DownloadData extends AppCompatActivity implements View.OnClickListener
{
    Button btnLogin, btnRefresh;
    TextView txtUnable, txtItemBalance, txtInventoryType, txtBORemarks;
    MaterialBalanceSQL materialBalanceSQL;
    //MaterialSQL materialSQL;
    InventoryTypeSQL inventoryTypeSQL;
    BORemarksSQL boRemarksSQL;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);
        this.setTitle("Downloading Data");
        btnLogin=findViewById(R.id.btnLogin);
        btnRefresh=findViewById(R.id.btnRefresh);
        txtUnable=findViewById(R.id.txtUnable);
        //txtItemList=findViewById(R.id.txtItemList);
        txtItemBalance=findViewById(R.id.txtItemBalance);
        txtInventoryType=findViewById(R.id.txtInventoryType);
        txtBORemarks=findViewById(R.id.txtBORemarks);
        btnLogin.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        saveMaterialBalance();
        //saveMaterial();
        saveInventoryType();
        saveBORemarks();
    }

    //======================Material Balance========================================================
    public void saveMaterialBalance()
    {
        materialBalanceSQL=new MaterialBalanceSQL(this);
        String url=getString(R.string.host) + getString(R.string.get_beginning_balance) + GlobalVar.api_token;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    materialBalanceSQL.deleteRecord(materialBalanceSQL,GlobalVar.customer_code);
                    int count =0;
                    while (count< jsonArray.length())
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(count);
                        materialBalanceSQL.insertRecord(materialBalanceSQL, jsonObject.getString("material_code"), jsonObject.getString("material_description"),GlobalVar.customer_code,
                                jsonObject.getString("base_uom"), jsonObject.getString("ending_balance"), jsonObject.getString("created_at"));
                        count++;
                        txtItemBalance.setText("Item Balance: " + count);
                    }
                    txtItemBalance.setText("Item Balance: Ok");
                    btnLogin.setVisibility(View.VISIBLE);
                }
                catch (JSONException e)
                {
                    //txtItemBalance.setText("Item Balance: Error1: " +e.getMessage());
                    txtItemBalance.setText("Error1: " +e.getMessage());
                    e.printStackTrace();
                    errorButton();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //txtItemBalance.setText("Item Balance: Error2: " + error.getMessage());
                txtItemBalance.setText("Error2: " + error.getMessage() +"--"+ error.toString());
                errorButton();
                error.printStackTrace();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map <String, String> params =new HashMap<>();
                params.put("customer_code",GlobalVar.customer_code );
                return params;
            }
        };
        final int TIMEOUT_MS = 60000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy. DEFAULT_BACKOFF_MULT));
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }

//    //======================Material Item========================================================
//    public void saveMaterial()
//    {
//        materialSQL=new MaterialSQL(this);
//        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//        //String qry = "Select * from vw_customer_material where customer_code ='" +GlobalVar.customer_code +"'";
//        String qry="call p_customer_material('"+GlobalVar.customer_code+"')";
//        CustomStringRequest customStringRequest=new CustomStringRequest(this);
//        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
//            @Override
//            public void onSuccess(String response)
//            {
//                try
//                {
//                    JSONObject parentJsonObject =new JSONObject(response);
//                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
//                    materialSQL.deleteRecord(materialSQL);
//                    int count =0;
//                    while (count< jsonArray.length())
//                    {
//                        JSONObject jsonObject =jsonArray.getJSONObject(count);
//                        materialSQL.insertRecord(materialSQL,jsonObject.getString("material_conversion_id"), jsonObject.getString("material_code"),jsonObject.getString("material_description"),jsonObject.getString("upc_number"),
//                                jsonObject.getString("alternative_unit"),jsonObject.getString("base_unit"), jsonObject.getString("numerator"),jsonObject.getString("denominator"));
//                        count ++;
//                        txtItemList.setText("Item List: " +count);
//                    }
//                    txtItemList.setText("Item List: Ok");
//                }
//                catch (JSONException e)
//                {
//                    txtItemList.setText("Item List: Error");
//                    errorButton();
//                    e.printStackTrace();
//                }
//
//            }
//            @Override
//            public void onError(VolleyError error)
//            {
//                txtItemList.setText("Item List: Error");
//                errorButton();
//                error.printStackTrace();
//            }
//        });
//    }

    //================================================================================================
    public void saveInventoryType()
    {
        inventoryTypeSQL=new InventoryTypeSQL(this);
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        String qry = "call p_inventory_type";

        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    inventoryTypeSQL.deleteRecord(inventoryTypeSQL);
                    int count =0;
                    while (count< jsonArray.length())
                    {
                        JSONObject jsonObject =jsonArray.getJSONObject(count);
                        inventoryTypeSQL.insertRecord(inventoryTypeSQL,jsonObject.getString("id"), jsonObject.getString("type"));
                        count ++;
                        txtInventoryType.setText("Inventory Type: " +count);
                    }
                    txtInventoryType.setText("Inventory Type: Ok");

                }
                catch (JSONException e)
                {
                    txtInventoryType.setText("Inventory Type: Error");
                    errorButton();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                txtInventoryType.setText("Inventory Type: Error");
                errorButton();
                //Toast.makeText(MainActivity.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    //================================================================================================
    public void saveBORemarks()
    {
        boRemarksSQL=new BORemarksSQL(this);
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        String qry = "call p_bo_remarks";

        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    boRemarksSQL.deleteRecord(boRemarksSQL);
                    int count =0;
                    while (count< jsonArray.length())
                    {
                        JSONObject jsonObject =jsonArray.getJSONObject(count);
                        boRemarksSQL.insertRecord(boRemarksSQL,jsonObject.getString("id"), jsonObject.getString("remarks"));
                        count ++;
                        txtBORemarks.setText("BO Remarks: "+count);
                    }
                    txtBORemarks.setText("BO Remarks: Ok");
                }
                catch (JSONException e)
                {
                    txtBORemarks.setText("BO Remarks: Error");
                    errorButton();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                txtBORemarks.setText("BO Remarks: Error");
                errorButton();
                //Toast.makeText(MainActivity.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    public void errorButton()
    {
        txtUnable.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnLogin:
                Login.sharedLoginPref(GlobalVar.user_id,GlobalVar.login_type, GlobalVar.full_name,GlobalVar.api_token,GlobalVar.customer_name,GlobalVar.customer_code,GlobalVar.schedule_id,GlobalVar.customer_id,GlobalVar.getCurrentTime());
                startActivity(new Intent(DownloadData.this, MainActivity.class));
                finish();
                break;
            case R.id.btnRefresh:
                txtUnable.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.GONE);
                saveMaterialBalance();
                //saveMaterial();
                saveInventoryType();
                saveBORemarks();
                break;
        }
    }
}
