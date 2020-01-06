package com.lafilgroup.merchandisinginventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.about.About;
import com.lafilgroup.merchandisinginventory.announcement.Announcement;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementAdapter;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.CheckGPSDevice;
import com.lafilgroup.merchandisinginventory.config.CircleTransform;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GPSLocator;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.home.Home;
import com.lafilgroup.merchandisinginventory.logs.Logs;
import com.lafilgroup.merchandisinginventory.offline.OfflineSave;
import com.lafilgroup.merchandisinginventory.profile.Profile;
import com.lafilgroup.merchandisinginventory.schedule.Schedule;
import com.lafilgroup.merchandisinginventory.sendfeedback.Messages;
import com.lafilgroup.merchandisinginventory.sendfeedback.SendFeedback;
import com.lafilgroup.merchandisinginventory.sqlconfig.BORemarksSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.CustomerSubmitSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.DeliveryRemarksSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.InventoryTypeSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialBalanceSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.OfflineInventoryHeaderSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionExpirationSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;
import com.lafilgroup.merchandisinginventory.stockinventory.StockInventory;
import com.lafilgroup.merchandisinginventory.storelogin.LoginTakePicture;
import com.lafilgroup.merchandisinginventory.storelogin.SelectSchedule;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    TextView lblName,lblID;
    ImageView imgMerchandiserPhoto;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    public static NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment=null;
    SharedPreferences sharedPreferences;

    MaterialSQL materialSQL;
    InventoryTypeSQL inventoryTypeSQL;
    BORemarksSQL boRemarksSQL;
    CustomerSubmitSQL customerSubmitSQL;
    TransactionItemsSQL transactionItemsSQL;
    TransactionExpirationSQL transactionExpirationSQL;
    MaterialBalanceSQL materialBalanceSQL;
    DeliveryRemarksSQL deliveryRemarksSQL;
    MaterialBalanceAdapter materialBalanceAdapter;
    MessageDialog messageDialog;
    SweetAlertDialog sweetAlertDialog;
    WaitingDialog waitingDialog;
    Menu myMenuSendMessage,myMenuBalance;
    OfflineInventoryHeaderSQL offlineInventoryHeaderSQL;

    GPSLocator gpsLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        defaultFragment();
        sharedPreferences=getSharedPreferences(Login.SHARED_PREF,MODE_PRIVATE);
        GlobalVar.user_id=sharedPreferences.getString("user_id","");
        GlobalVar.api_token=sharedPreferences.getString("api_token","");
        GlobalVar.full_name=sharedPreferences.getString("full_name","");
        GlobalVar.login_type=sharedPreferences.getString("login_type","");
        GlobalVar.customer_name=sharedPreferences.getString("customer_name","");
        GlobalVar.customer_code=sharedPreferences.getString("customer_code","");
        GlobalVar.schedule_id=sharedPreferences.getString("schedule_id","");
        GlobalVar.customer_id=sharedPreferences.getString("customer_id","");
        GlobalVar.time_in=sharedPreferences.getString("time_in","");
        headerDetails();

        if (GlobalVar.login_type.equals("Store"))
        {
            //saveMaterialBalance();
             saveMaterial();
            //saveInventoryType();
            //saveBORemarks();
            //saveDeliveryRemarks();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkGpsStatus();
    }

    public void checkGpsStatus()
    {
        CheckGPSDevice checkGPSDevice=new CheckGPSDevice(this);
        checkGPSDevice.CheckGpsStatus();
    }

    public void headerDetails()
    {
        View hView =navigationView.getHeaderView(0);
        lblName=hView.findViewById(R.id.lblName);
        lblID=hView.findViewById(R.id.lblID);
        imgMerchandiserPhoto=hView.findViewById(R.id.imgMerchandiserPhoto);
        lblName.setText(GlobalVar.full_name);
        lblID.setText("Merchandiser ID: " + GlobalVar.user_id);
        getUserImage();
    }

    public void getUserImage()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_merchandiser where merchandiser_id = '" + GlobalVar.user_id +  "'";
        String qry = "call p_merchandiser('" + GlobalVar.user_id +  "')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    JSONObject jsonObject =jsonArray.getJSONObject(0);
                    Picasso.with(MainActivity.this).load(getString(R.string.host) + getString(R.string.image_url)+jsonObject.getString("image_path")).transform(new CircleTransform()).placeholder(R.mipmap.ic_launcher_round).into(imgMerchandiserPhoto);
                }
                catch (JSONException e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                //Toast.makeText(MainActivity.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    //================================================================================================
    public void saveMaterial()
    {
        waitingDialog=new WaitingDialog(MainActivity.this);
        waitingDialog.openDialog();
        materialSQL=new MaterialSQL(this);
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_customer_material where customer_code ='" +GlobalVar.customer_code +"'";
        String qry="call p_customer_material('"+GlobalVar.customer_code+"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    materialSQL.deleteRecord(materialSQL);
                    int count =0;
                    while (count< jsonArray.length())
                    {
                        JSONObject jsonObject =jsonArray.getJSONObject(count);
                        materialSQL.insertRecord(materialSQL,jsonObject.getString("material_conversion_id"), jsonObject.getString("material_code"),jsonObject.getString("material_description"),jsonObject.getString("upc_number"),
                                jsonObject.getString("alternative_unit"),jsonObject.getString("base_unit"), jsonObject.getString("numerator"),jsonObject.getString("denominator"));
                        count ++;
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                waitingDialog.closeDialog();
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

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
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
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
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                //Toast.makeText(MainActivity.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    //================================================================================================
    public void saveDeliveryRemarks()
    {
        deliveryRemarksSQL=new DeliveryRemarksSQL(this);
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        String qry = "call p_delivery_remarks";

        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    deliveryRemarksSQL.deleteRecord(deliveryRemarksSQL);
                    int count =0;
                    while (count< jsonArray.length())
                    {
                        JSONObject jsonObject =jsonArray.getJSONObject(count);
                        deliveryRemarksSQL.insertRecord(deliveryRemarksSQL,jsonObject.getString("id"), jsonObject.getString("remarks"));
                        count ++;
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                //Toast.makeText(MainActivity.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

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
                    materialBalanceSQL.deleteRecord(materialBalanceSQL, GlobalVar.customer_code);
                    int count =0;
                    while (count< jsonArray.length())
                    {
                        JSONObject jsonObject =jsonArray.getJSONObject(count);
                        materialBalanceSQL.insertRecord(materialBalanceSQL,jsonObject.getString("material_code"),jsonObject.getString("material_description"),jsonObject.getString(GlobalVar.customer_code),
                                jsonObject.getString("base_uom"),jsonObject.getString("ending_balance"),jsonObject.getString("created_at"));
                        count++;
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
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
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            if (fragment instanceof Home)
            {
                if (doubleBackToExitPressedOnce)
                {
                    super.onBackPressed();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please tap back again to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
            else
            {
                defaultFragment();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (toggle.onOptionsItemSelected(item))
        {
            //getUserImage();
            checkGpsStatus();
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.mybutton)
        {
            startActivity(new Intent(this, SendFeedback.class));
        }
        else if (id==R.id.mybuttonBalance)
        {
            viewBalance();
        }
        return super.onOptionsItemSelected(item);
    }

    public void viewBalance()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_material_balance,null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        ListView lvwMaterialBalance;
        TextView lblTotal;

        lvwMaterialBalance = promptView.findViewById(R.id.lvwMaterialBalance);
        lblTotal=promptView.findViewById(R.id.lblTotal);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        materialBalanceSQL=new MaterialBalanceSQL(this);
        ArrayList<MaterialBalanceSQL.MaterialBalanceDTO> dto=materialBalanceSQL.getBalance(materialBalanceSQL, GlobalVar.customer_code);
        materialBalanceAdapter=new MaterialBalanceAdapter(this,dto);
        lvwMaterialBalance.setAdapter(materialBalanceAdapter);
        lblTotal.setText("Total: " +dto.size()+" item(s)");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_icon, menu);

        myMenuSendMessage=menu;
        myMenuBalance=menu;

        myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
        myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void defaultFragment()
    {
        fragment=new Home();
        if (fragment != null)
        {
            this.setTitle(getString(R.string.app_name));
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fLayout, fragment);
            fragmentTransaction.commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuHome)
        {
            this.setTitle(getString(R.string.app_name));
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            fragment =new Home();
        }
        else if (id == R.id.menuProfile)
        {
            this.setTitle("Profile");
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            fragment =new Profile();
        }
        else if (id == R.id.menuSchedule)
        {
            this.setTitle("Schedule");
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            fragment =new Schedule();
        }
        else if (id == R.id.menuStockInventory)
        {
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            if ( GlobalVar.login_type.equals("Store"))
            {
                myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(true);
                customerSubmitSQL = new CustomerSubmitSQL(this);
                offlineInventoryHeaderSQL=new OfflineInventoryHeaderSQL(this);

                ArrayList<CustomerSubmitSQL.CustomerSubmitDTO> dtoSubmit =customerSubmitSQL.searchItem(customerSubmitSQL,GlobalVar.customer_code,GlobalVar.getCurrentDate(0));
                ArrayList<OfflineInventoryHeaderSQL.OfflineInventoryHeaderDTO> dtoOffline = offlineInventoryHeaderSQL.searchItem(offlineInventoryHeaderSQL,GlobalVar.customer_code);
                if (dtoSubmit.size()<=0)
                {
                    if (dtoOffline.size()<=0)
                    {
                        this.setTitle("Submit Inventory");
                        fragment = new StockInventory();
                    }
                    else
                    {
                        messageDialog=new MessageDialog(this,"","You have a pending inventory in offline save for " + GlobalVar.customer_name + ".");
                        messageDialog.messageInformation();
                        myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
                    }
                }
                else
                {
                    messageDialog=new MessageDialog(this,"","You already submitted inventory for " + GlobalVar.customer_name +" today.");
                    messageDialog.messageInformation();
                    myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
                }
            }
            else
            {
                messageDialog=new MessageDialog(this,"","You need store login to make transaction.");
                messageDialog.messageInformation();
            }
        }
        else if (id == R.id.menuAnnouncement)
        {
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            this.setTitle("Announcements");
            fragment = new Announcement();
        }

        else if (id == R.id.menuLogs)
        {
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            this.setTitle("Inventory Logs");
            fragment =new Logs();
        }

        else if (id == R.id.menuOfflineSaving)
        {
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            this.setTitle("Offline Inventory");
            fragment =new OfflineSave();
        }

        else if (id == R.id.menuSendFeedback)
        {
            //startActivity(new Intent(this, Messages.class));
            this.setTitle("Messages");
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(true);
            fragment =new Messages();
        }
        else if (id == R.id.menuAbout)
        {
            this.setTitle("About");
            myMenuBalance.findItem(R.id.mybuttonBalance).setVisible(false);
            myMenuSendMessage.findItem(R.id.mybutton).setVisible(false);
            fragment =new About();
        }
        else if (id == R.id.menuLogout)
        {
            if ( GlobalVar.login_type.equals("Store"))
            {
                if (saveItemCount() == 0)
                {

                    sweetAlertDialog= new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
                    sweetAlertDialog.setContentText("(Note: Log out served as your punch out in " + GlobalVar.customer_name + ".)");
                }
                else
                {
                    if (saveOfflineCount()==0)
                    {
                        sweetAlertDialog= new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setContentText("(Note: Log out served as your punch out in " + GlobalVar.customer_name + " and save items in submit inventory form will remove. To avoid removing, kindly save as offline.) ");
                    }
                    else
                    {
                        sweetAlertDialog= new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setContentText("(Note: Log out served as your punch out in " + GlobalVar.customer_name + ".)");
                    }

                }
            }
            else
            {
                sweetAlertDialog= new SweetAlertDialog(this);
            }

            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitle("Do you want to log out?");
            sweetAlertDialog.setCancelText("Cancel");
            sweetAlertDialog.setConfirmText("Confirm");

            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog)
                {
                    sDialog.cancel();
                    if ( GlobalVar.login_type.equals("Store"))
                    {
                        gpsLocator=new GPSLocator(MainActivity.this);
                        gpsLocator.getCoordinates(new GPSLocator.dataCallback()
                        {
                            @Override
                            public void location(String lat, String lng)
                            {
                                checkLocation(GlobalVar.customer_code,lat,lng,GlobalVar.customer_name);
                            }
                        });
                    }

                    else
                    {
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        editor.apply();
                        clearVariable();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    }
                }
            });

            sweetAlertDialog.showCancelButton(true);
            sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                        drawer.closeDrawer(GravityCompat.START);
                        sDialog.cancel();
                    }
                });
            sweetAlertDialog.show();
        }

        if (fragment != null)
        {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fLayout, fragment);
            fragmentTransaction.commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkLocation(final String customer_code, final String lat, final String lng, final String customer_name)
    {
        waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.location) + GlobalVar.api_token;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                waitingDialog.closeDialog();
                if (response.toString().equals("success"))
                {
                    insertAttendance();
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog =new MessageDialog(MainActivity.this,"","You are not in " + customer_name +" area.");
                    messageDialog.messageError();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                messageDialog =new MessageDialog(MainActivity.this,"Error","Network is unreachable.");
                messageDialog.messageError();

                waitingDialog.closeDialog();
                error.printStackTrace();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map <String, String> params =new HashMap<>();
                params.put("customer_code",customer_code );
                params.put("lat",lat );
                params.put("lng",lng );
                return params;
            }
        };
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }

    public final Integer saveItemCount()
    {
        transactionItemsSQL=new TransactionItemsSQL(this);
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto=transactionItemsSQL.allItems(transactionItemsSQL, GlobalVar.customer_code);
        return dto.size();
    }

    public final Integer saveOfflineCount()
    {
        offlineInventoryHeaderSQL=new OfflineInventoryHeaderSQL(this);
        ArrayList<OfflineInventoryHeaderSQL.OfflineInventoryHeaderDTO> dto=offlineInventoryHeaderSQL.searchItem(offlineInventoryHeaderSQL, GlobalVar.customer_code);
        return dto.size();
    }

    public void insertAttendance()
    {
        waitingDialog =new WaitingDialog(this);
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.exec_url) + GlobalVar.api_token;
        //String qry = "update merchandiser_attendance set time_out='"+GlobalVar.getCurrentTime()+"' where schedule_id = '"+GlobalVar.schedule_id+"'";
        String qry="call p_update_merchandiser_attendance('"+GlobalVar.getCurrentTime()+"','"+GlobalVar.schedule_id+"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                if (response.toString().equals("success"))
                {
                    updateSchedule();
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog=new MessageDialog(MainActivity.this,"Error","There is an error upon log out. Try again later." );
                    messageDialog.messageError();
                }
                waitingDialog.closeDialog();
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(MainActivity.this,"Error","Network is unreachable." );
                messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }

    public void updateSchedule()
    {
        String url=getString(R.string.host) + getString(R.string.exec_url) + GlobalVar.api_token;
        //String qry = "update merchandiser_schedule set status='001' where id = '"+GlobalVar.schedule_id+"'";
        String qry="call p_update_merchandiser_schedule('"+GlobalVar.schedule_id+"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                if (response.toString().equals("success"))
                {
                    if (saveItemCount() != 0)
                    {
                        if (saveOfflineCount() ==0)
                        {
                            transactionItemsSQL=new TransactionItemsSQL(MainActivity.this);
                            transactionItemsSQL.deleteCustomerItem(transactionItemsSQL,GlobalVar.customer_code);
                            transactionExpirationSQL = new TransactionExpirationSQL(MainActivity.this);
                            transactionExpirationSQL.deleteCustomerExpiration(transactionExpirationSQL,GlobalVar.customer_code);
                        }
                    }
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    editor.apply();
                    logoutMessage();
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog=new MessageDialog(MainActivity.this,"Error","There is an error upon log out. Try again later." );
                    messageDialog.messageError();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                messageDialog=new MessageDialog(MainActivity.this,"Error","Network is unreachable." );
                messageDialog.messageError();
            }
        });
    }

    public void logoutMessage()
    {
        sweetAlertDialog= new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setContentText(GlobalVar.totalDifferenceTime(GlobalVar.time_in,GlobalVar.getCurrentTime()));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitle("Success");
        sweetAlertDialog.setConfirmText("Ok");

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog)
            {
                clearVariable();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
        sweetAlertDialog.show();
    }

    public void clearVariable()
    {
        GlobalVar.user_id=null;
        GlobalVar.api_token=null;
        GlobalVar.full_name=null;
        GlobalVar.login_type=null;
        GlobalVar.customer_name=null;
        GlobalVar.schedule_id=null;
        GlobalVar.customer_code=null;
        GlobalVar.customer_id=null;
        GlobalVar.time_in=null;
    }

    public void message_count()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "select * from vw_merchandiser_message_header where merchandiser_id='" +GlobalVar.user_id+"' and seen_by_sender='no'";
        String qry = "call p_message_header('"+GlobalVar.user_id+"','no')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try {
                    JSONObject parentJSonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJSonObject.getJSONArray("items");

                    if (jsonArray.length()==0)
                    {
                        navigationView.getMenu().findItem(R.id.menuSendFeedback).setTitle("Messages)").setIcon(R.drawable.ic_inbox);
                    }
                    else
                    {
                        if (jsonArray.length()==1)
                        {
                            navigationView.getMenu().findItem(R.id.menuSendFeedback).setTitle("Messages ("+jsonArray.length()+" message)").setIcon(R.drawable.message_new);
                        }
                        else
                        {
                            navigationView.getMenu().findItem(R.id.menuSendFeedback).setTitle("Messages ("+jsonArray.length()+" messages)").setIcon(R.drawable.message_new);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {

            }
        });
    }
}
