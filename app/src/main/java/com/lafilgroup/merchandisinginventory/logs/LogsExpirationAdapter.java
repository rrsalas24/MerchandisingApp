package com.lafilgroup.merchandisinginventory.logs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;

/**
 * Created by RR SALAS on 6/20/2018.
 */

public class LogsExpirationAdapter extends BaseAdapter
{
    LogsExpirationDTO logsExpirationDTO;
    Context context;
    private static LayoutInflater inflater=null;

    public LogsExpirationAdapter(LogsExpirationDTO logsExpirationDTO, Context context) {
        this.logsExpirationDTO = logsExpirationDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(logsExpirationDTO.getItems().size()>0)
        {
            return logsExpirationDTO.getItems().size();
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
        TextView lblExpirationDate, lblExpirationQTY;
        Button btnRemove;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Holder h=new Holder();
        View v;
        v=inflater.inflate(R.layout.row_expiration_details,null);

        h.lblExpirationDate=v.findViewById(R.id.lblExpirationDate);
        h.lblExpirationQTY=v.findViewById(R.id.lblExpirationQTY);
        h.btnRemove=v.findViewById(R.id.btnRemove);

        h.btnRemove.setVisibility(View.GONE);
        h.lblExpirationDate.setText(GlobalVar.toDateToString(logsExpirationDTO.getItems().get(i).getExpiration_date().toString()));
        h.lblExpirationQTY.setText(logsExpirationDTO.getItems().get(i).getEntry_qty().toString());
        return v;
    }
}
