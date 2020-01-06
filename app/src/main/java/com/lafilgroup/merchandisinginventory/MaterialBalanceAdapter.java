package com.lafilgroup.merchandisinginventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialBalanceSQL;

import java.util.List;

/**
 * Created by RR SALAS on 7/19/2018.
 */

public class MaterialBalanceAdapter extends BaseAdapter
{
    Context context;
    private final List<MaterialBalanceSQL.MaterialBalanceDTO> items;
    private static LayoutInflater inflater=null;

    public MaterialBalanceAdapter(Context context, List<MaterialBalanceSQL.MaterialBalanceDTO> items) {
        this.context = context;
        this.items = items;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
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
        TextView lblMaterial, lblUOM, lblQTY;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Holder h=new Holder();
        View v;
        v=inflater.inflate(R.layout.row_material_balance,null);
        h.lblMaterial=v.findViewById(R.id.lblMaterial);
        h.lblUOM=v.findViewById(R.id.lblUOM);
        h.lblQTY=v.findViewById(R.id.lblQTY);

        h.lblMaterial.setText(items.get(i).getMaterial_description());
        h.lblUOM.setText(items.get(i).getBase_uom());
        h.lblQTY.setText(items.get(i).getEnding_balance()+"");

        return v;
    }
}
