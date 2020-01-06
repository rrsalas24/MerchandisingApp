package com.lafilgroup.merchandisinginventory.offline;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.OfflineInventoryHeaderSQL;
import com.lafilgroup.merchandisinginventory.stockinventory.ConfirmSubmit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RR SALAS on 3/3/2019.
 */

public class OfflineSaveAdapter extends BaseAdapter
{
    Context context;
    private final List<OfflineInventoryHeaderSQL.OfflineInventoryHeaderDTO> items;
    private static LayoutInflater inflater=null;
    MessageDialog messageDialog;
    WaitingDialog waitingDialog;

    public OfflineSaveAdapter(Context context, List<OfflineInventoryHeaderSQL.OfflineInventoryHeaderDTO> items)
    {
        this.context = context;
        this.items = items;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
      return items.size();
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
        TextView lblCustomerName, lblCustomerCode, lblScheduleId, lblDateTime;
        Button btnSubmit;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        Holder h =new Holder();
        View v;
        v=inflater.inflate(R.layout.row_offline_inventory,null);

        h.btnSubmit=v.findViewById(R.id.btnSubmit);
        h.lblCustomerName=v.findViewById(R.id.lblCustomerName);
        h.lblCustomerCode=v.findViewById(R.id.lblCustomerCode);
        h.lblScheduleId=v.findViewById(R.id.lblScheduleId);
        h.lblDateTime=v.findViewById(R.id.lblDateTime);

        h.lblCustomerName.setText(items.get(i).getCustomer_name());
        h.lblCustomerCode.setText("Customer Code: " + items.get(i).getCustomer_code());
        h.lblScheduleId.setText("Schedule ID: "+items.get(i).getSchedule_id());
        h.lblDateTime.setText("Date/Time: "+items.get(i).getDate_time());

        h.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getTransactionNumber(GlobalVar.user_id,items.get(i).getCustomer_code(),items.get(i).getCustomer_id(),items.get(i).getSchedule_id(),items.get(i).getDate_time());
            }
        });
        return v;
    }

    public void getTransactionNumber(final String merchandiser_id, final String customer_code, final String customer_id, final String schedule_id, final String date_time) {
        waitingDialog= new WaitingDialog(context);
        waitingDialog.openDialog();
        String url = context.getString(R.string.host) + context.getString(R.string.transaction_number) + GlobalVar.api_token;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                waitingDialog.closeDialog();
                OfflineConfirmSubmit.transaction_number = response.toString();
                OfflineConfirmSubmit.customer_code = customer_code;
                OfflineConfirmSubmit.customer_id = customer_id;
                OfflineConfirmSubmit.schedule_id = schedule_id;
                OfflineConfirmSubmit.date_time = date_time;
                context.startActivity(new Intent(context, OfflineConfirmSubmit.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog = new MessageDialog(context, "Error", "Network is unreachable.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("merchandiser_id", merchandiser_id);
                params.put("customer_code", customer_code);
                params.put("customer_id", customer_id);
                return params;
            }
        };
        AppContoller.getmInstance(context).addRequestQue(stringRequest);
    }
}
