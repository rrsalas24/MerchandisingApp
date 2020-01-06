package com.lafilgroup.merchandisinginventory.stockinventory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RR SALAS on 5/9/2018.
 */

public class SearchItemAdapter extends BaseAdapter
{
    public static String viewType;
    Context context;
    private final List <MaterialSQL.MaterialDTO> items;
    private static LayoutInflater inflater=null;


    public SearchItemAdapter(Context context, List<MaterialSQL.MaterialDTO> items)
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
        TextView lblBarcode, lblMaterialCode, lblDescription;
        LinearLayout lnrItems;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        Holder h = new Holder();
        View v;
        if (viewType=="table")
        {
            v=inflater.inflate(R.layout.row_items_table,null);
        }
        else
        {
            v=inflater.inflate(R.layout.row_items,null);
        }

        h.lblBarcode=v.findViewById(R.id.lblBarcode);
        h.lblMaterialCode=v.findViewById(R.id.lblMaterialCode);
        h.lblDescription=v.findViewById(R.id.lblDescription);
        h.lnrItems=v.findViewById(R.id.lnrItems);


        if (viewType=="table")
        {
            h.lblBarcode.setText(items.get(i).getUpc_number());
            h.lblMaterialCode.setText(items.get(i).getMaterial_code());
            h.lblDescription.setText(items.get(i).getMaterial_description());
        }
        else
        {
            h.lblBarcode.setText("Barcode: " + items.get(i).getUpc_number());
            h.lblMaterialCode.setText("Material Code: " +items.get(i).getMaterial_code());
            h.lblDescription.setText("Description: " +items.get(i).getMaterial_description());
        }

        h.lnrItems.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (SearchItem.count_type.equals("physical_count"))
                {
                    AddPhysicalCount.uomList.clear();
                    AddPhysicalCount.upc_number=items.get(i).getUpc_number();
                    AddPhysicalCount.material_code=items.get(i).getMaterial_code();
                    AddPhysicalCount.description=items.get(i).getMaterial_description();
                    //AddPhysicalCount.alternative_unit=items.get(i).getAlternative_unit();
                    //AddPhysicalCount.uomList.add(items.get(i).getAlternative_unit());
                }
                else if (SearchItem.count_type.equals("delivery_count"))
                {
                    AddDeliveryCount.uomList.clear();
                    AddDeliveryCount.upc_number=items.get(i).getUpc_number();
                    AddDeliveryCount.material_code=items.get(i).getMaterial_code();
                    AddDeliveryCount.description=items.get(i).getMaterial_description();
                    //AddDeliveryCount.alternative_unit=items.get(i).getAlternative_unit();
                    //AddDeliveryCount.uomList.add(items.get(i).getAlternative_unit());
                }
                else if (SearchItem.count_type.equals("return_count"))
                {
                    AddReturnCount.uomList.clear();
                    AddReturnCount.upc_number=items.get(i).getUpc_number();
                    AddReturnCount.material_code=items.get(i).getMaterial_code();
                    AddReturnCount.description=items.get(i).getMaterial_description();
                    //AddReturnCount.alternative_unit=items.get(i).getAlternative_unit();
                    //AddReturnCount.uomList.add(items.get(i).getAlternative_unit());
                }

                if (SearchItem.count_type.equals("physical_count"))
                {
                    AddPhysicalCount.uomList.clear();
                }
                else if (SearchItem.count_type.equals("delivery_count"))
                {
                    AddDeliveryCount.uomList.clear();
                }
                else if (SearchItem.count_type.equals("return_count"))
                {
                    AddReturnCount.uomList.clear();
                }

                MaterialSQL materialSQL;
                materialSQL=new MaterialSQL(context);
                ArrayList<MaterialSQL.MaterialDTO> dto = materialSQL.getUOM(materialSQL,items.get(i).getMaterial_code());
                int count=0;
                while (count<dto.size())
                {
                    if (SearchItem.count_type.equals("physical_count"))
                    {
                        AddPhysicalCount.uomList.add(dto.get(count).getAlternative_unit());
                    }
                    else if (SearchItem.count_type.equals("delivery_count"))
                    {
                        AddDeliveryCount.uomList.add(dto.get(count).getAlternative_unit());
                    }
                    else if (SearchItem.count_type.equals("return_count"))
                    {
                        AddReturnCount.uomList.add(dto.get(count).getAlternative_unit());
                    }
                    count++;
                }
                ((Activity)context).finish();
            }
        });
        return v;
    }
}
