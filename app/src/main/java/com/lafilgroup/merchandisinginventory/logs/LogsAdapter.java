package com.lafilgroup.merchandisinginventory.logs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.sendfeedback.MessageItems;

/**
 * Created by RR SALAS on 6/19/2018.
 */

public class LogsAdapter extends BaseAdapter
{
    public static String viewType;
    LogsDTO logsDTO;
    Context context;
    private static LayoutInflater inflater=null;

    public LogsAdapter(LogsDTO logsDTO, Context context)
    {
        this.logsDTO = logsDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(logsDTO.getItems().size()>0)
        {
            return logsDTO.getItems().size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    public class  Holder
    {
        TextView lblCustomerName, lblTransactionNumber, lblRemarks, lblDate;
        Button btnView;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        Holder h=new Holder();
        View v;
        if (viewType=="table")
        {
            v=inflater.inflate(R.layout.row_logs_table,null);
        }
        else
        {
            v=inflater.inflate(R.layout.row_logs_list,null);
        }

        h.lblCustomerName=v.findViewById(R.id.lblCustomerName);
        h.lblTransactionNumber=v.findViewById(R.id.lblTransactionNumber);
        h.lblRemarks=v.findViewById(R.id.lblRemarks);
        h.lblDate=v.findViewById(R.id.lblDate);
        h.btnView=v.findViewById(R.id.btnView);

        if (viewType=="table")
        {
            h.lblCustomerName.setText(logsDTO.getItems().get(i).getCustomer_name());
            h.lblTransactionNumber.setText(logsDTO.getItems().get(i).getTransaction_number());
            h.lblRemarks.setText(logsDTO.getItems().get(i).getRemarks());
            h.lblDate.setText(GlobalVar.toDatetimeToString(logsDTO.getItems().get(i).getCreated_at()));
        }
        else
        {
            h.lblCustomerName.setText(logsDTO.getItems().get(i).getCustomer_name());
            h.lblTransactionNumber.setText("Transaction Number: "+logsDTO.getItems().get(i).getTransaction_number());
            h.lblRemarks.setText("Remarks: "+logsDTO.getItems().get(i).getRemarks());
            h.lblDate.setText("Date: "+GlobalVar.toDatetimeToString(logsDTO.getItems().get(i).getCreated_at()));
        }

        h.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LogsItem.transaction_number=logsDTO.getItems().get(i).getTransaction_number();
                context.startActivity(new Intent(context, LogsItem.class));
            }
        });
        return v;
    }
}
