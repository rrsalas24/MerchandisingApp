package com.lafilgroup.merchandisinginventory.logs;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementsDTO;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.home.AnnouncementRecyclerAdapter;

public class LogsItem extends AppCompatActivity
{
    static String transaction_number;

    TextView lblNoPhysicalCount,lblNoDeliveryCount, lblNoReturnCount;

    RecyclerView rvwPhysicalCount, rvwDeliveryCount,rvwReturnCount;
    RecyclerView.LayoutManager layoutManager;

    LinearLayout lnrPhysicalColumn, lnrDeliveryColumn, lnrReturnColumn;

    LogsItemDTO logsItemDTO;
    LogsItemAdapter logsItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs_item);
        this.setTitle("Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        castObj();
        getPhysical();
        getDelivery();
        getReturn();
    }

    public void castObj()
    {
        lblNoPhysicalCount=findViewById(R.id.lblNoPhysicalCount);
        lblNoDeliveryCount=findViewById(R.id.lblNoDeliveryCount);
        lblNoReturnCount=findViewById(R.id.lblNoReturnCount);

        rvwPhysicalCount=findViewById(R.id.rvwPhysicalCount);
        rvwDeliveryCount=findViewById(R.id.rvwDeliveryCount);
        rvwReturnCount=findViewById(R.id.rvwReturnCount);

        lnrPhysicalColumn=findViewById(R.id.lnrPhysicalColumn);
        lnrDeliveryColumn=findViewById(R.id.lnrDeliveryColumn);
        lnrReturnColumn=findViewById(R.id.lnrReturnColumn);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    public void getPhysical()
    {
        final WaitingDialog waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_inventory_transaction_items where transaction_number= '" + transaction_number +"' and inventory_type in ('1','2','3')";
        String qry="call p_inventory_items('"+transaction_number+"',\"1,2,3\")";
        CustomStringRequest customStringRequest=new CustomStringRequest(LogsItem.this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                jsonToGSonPhysical(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                Toast.makeText(LogsItem.this, "No network connectivity", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSonPhysical(String response)
    {
        Gson gson =new Gson();
        logsItemDTO=gson.fromJson(response, LogsItemDTO.class);
        if(logsItemDTO.getItems().size()==0)
        {
            logsItemDTO.getItems().clear();
            lblNoPhysicalCount.setVisibility(View.VISIBLE);
        }
        else
        {
            lblNoPhysicalCount.setVisibility(View.GONE);
        }

        logsItemAdapter=new LogsItemAdapter(logsItemDTO,LogsItem.this,"1");
        layoutManager= new LinearLayoutManager(LogsItem.this);
        rvwPhysicalCount.setLayoutManager(layoutManager);
        rvwPhysicalCount.setAdapter(logsItemAdapter);
    }

    public void getDelivery()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_inventory_transaction_items where transaction_number= '" + transaction_number +"' and inventory_type ='4'";
        String qry="call p_inventory_items('"+transaction_number+"','4')";
        CustomStringRequest customStringRequest=new CustomStringRequest(LogsItem.this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                jsonToGSonDelivery(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSonDelivery(String response)
    {
        Gson gson =new Gson();
        logsItemDTO=gson.fromJson(response, LogsItemDTO.class);
        if(logsItemDTO.getItems().size()==0)
        {
            logsItemDTO.getItems().clear();
            lblNoDeliveryCount.setVisibility(View.VISIBLE);
        }
        else
        {
            lblNoDeliveryCount.setVisibility(View.GONE);
        }

        logsItemAdapter=new LogsItemAdapter(logsItemDTO,LogsItem.this,"4");
        layoutManager= new LinearLayoutManager(LogsItem.this);
        rvwDeliveryCount.setLayoutManager(layoutManager);
        rvwDeliveryCount.setAdapter(logsItemAdapter);
    }

    public void getReturn()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_inventory_transaction_items where transaction_number= '" + transaction_number +"' and inventory_type ='5'";
        String qry="call p_inventory_items('"+transaction_number+"','5')";
        CustomStringRequest customStringRequest=new CustomStringRequest(LogsItem.this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                jsonToGSonReturn(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSonReturn(String response)
    {
        Gson gson =new Gson();
        logsItemDTO=gson.fromJson(response, LogsItemDTO.class);
        if(logsItemDTO.getItems().size()==0)
        {
            logsItemDTO.getItems().clear();
            lblNoReturnCount.setVisibility(View.VISIBLE);
        }
        else
        {
            lblNoReturnCount.setVisibility(View.GONE);
        }

        logsItemAdapter=new LogsItemAdapter(logsItemDTO,LogsItem.this,"5");
        layoutManager= new LinearLayoutManager(LogsItem.this);
        rvwReturnCount.setLayoutManager(layoutManager);
        rvwReturnCount.setAdapter(logsItemAdapter);
    }
}
