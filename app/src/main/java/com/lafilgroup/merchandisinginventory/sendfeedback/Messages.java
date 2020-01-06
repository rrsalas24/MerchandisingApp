package com.lafilgroup.merchandisinginventory.sendfeedback;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementAdapter;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;

public class Messages extends Fragment
{
    public static boolean mSent;
    public static boolean mSeen;
    ListView lvwMessages;
    LinearLayout lnrNoNetwork;
    MessagesDTO messagesDTO;
    MessageAdapter messageAdapter;
    WaitingDialog waitingDialog;
    MessageDialog messageDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_messages,null);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        lvwMessages=view.findViewById(R.id.lvwMessages);
        lnrNoNetwork=view.findViewById(R.id.lnrNoNetwork);
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        getMessages();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                waitingDialog.closeDialog();
                swipeRefreshLayout.setRefreshing(false);
                getMessages();
            }
        });
    }

    @Override
    public void onResume()
    {
        if (mSent==true)
        {
            getMessages();
            mSent=false;
            messageDialog=new MessageDialog(getActivity(),"Success","Message sent.");
            messageDialog.messageSuccess();
        }

        if(mSeen==true)
        {
            mSeen=false;
            getMessages();
        }
        super.onResume();
    }

    public void getMessages()
    {
        waitingDialog=new WaitingDialog(getActivity());
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_merchandiser_message_header where merchandiser_id = '" + GlobalVar.user_id +  "' order by created_at desc";
        String qry = "call p_message_header('"+GlobalVar.user_id+"','')";
        CustomStringRequest customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                lnrNoNetwork.setVisibility(View.GONE);
                waitingDialog.closeDialog();
                jsonToGSon(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                lnrNoNetwork.setVisibility(View.VISIBLE);
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(getActivity(),"Error","No network connectivity.");
                messageDialog.messageError();
            }
        });
    }

    private void jsonToGSon(String response)
    {
        try
        {
            Gson gson =new Gson();
            messagesDTO=gson.fromJson(response,MessagesDTO.class);
            if(messagesDTO.getItems().size()==0)
            {
                messagesDTO.getItems().clear();
                messageDialog=new MessageDialog(getActivity(),"","No record found.");
                messageDialog.messageInformation();
            }
            messageAdapter =new MessageAdapter(messagesDTO,getActivity());
            lvwMessages.setAdapter(messageAdapter);
        }
        catch (Exception error)
        {
            messageDialog=new MessageDialog(getActivity(),"Error","Error occurred. (Details: Error during messages page DTO) "+error.getMessage());
            messageDialog.messageError();
        }
    }
}
