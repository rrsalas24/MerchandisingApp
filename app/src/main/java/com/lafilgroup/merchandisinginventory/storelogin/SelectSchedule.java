package com.lafilgroup.merchandisinginventory.storelogin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.Login;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CheckGPSDevice;
import com.lafilgroup.merchandisinginventory.config.CircleDisplay;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.schedule.SchedulesDTO;

public class SelectSchedule extends AppCompatActivity
{
    SchedulesDTO schedulesDTO;
    SelectScheduleAdapter selectScheduleAdapter;
    ListView lvwSchedule;
    TextView lblSelect, lblPercent;
    Button btnLoginOutdoor;
    CircleDisplay cd;
    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_schedule);
        this.setTitle("Select Schedule");
        lvwSchedule=findViewById(R.id.lvwSchedule);
        lblSelect=findViewById(R.id.lblSelect);
        lblPercent=findViewById(R.id.lblPercent);
        btnLoginOutdoor=findViewById(R.id.btnLoginOutdoor);
        cd =  findViewById(R.id.circleDisplay);
        checkGpsStatus();

        btnLoginOutdoor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                GlobalVar.login_type="Outdoor";
                Login.sharedLoginPref(GlobalVar.user_id,GlobalVar.login_type, GlobalVar.full_name,GlobalVar.api_token,"","","","","");
                //Toast.makeText(SelectSchedule.this, "Login successfully.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SelectSchedule.this, MainActivity.class));
                finish();
            }
        });
        getSchedule();
    }

    @Override
    protected void onResume()
    {
        checkGpsStatus();
        super.onResume();
    }

    public void checkGpsStatus()
    {
        CheckGPSDevice checkGPSDevice=new CheckGPSDevice(this);
        checkGPSDevice.CheckGpsStatus();
    }

    public void percentageVisited(Float percent)
    {
        cd.setAnimDuration(1500);
        cd.setValueWidthPercent(24f);
        cd.setTextSize(46f);
        cd.setColor(Color.BLUE);
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);
        cd.setFormatDigits(0);
        cd.setTouchEnabled(false);
        cd.setUnit("%");
        cd.setStepSize(0.5f);
        cd.showValue(percent, 100f, true);
    }

    public void getSchedule()
    {
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//        String qry = "Select * from vw_schedules where merchandiser_id like '" + GlobalVar.user_id +
//                "'  and (date between '" + GlobalVar.getCurrentDate(0) +
//                "' and '" + GlobalVar.getCurrentDate(0) + "') order by date desc";

        String qry = "call p_schedules('"+GlobalVar.user_id+"','"+GlobalVar.getCurrentDate(0)+"','"+GlobalVar.getCurrentDate(0)+"')";

        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                jsonToGSonSchedule(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                Toast.makeText(SelectSchedule.this, "No network connectivity.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSonSchedule(String response)
    {
        try
        {
            double percent=0;
            Gson gson =new Gson();
            schedulesDTO=gson.fromJson(response, SchedulesDTO.class);
            if(schedulesDTO.getItems().size()==0)
            {
                schedulesDTO.getItems().clear();
                btnLoginOutdoor.setVisibility(View.VISIBLE);
                lblSelect.setText("No pending schedule.");
                lblPercent.setVisibility(View.GONE);
                cd.setVisibility(View.GONE);
            }
            else
            {
                btnLoginOutdoor.setVisibility(View.GONE);
                lblSelect.setText("Pick Schedule");
                lblPercent.setVisibility(View.VISIBLE);
                cd.setVisibility(View.VISIBLE);
            }

            selectScheduleAdapter=new SelectScheduleAdapter(schedulesDTO,this);
            percent=Double.parseDouble(selectScheduleAdapter.visited()+"")/Double.parseDouble(selectScheduleAdapter.getCount()+"") *100;

            if (percent==100)
            {
                btnLoginOutdoor.setVisibility(View.VISIBLE);
            }
            lblPercent.setText(Math.round(percent)+" PERCENT COMPLETED TODAY");
            percentageVisited(Float.parseFloat(percent+""));
            lvwSchedule.setAdapter(selectScheduleAdapter);
        }
        catch (Exception error)
        {
        messageDialog=new MessageDialog(this,"Error","Error occurred. (Details: Error during select schedule DTO). " + error.getMessage());
        messageDialog.messageError();
        }
    }
}
