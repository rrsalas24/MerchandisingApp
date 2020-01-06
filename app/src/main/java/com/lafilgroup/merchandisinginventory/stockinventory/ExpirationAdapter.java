package com.lafilgroup.merchandisinginventory.stockinventory;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.home.AnnouncementRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/5/2018.
 */

public class ExpirationAdapter extends RecyclerView.Adapter<ExpirationAdapter.MyViewHolder>
{
    public static String remove_type;
    ArrayList<ExpirationDetailsDTO> arrayList=new ArrayList<>();
    Context context;
    private static LayoutInflater inflater=null;

    public ExpirationAdapter(ArrayList<ExpirationDetailsDTO> arrayList, Context context)
    {
        this.arrayList = arrayList;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_expiration_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        holder.lblExpirationDate.setText(arrayList.get(position).getExpiration_date());
        holder.lblExpirationQTY.setText(arrayList.get(position).getExpiration_qty());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (remove_type.equals("add_physical"))
                {
                    AddPhysicalCount.expirationQTY.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    AddPhysicalCount.expirationDate.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    expirationDetailsAddPhysicalCount();
                }
                else if (remove_type.equals("add_delivery"))
                {
                    AddDeliveryCount.expirationQTY.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    AddDeliveryCount.expirationDate.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    expirationDetailsAddDeliveryCount();
                }
                else if (remove_type.equals("add_return"))
                {
                    AddReturnCount.expirationQTY.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    AddReturnCount.expirationDate.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    expirationDetailsAddReturnCount();
                }
                else
                {
                    PhysicalCountDetails.expirationQTY.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    PhysicalCountDetails.expirationDate.remove(Integer.parseInt(arrayList.get(position).getExpiration_id()));
                    expirationDetailsPhysicalCountDetails();
                }
            }
        });
    }

    public  void expirationDetailsAddPhysicalCount()
    {
        int counter=0;
        arrayList.clear();
        while (counter<AddPhysicalCount.expirationDate.size())
        {
            ExpirationDetailsDTO expirationDetailsDTO=new ExpirationDetailsDTO();
            expirationDetailsDTO.setExpiration_id(counter +"");
            expirationDetailsDTO.setExpiration_date(AddPhysicalCount.expirationDate.get(counter));
            expirationDetailsDTO.setExpiration_qty(AddPhysicalCount.expirationQTY.get(counter));
            arrayList.add(expirationDetailsDTO);
            counter++;
        }
        AddPhysicalCount.expirationAdapter=new ExpirationAdapter(arrayList,context);
        AddPhysicalCount.layoutManager= new LinearLayoutManager(context);
        AddPhysicalCount.rvwExpiration.setLayoutManager(AddPhysicalCount.layoutManager);
        AddPhysicalCount.rvwExpiration.setAdapter(AddPhysicalCount.expirationAdapter);
    }

    public  void expirationDetailsAddDeliveryCount()
    {
        int counter=0;
        arrayList.clear();
        while (counter<AddPhysicalCount.expirationDate.size())
        {
            ExpirationDetailsDTO expirationDetailsDTO=new ExpirationDetailsDTO();
            expirationDetailsDTO.setExpiration_id(counter +"");
            expirationDetailsDTO.setExpiration_date(AddDeliveryCount.expirationDate.get(counter));
            expirationDetailsDTO.setExpiration_qty(AddDeliveryCount.expirationQTY.get(counter));
            arrayList.add(expirationDetailsDTO);
            counter++;
        }
        AddDeliveryCount.expirationAdapter=new ExpirationAdapter(arrayList,context);
        AddDeliveryCount.layoutManager= new LinearLayoutManager(context);
        AddDeliveryCount.rvwExpiration.setLayoutManager(AddDeliveryCount.layoutManager);
        AddDeliveryCount.rvwExpiration.setAdapter(AddDeliveryCount.expirationAdapter);
    }

    public void expirationDetailsAddReturnCount()
    {
        int counter=0;
        arrayList.clear();
        while (counter<AddReturnCount.expirationDate.size())
        {
            ExpirationDetailsDTO expirationDetailsDTO=new ExpirationDetailsDTO();
            expirationDetailsDTO.setExpiration_id(counter +"");
            expirationDetailsDTO.setExpiration_date(AddReturnCount.expirationDate.get(counter));
            expirationDetailsDTO.setExpiration_qty(AddReturnCount.expirationQTY.get(counter));
            arrayList.add(expirationDetailsDTO);
            counter++;
        }
        AddReturnCount.expirationAdapter=new ExpirationAdapter(arrayList,context);
        AddReturnCount.layoutManager= new LinearLayoutManager(context);
        AddReturnCount.rvwExpiration.setLayoutManager(AddReturnCount.layoutManager);
        AddReturnCount.rvwExpiration.setAdapter(AddReturnCount.expirationAdapter);
    }


    public  void expirationDetailsPhysicalCountDetails()
    {
        int counter=0;
        arrayList.clear();
        while (counter<PhysicalCountDetails.expirationDate.size())
        {
            ExpirationDetailsDTO expirationDetailsDTO=new ExpirationDetailsDTO();
            expirationDetailsDTO.setExpiration_id(counter +"");
            expirationDetailsDTO.setExpiration_date(PhysicalCountDetails.expirationDate.get(counter));
            expirationDetailsDTO.setExpiration_qty(PhysicalCountDetails.expirationQTY.get(counter));
            arrayList.add(expirationDetailsDTO);
            counter++;
        }
        PhysicalCountDetails.expirationAdapter=new ExpirationAdapter(arrayList,context);
        PhysicalCountDetails.layoutManager= new LinearLayoutManager(context);
        PhysicalCountDetails.rvwExpiration.setLayoutManager(PhysicalCountDetails.layoutManager);
        PhysicalCountDetails.rvwExpiration.setAdapter(PhysicalCountDetails.expirationAdapter);
    }

    @Override
    public int getItemCount()
    {
        if (arrayList.size()>0)
        {
            return arrayList.size();
        }
        return  0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView lblExpirationDate, lblExpirationQTY;
        Button btnRemove;
        public MyViewHolder(View view)
        {
            super(view);
            lblExpirationDate = view.findViewById(R.id.lblExpirationDate);
            lblExpirationQTY =  view.findViewById(R.id.lblExpirationQTY);
            btnRemove=view.findViewById(R.id.btnRemove);

        }
    }
}
