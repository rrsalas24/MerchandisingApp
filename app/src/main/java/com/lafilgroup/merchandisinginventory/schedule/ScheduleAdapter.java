package com.lafilgroup.merchandisinginventory.schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CircleTransform;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RR SALAS on 3/20/2018.
 */

public class ScheduleAdapter extends BaseAdapter
{
    public static String viewType;
    SchedulesDTO schedulesDTO;
    Context context;
    private static LayoutInflater inflater=null;
    MessageDialog messageDialog;
    WaitingDialog waitingDialog;

    public ScheduleAdapter(SchedulesDTO schedulesDTO, Context context)
    {
        this.schedulesDTO = schedulesDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        if(schedulesDTO.getItems().size()>0)
        {
            return schedulesDTO.getItems().size();
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
        View v;
        Holder h = new Holder();
        if (viewType=="table")
        {
             v=inflater.inflate(R.layout.row_schedule_table,null);
        }
        else
        {
             v=inflater.inflate(R.layout.row_schedule,null);
        }

        h.lblDate=v.findViewById(R.id.lblDate);
        h.lblCustomer=v.findViewById(R.id.lblCustomer);
        h.lblCustomerAddress=v.findViewById(R.id.lblCustomerAddress);
        h.lblStatus=v.findViewById(R.id.lblStatus);
        h.lblTime=v.findViewById(R.id.lblTime);
        h.imgStatus=v.findViewById(R.id.imgStatus);
        h.lnrSchedule=v.findViewById(R.id.lnrSchedule);

        if (schedulesDTO.getItems().get(i).getStatus().equals("001"))
        {
            h.imgStatus.setImageResource(R.drawable.ic_success);
        }
        else if (schedulesDTO.getItems().get(i).getStatus().equals("003"))
        {
            h.imgStatus.setImageResource(R.drawable.ic_absent);
        }
        else
        {
            h.imgStatus.setImageResource(R.drawable.ic_close);
        }

        if (viewType=="table")
        {
            h.lblDate.setText(GlobalVar.toDateToString(schedulesDTO.getItems().get(i).getDate()));
            h.lblCustomer.setText(schedulesDTO.getItems().get(i).getCustomer_name());
            h.lblCustomerAddress.setText(schedulesDTO.getItems().get(i).getAddress());
            h.lblStatus.setText(schedulesDTO.getItems().get(i).getStatus_description());
            h.lblTime.setText(GlobalVar.toTime24to12(schedulesDTO.getItems().get(i).getTime_in()) + " - " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(i).getTime_out()));
        }
        else
        {
            h.lblDate.setText("Date: " + GlobalVar.toDateToString(schedulesDTO.getItems().get(i).getDate()));
            h.lblCustomer.setText(schedulesDTO.getItems().get(i).getCustomer_name());
            h.lblCustomerAddress.setText("Address: " + schedulesDTO.getItems().get(i).getAddress());
            h.lblStatus.setText("Status: " + schedulesDTO.getItems().get(i).getStatus_description());
            h.lblTime.setText("Time: " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(i).getTime_in()) + " - " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(i).getTime_out()));
     }

        h.lnrSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (schedulesDTO.getItems().get(i).getStatus_description().equals("Visited"))
                {
                    getDetails(schedulesDTO.getItems().get(i).getId());
                }
            }
        });
        return v;
    }

    public void getDetails(String schedule_id)
    {
        waitingDialog=new WaitingDialog(context);
        waitingDialog.openDialog();
        String url=context.getString(R.string.host) + context.getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_merchandiser_attendance_image where schedule_id = '" + schedule_id +  "'";
        String qry="call p_merchandiser_attendance_image('"+schedule_id+"')";
        final CustomStringRequest customStringRequest=new CustomStringRequest(context);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                try
                {
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");
                    JSONObject jsonObject =jsonArray.getJSONObject(0);
                    showDialog(jsonObject.getString("time_in"),jsonObject.getString("time_out"),jsonObject.getString("image_path"));
               }
                catch (JSONException e)
                {
                    messageDialog=new MessageDialog(context,"",e.toString());
                    messageDialog.messageError();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(context,"Error","No network connectivity");
                messageDialog.messageError();
            }
        });
    }

    public void showDialog(String time_in, String time_out, String image_path)
    {
        try
        {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_schedule_details,null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptView);

            ImageView imgPicture;
            TextView lblTimeIn, lblTimeOut,lblTotalHours;

            imgPicture = promptView.findViewById(R.id.imgPicture);
            lblTimeIn=promptView.findViewById(R.id.lblTimeIn);
            lblTimeOut=promptView.findViewById(R.id.lblTimeOut);
            lblTotalHours=promptView.findViewById(R.id.lblTotalHours);

            lblTimeIn.setText("Actual Time In: " + GlobalVar.toTime24to12(time_in));
            lblTimeOut.setText("Actual Time Out: " + GlobalVar.toTime24to12(time_out));
            Picasso.with(context).load(context.getString(R.string.host) + context.getString(R.string.image_url)+image_path).placeholder(R.mipmap.ic_launcher).into(imgPicture);
            lblTotalHours.setText(GlobalVar.totalDifferenceTime(time_in,time_out));
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
        }
        catch (Exception error)
        {
            messageDialog=new MessageDialog(context,"Error","Error occurred. (Details: Error viewing picture and actual time in/out) "+error.getMessage());
            messageDialog.messageError();
        }
    }
}
