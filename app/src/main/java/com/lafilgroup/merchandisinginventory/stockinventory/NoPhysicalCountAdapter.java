package com.lafilgroup.merchandisinginventory.stockinventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.MaterialBalanceAdapter;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;

import java.util.List;

/**
 * Created by RR SALAS on 9/14/2018.
 */

public class NoPhysicalCountAdapter extends BaseAdapter
{
    Context context;
    private final List<MaterialSQL.MaterialDTO> items;
    private static LayoutInflater inflater=null;

    public NoPhysicalCountAdapter(Context context, List<MaterialSQL.MaterialDTO> items) {
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
        TextView lblMaterialCode, lblMaterialDescription;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Holder h=new Holder();
        View v;
        v=inflater.inflate(R.layout.row_no_physical_count,null);
        h.lblMaterialCode=v.findViewById(R.id.lblMaterialCode);
        h.lblMaterialDescription=v.findViewById(R.id.lblMaterialDescription);

        h.lblMaterialCode.setText(items.get(i).getMaterial_code());
        h.lblMaterialDescription.setText(items.get(i).getMaterial_description());

        return v;
    }
}
