package com.lafilgroup.merchandisinginventory.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.schedule.SchedulesDTO;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by RR SALAS on 4/5/2018.
 */


public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.MyViewHolder>
{
    SchedulesDTO schedulesDTO;
    Context context;
    private static LayoutInflater inflater=null;
    MessageDialog messageDialog;
    WaitingDialog waitingDialog;

    public ScheduleRecyclerAdapter(SchedulesDTO schedulesDTO, Context context)
    {
        this.schedulesDTO = schedulesDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_schedule, parent, false);
        return new ScheduleRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        if (schedulesDTO.getItems().get(position).getStatus().equals("001"))
        {
            holder.imgStatus.setImageResource(R.drawable.ic_success);
        }
        else if (schedulesDTO.getItems().get(position).getStatus().equals("003"))
        {
            holder.imgStatus.setImageResource(R.drawable.ic_absent);
        }
        else
        {
            holder.imgStatus.setImageResource(R.drawable.ic_close);
        }

        holder.lblCustomer.setText(schedulesDTO.getItems().get(position).getCustomer_name());
        holder.lblCustomerAddress.setText("Address: "+schedulesDTO.getItems().get(position).getAddress());
        holder.lblStatus.setText("Status: " + schedulesDTO.getItems().get(position).getStatus_description());
        holder.lblDate.setVisibility(View.GONE);
        holder.lblTime.setText("Time: " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(position).getTime_in()) + " - " + GlobalVar.toTime24to12(schedulesDTO.getItems().get(position).getTime_out()));

        holder.lnrSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (schedulesDTO.getItems().get(position).getStatus_description().equals("Visited"))
                {
                    getDetails(schedulesDTO.getItems().get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedulesDTO.getItems().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView lblCustomer, lblCustomerAddress, lblStatus,lblTime,lblDate;
        ImageView imgStatus;
        LinearLayout lnrSchedule;
        public MyViewHolder(View view)
        {
            super(view);
            lblDate=view.findViewById(R.id.lblDate);
            lblCustomer = view.findViewById(R.id.lblCustomer);
            lblCustomerAddress =  view.findViewById(R.id.lblCustomerAddress);
            lblStatus = view.findViewById(R.id.lblStatus);
            lblTime = view.findViewById(R.id.lblTime);
            imgStatus=view.findViewById(R.id.imgStatus);
            lnrSchedule=view.findViewById(R.id.lnrSchedule);
        }
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
}
