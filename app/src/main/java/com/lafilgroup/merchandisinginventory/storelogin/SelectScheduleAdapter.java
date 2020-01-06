package com.lafilgroup.merchandisinginventory.storelogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.GPSLocator;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.schedule.SchedulesDTO;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RR SALAS on 4/6/2018.
 */

public class SelectScheduleAdapter extends BaseAdapter
{
    SchedulesDTO schedulesDTO;
    Context context;
    private static LayoutInflater inflater=null;
    MessageDialog messageDialog;

    public SelectScheduleAdapter(SchedulesDTO schedulesDTO, Context context) {
        this.schedulesDTO = schedulesDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(schedulesDTO.getItems().size()>0)
        {
            return schedulesDTO.getItems().size();

        }
        return 0;
    }


    public Integer visited()
    {
        if(schedulesDTO.getItems().size()>0)
        {
            int count =0;
            int visited=0;
            while (count< schedulesDTO.getItems().size())
            {
                if (schedulesDTO.getItems().get(count).getStatus_description().equals("Visited"))
                {
                    visited++;
                }
                count++;
            }
            return visited;
        }
        return 0;
    }


    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class  Holder
    {
        TextView lblDate, lblCustomer, lblCustomerAddress, lblStatus,lblTime;
        ImageView imgStatus;
        LinearLayout lnrSchedule;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        final Holder h = new Holder();
        View v=inflater.inflate(R.layout.row_schedule,null);
        h.lblDate=v.findViewById(R.id.lblDate);
        h.lblCustomer=v.findViewById(R.id.lblCustomer);
        h.lblCustomerAddress=v.findViewById(R.id.lblCustomerAddress);
        h.lblStatus=v.findViewById(R.id.lblStatus);
        h.lblTime=v.findViewById(R.id.lblTime);
        h.imgStatus=v.findViewById(R.id.imgStatus);
        h.lnrSchedule=v.findViewById(R.id.lnrSchedule);

        if (schedulesDTO.getItems().get(i).getStatus_description().equals("Visited"))
        {
            h.imgStatus.setImageResource(R.drawable.ic_success);
        }
        else
        {
            h.imgStatus.setImageResource(R.drawable.ic_close);
        }
        h.lblDate.setVisibility(View.GONE);
        h.lblStatus.setVisibility(View.GONE);

        h.lblCustomer.setText(schedulesDTO.getItems().get(i).getCustomer_name());
        h.lblCustomerAddress.setText("Address: "+schedulesDTO.getItems().get(i).getAddress());
        h.lblTime.setText("Time: " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(i).getTime_in()) + " - " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(i).getTime_out()));

        h.lnrSchedule.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                h.lnrSchedule.setEnabled(false);
                GPSLocator gpsLocator;
                if (schedulesDTO.getItems().get(i).getStatus().equals("001"))
                {
                    messageDialog =new MessageDialog(context,"","Store already visited.");
                    messageDialog.messageError();
                    h.lnrSchedule.setEnabled(true);
                }
                else
                {
                    if (GlobalVar.compareTime(schedulesDTO.getItems().get(i).getTime_out(),GlobalVar.getCurrentTime())==true)
                    {
                        h.lnrSchedule.setEnabled(true);
                        gpsLocator=new GPSLocator(context);
                        gpsLocator.getCoordinates(new GPSLocator.dataCallback() {
                            @Override
                            public void location(String lat, String lng)
                            {
                                h.lnrSchedule.setEnabled(true);
                                checkLocation(schedulesDTO.getItems().get(i).getCustomer_code(),lat,lng,schedulesDTO.getItems().get(i).getCustomer_name(),schedulesDTO.getItems().get(i).getId(),schedulesDTO.getItems().get(i).getCustomer_id());
                            }
                        });
                    }
                    else
                    {
                        h.lnrSchedule.setEnabled(true);
                        messageDialog =new MessageDialog(context,"","Unable to login. Schedule time already finished.");
                        messageDialog.messageError();
                    }
                }
            }
        });
        return v;
    }

//    public void checkLocation(final String customer_code, final String lat, final String lng, final String customer_name, final String schedule_id, final String customer_id)
//    {
//        GlobalVar.customer_name=customer_name;
//        GlobalVar.customer_code=customer_code;
//        GlobalVar.schedule_id=schedule_id;
//        GlobalVar.customer_id=customer_id;
//        context.startActivity(new Intent(context, LoginTakePicture.class));
//        ((Activity)context).finish();
//    }

    public void checkLocation(final String customer_code, final String lat, final String lng, final String customer_name, final String schedule_id, final String customer_id)
    {
        final WaitingDialog waitingDialog=new WaitingDialog(context);
        waitingDialog.openDialog();
        String url=context.getString(R.string.host) + context.getString(R.string.location) + GlobalVar.api_token;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                waitingDialog.closeDialog();
                if (response.toString().equals("success"))
                {
                    GlobalVar.customer_name=customer_name;
                    GlobalVar.customer_code=customer_code;
                    GlobalVar.schedule_id=schedule_id;
                    GlobalVar.customer_id=customer_id;
                    context.startActivity(new Intent(context, LoginTakePicture.class));
                    ((Activity)context).finish();
                }
                else if (response.toString().equals("failed"))
                {
                  messageDialog =new MessageDialog(context,"","You are not in " + customer_name +" area.");
                  messageDialog.messageError();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                //messageDialog =new MessageDialog(context,"Error","No network connectivity.");
                messageDialog =new MessageDialog(context,"Error",error.getMessage());
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
        AppContoller.getmInstance(context).addRequestQue(stringRequest);
    }
}
