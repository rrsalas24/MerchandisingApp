package com.lafilgroup.merchandisinginventory.announcement;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.schedule.ScheduleAdapter;
import com.lafilgroup.merchandisinginventory.schedule.SchedulesDTO;

/**
 * Created by RR SALAS on 3/20/2018.
 */

public class Announcement extends Fragment implements RadioGroup.OnCheckedChangeListener
{
    ListView lvwAnnouncements;
    AnnouncementsDTO announcementsDTO;
    AnnouncementAdapter announcementAdapter;
    ProgressDialog dialog;
    LinearLayout lnrNoNetwork,lnrColumn;
    RadioGroup rgOption;
    RadioButton rbListView,rbTableView;
    MessageDialog messageDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_announcement,null);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        lvwAnnouncements=view.findViewById(R.id.lvwAnnouncements);
        lnrColumn=view.findViewById(R.id.lnrColumn);
        lnrNoNetwork=view.findViewById(R.id.lnrNoNetwork);

        rgOption=view.findViewById(R.id.rgOption);
        rbListView=view.findViewById(R.id.rbListView);
        rbTableView=view.findViewById(R.id.rbTableView);

        rgOption.setOnCheckedChangeListener(this);
        getAnnouncement("list");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i)
    {
        switch (radioGroup.getId())
        {
            case R.id.rgOption:

                if (i==R.id.rbListView)
                {
                    lnrColumn.setVisibility(View.GONE);
                    lvwAnnouncements.setAdapter(null);
                    lvwAnnouncements.setDivider(null);
                    getAnnouncement("list");
                }
                else if (i==R.id.rbTableView)
                {
                    lvwAnnouncements.setAdapter(null);
                    lnrColumn.setVisibility(View.VISIBLE);
                    ColorDrawable colorDrawable=new ColorDrawable(this.getResources().getColor(R.color.divider));
                    lvwAnnouncements.setDivider(colorDrawable);
                    lvwAnnouncements.setDividerHeight(1);
                    getAnnouncement("table");
                }
                break;
        }
    }

    public void getAnnouncement(final String viewType)
    {
        final WaitingDialog waitingDialog =new WaitingDialog(getActivity());
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//      String qry = "Select * from vw_announcements where date(created_at) between '" + GlobalVar.getCurrentDate(-30) +
//                "' and '" + GlobalVar.getCurrentDate(0) + "' order by created_at desc";
        String qry = "call p_announcements('"+GlobalVar.getCurrentDate(-30)+"','"+GlobalVar.getCurrentDate(0)+"')";

        CustomStringRequest customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                AnnouncementAdapter.viewType=viewType;
                lnrNoNetwork.setVisibility(View.GONE);
                jsonToGSon(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.VISIBLE);
                messageDialog=new MessageDialog(getActivity(),"Error","No network connectivity.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSon(String response)
    {
        try
        {
            Gson gson =new Gson();
            announcementsDTO=gson.fromJson(response, AnnouncementsDTO.class);
            if(announcementsDTO.getItems().size()==0)
            {
                announcementsDTO.getItems().clear();
                messageDialog=new MessageDialog(getActivity(),"","No record found.");
                messageDialog.messageInformation();
            }
            announcementAdapter=new AnnouncementAdapter(announcementsDTO,getActivity());
            lvwAnnouncements.setAdapter(announcementAdapter);
        }
        catch (Exception error)
        {
            messageDialog=new MessageDialog(getActivity(),"Error","Error occurred. (Details: Error during announcement page DTO) "+error.getMessage());
            messageDialog.messageError();
        }
    }
}
