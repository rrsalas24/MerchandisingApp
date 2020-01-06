package com.lafilgroup.merchandisinginventory.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementsDTO;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.CheckGPSDevice;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.schedule.SchedulesDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class Home extends Fragment
{
    TextView lblName, lblLoginType, lblCustomer, lblNoAnnounce, lblNoSchedule;
    LinearLayout lnrNoNetwork,lnrHome;
    AnnouncementsDTO announcementsDTO;
    AnnouncementRecyclerAdapter announcementRecyclerAdapter;
    SchedulesDTO schedulesDTO;
    ScheduleRecyclerAdapter scheduleRecyclerAdapter;
    RecyclerView rvwAnnouncementsToday,rvwSchedulesToday;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    MessageDialog messageDialog;
    WaitingDialog waitingDialog;
    CustomStringRequest customStringRequest;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        lblName=view.findViewById(R.id.lblName);
        lblLoginType=view.findViewById(R.id.lblLoginType);
        lblCustomer=view.findViewById(R.id.lblCustomer);
        lblNoAnnounce=view.findViewById(R.id.lblNoAnnounce);
        lblNoSchedule=view.findViewById(R.id.lblNoSchedule);
        rvwAnnouncementsToday=view.findViewById(R.id.rvwAnnouncementsToday);
        rvwSchedulesToday=view.findViewById(R.id.rvwSchedulesToday);

        lnrNoNetwork=view.findViewById(R.id.lnrNoNetwork);
        lnrHome=view.findViewById(R.id.lnrHome);

        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);

        lblName.setText("Welcome, " + GlobalVar.full_name +"!");
        lblLoginType.setText("Login Type: " + GlobalVar.login_type);

        if (GlobalVar.login_type.equals("Outdoor"))
        {
            lblCustomer.setText("Customer: None");
        }
        else
        {
            lblCustomer.setText("Customer: " + GlobalVar.customer_name);
        }

        getAnnouncement();
        getSchedule();
        checkGpsStatus();
        message_count();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(false);
                waitingDialog.closeDialog();
                getAnnouncement();
                getSchedule();
                checkGpsStatus();
                message_count();

            }
        });
    }

    public void checkGpsStatus()
    {
        CheckGPSDevice checkGPSDevice=new CheckGPSDevice(getActivity());
        checkGPSDevice.CheckGpsStatus();
    }

    public void getAnnouncement()
    {
        waitingDialog=new WaitingDialog(getActivity());
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//        String qry = "Select * from vw_announcements where date(created_at) between '" + GlobalVar.getCurrentDate(0) +
//                "' and '" + GlobalVar.getCurrentDate(0) + "' order by created_at desc";

        String qry = "call p_announcements('"+GlobalVar.getCurrentDate(0)+"','"+GlobalVar.getCurrentDate(0)+"')";

        customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.GONE);
                jsonToGSonAnnouncement(response);

            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.VISIBLE);
                //messageDialog=new MessageDialog(getActivity(),"Error","No network connectivity.");
                //messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSonAnnouncement(String response)
    {
        try
        {
            Gson gson = new Gson();
            announcementsDTO = gson.fromJson(response, AnnouncementsDTO.class);
            if (announcementsDTO.getItems().size() == 0) {
                announcementsDTO.getItems().clear();
                lblNoAnnounce.setVisibility(View.VISIBLE);
            } else {
                lblNoAnnounce.setVisibility(View.GONE);
            }

            announcementRecyclerAdapter = new AnnouncementRecyclerAdapter(announcementsDTO, getContext());
            layoutManager = new LinearLayoutManager(getContext());
            rvwAnnouncementsToday.setLayoutManager(layoutManager);
            rvwAnnouncementsToday.setAdapter(announcementRecyclerAdapter);
        }
        catch (Exception e)
        {
            messageDialog=new MessageDialog(getActivity(),"Error","Error occurred. (Details: Error during home announcement DTO) " + e.getMessage());
            messageDialog.messageError();
        }
    }

    public void getSchedule()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//      String qry = "Select * from vw_schedules where merchandiser_id like '" + GlobalVar.user_id +
//                "' and (date between '" + GlobalVar.getCurrentDate(0) +
//                "' and '" + GlobalVar.getCurrentDate(0) + "') order by date desc";

        String qry = "call p_schedules('"+GlobalVar.user_id+"','"+GlobalVar.getCurrentDate(0)+"','"+GlobalVar.getCurrentDate(0)+"')";
        customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                jsonToGSonSchedule(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSonSchedule(String response)
    {
        try {
            Gson gson = new Gson();
            schedulesDTO = gson.fromJson(response, SchedulesDTO.class);
            if (schedulesDTO.getItems().size() == 0) {
                schedulesDTO.getItems().clear();
                lblNoSchedule.setVisibility(View.VISIBLE);
            } else {
                lblNoSchedule.setVisibility(View.GONE);
            }

            scheduleRecyclerAdapter = new ScheduleRecyclerAdapter(schedulesDTO, getContext());
            layoutManager = new LinearLayoutManager(getContext());
            rvwSchedulesToday.setLayoutManager(layoutManager);
            rvwSchedulesToday.setAdapter(scheduleRecyclerAdapter);
        }
        catch (Exception ex)
        {
            messageDialog=new MessageDialog(getActivity(),"Error","Error occurred. (Details: Error during home schedule DTO) "+ex.getMessage());
            messageDialog.messageError();
        }
    }

    public void message_count()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "select * from vw_merchandiser_message_header where merchandiser_id='" +GlobalVar.user_id+"' and seen_by_sender='no'";
        String qry = "call p_message_header('"+GlobalVar.user_id+"','no')";
        customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                try {
                    JSONObject parentJSonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJSonObject.getJSONArray("items");

                    if (jsonArray.length()==0)
                    {
                        MainActivity.navigationView.getMenu().findItem(R.id.menuSendFeedback).setTitle("Messages").setIcon(R.drawable.ic_inbox);
                    }
                    else
                    {
                        MainActivity.navigationView.getMenu().findItem(R.id.menuSendFeedback).setTitle("Messages ("+jsonArray.length()+")").setIcon(R.drawable.message_new);
                        messageDialog=new MessageDialog(getActivity(),"New Message","You have "+jsonArray.length()+ " message(s).");
                        messageDialog.messageInformation();
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

    @Override
    public void onStop()
    {
        super.onStop();
//        if (customStringRequest.requestQueue != null)
//        {
//            customStringRequest.requestQueue.cancelAll(customStringRequest.stringRequest);
//        }
    }
}
