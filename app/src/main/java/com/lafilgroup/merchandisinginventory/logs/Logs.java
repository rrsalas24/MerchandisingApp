package com.lafilgroup.merchandisinginventory.logs;


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
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementAdapter;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementsDTO;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;

/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class Logs extends Fragment implements RadioGroup.OnCheckedChangeListener
{
    ListView lvwLogs;
    LinearLayout lnrNoNetwork,lnrColumn;
    RadioGroup rgOption;
    RadioButton rbListView,rbTableView;
    LogsDTO logsDTO;
    LogsAdapter logsAdapter;
    MessageDialog messageDialog;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logs,null);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvwLogs=view.findViewById(R.id.lvwLogs);
        lnrColumn=view.findViewById(R.id.lnrColumn);
        lnrNoNetwork=view.findViewById(R.id.lnrNoNetwork);

        rgOption=view.findViewById(R.id.rgOption);
        rbListView=view.findViewById(R.id.rbListView);
        rbTableView=view.findViewById(R.id.rbTableView);
        rgOption.setOnCheckedChangeListener(this);
        getLogs("list");
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
                    lvwLogs.setAdapter(null);
                    lvwLogs.setDivider(null);
                    getLogs("list");
                }
                else if (i==R.id.rbTableView)
                {
                    lvwLogs.setAdapter(null);
                    lnrColumn.setVisibility(View.VISIBLE);
                    ColorDrawable colorDrawable=new ColorDrawable(this.getResources().getColor(R.color.divider));
                    lvwLogs.setDivider(colorDrawable);
                    lvwLogs.setDividerHeight(1);
                    getLogs("table");
                }
                break;
        }
    }

    public void getLogs(final String viewType)
    {
        final WaitingDialog waitingDialog=new WaitingDialog(getActivity());
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_inventory_transaction_header where merchandiser_id = '" + GlobalVar.user_id +"' order by created_at desc";
        String qry = "call p_inventory_header('"+GlobalVar.user_id+"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                LogsAdapter.viewType=viewType;
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
        Gson gson =new Gson();
        logsDTO=gson.fromJson(response, LogsDTO.class);
        if(logsDTO.getItems().size()==0)
        {
            logsDTO.getItems().clear();
            messageDialog=new MessageDialog(getActivity(),"","No record found.");
            messageDialog.messageInformation();
        }
        logsAdapter=new LogsAdapter(logsDTO,getActivity());
        lvwLogs.setAdapter(logsAdapter);
    }
}
