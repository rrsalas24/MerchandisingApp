package com.lafilgroup.merchandisinginventory.logs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.stockinventory.AddPhysicalCount;
import com.lafilgroup.merchandisinginventory.stockinventory.TransactionItemAdapter;

import java.util.Calendar;

/**
 * Created by RR SALAS on 6/20/2018.
 */

public class LogsItemAdapter extends RecyclerView.Adapter<LogsItemAdapter.MyViewHolder>
{
    String countType;
    LogsItemDTO logsItemDTO;
    Context context;
    private static LayoutInflater inflater=null;

    LogsExpirationDTO logsExpirationDTO;
    LogsExpirationAdapter logsExpirationAdapter;

    MessageDialog messageDialog;

    public LogsItemAdapter(LogsItemDTO logsItemDTO, Context context, String countType)
    {
        this.logsItemDTO = logsItemDTO;
        this.context = context;
        this.countType = countType;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
            itemView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_logs_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (countType.equals("4"))
        {
            holder.lblLocation.setVisibility(View.GONE);
            holder.lblDeliveryDate.setVisibility(View.VISIBLE);
            holder.lblReturnDate.setVisibility(View.GONE);
            holder.lblRemarks.setVisibility(View.VISIBLE);
            holder.lblDeliveryDate.setText("Delivery Date: "+GlobalVar.toDateToString(logsItemDTO.getItems().get(position).getDelivery_date()));
            holder.lblReturnDate.setText("Return Date: "+ "");
            holder.btnView.setVisibility(View.GONE);
        }
        else if (countType.equals("5"))
        {
            holder.lblLocation.setVisibility(View.GONE);
            holder.lblDeliveryDate.setVisibility(View.GONE);
            holder.lblReturnDate.setVisibility(View.VISIBLE);
            holder.lblRemarks.setVisibility(View.VISIBLE);
            holder.lblDeliveryDate.setText("Delivery Date: "+"");
            holder.lblReturnDate.setText("Return Date: "+ GlobalVar.toDateToString(logsItemDTO.getItems().get(position).getReturn_date()));
            holder.btnView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.lblLocation.setVisibility(View.VISIBLE);
            holder.lblDeliveryDate.setVisibility(View.GONE);
            holder.lblReturnDate.setVisibility(View.GONE);
            holder.lblRemarks.setVisibility(View.GONE);
            holder.btnView.setVisibility(View.VISIBLE);
        }

        holder.lblDescription.setText(logsItemDTO.getItems().get(position).getMaterial_description());
        holder.lblUOM.setText("UOM: " + logsItemDTO.getItems().get(position).getEntry_uom());
        holder.lblLocation.setText("Location: "+logsItemDTO.getItems().get(position).getInventory_type_description());
        holder.lblQTYCounted.setText("QTY: "+logsItemDTO.getItems().get(position).getEntry_qty()+"");
        holder.lblRemarks.setText("Remarks: "+logsItemDTO.getItems().get(position).getRemarks());

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getExpiration(logsItemDTO.getItems().get(position).getTransaction_number(),logsItemDTO.getItems().get(position).getMaterial_code()
                        ,logsItemDTO.getItems().get(position).getEntry_uom(),logsItemDTO.getItems().get(position).getInventory_type());
            }
        });
    }

    public void getExpiration(String transaction_number, String material_code, String uom, String inventory_type)
    {
        final WaitingDialog waitingDialog=new WaitingDialog(context);
        waitingDialog.openDialog();
        String url=context.getString(R.string.host) + context.getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from inventory_transaction_expiration where transaction_number = '" + transaction_number +"' and material_code = '" + material_code +"' and uom='"+ uom+"' and inventory_type='"+inventory_type+"'" ;
        String qry="call p_inventory_expiration('"+transaction_number+"','"+material_code+"','"+uom+"','"+inventory_type+"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(context);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                jsonToGSon(response);
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

    private void jsonToGSon(String response)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_view_expiration,null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        ListView lvwExpiration;
        lvwExpiration = promptView.findViewById(R.id.lvwExpiration);

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

        Gson gson =new Gson();
        logsExpirationDTO=gson.fromJson(response, LogsExpirationDTO.class);
        if(logsExpirationDTO.getItems().size()==0)
        {
            logsExpirationDTO.getItems().clear();
            Toast.makeText(context, "No record found.", Toast.LENGTH_SHORT).show();
        }
        logsExpirationAdapter=new LogsExpirationAdapter(logsExpirationDTO,context);
        lvwExpiration.setAdapter(logsExpirationAdapter);
    }

    @Override
    public int getItemCount() {
        if (logsItemDTO.getItems().size()>0)
        {
            return logsItemDTO.getItems().size();
        }
        return  0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView lblDescription, lblUOM, lblLocation, lblQTYCounted, lblDeliveryDate, lblReturnDate, lblRemarks;
        Button btnView;
        public MyViewHolder(View view)
        {
            super(view);
            lblDescription = view.findViewById(R.id.lblDescription);
            lblUOM = view.findViewById(R.id.lblUOM);
            lblLocation = view.findViewById(R.id.lblLocation);
            lblQTYCounted = view.findViewById(R.id.lblQTYCounted);
            lblDeliveryDate=view.findViewById(R.id.lblDeliveryDate);
            lblReturnDate=view.findViewById(R.id.lblReturnDate);
            lblRemarks=view.findViewById(R.id.lblRemarks);
            btnView=view.findViewById(R.id.btnView);
        }
    }
}

