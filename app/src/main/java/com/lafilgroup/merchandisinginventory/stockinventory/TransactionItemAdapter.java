package com.lafilgroup.merchandisinginventory.stockinventory;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.sendfeedback.MessageItems;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;
import com.lafilgroup.merchandisinginventory.storelogin.LoginTakePicture;

import java.util.ArrayList;


/**
 * Created by RR SALAS on 6/6/2018.
 */

public class TransactionItemAdapter extends RecyclerView.Adapter<TransactionItemAdapter.MyViewHolder>
{
    String viewTypeView;
    String countType;
    ArrayList<TransactionItemsSQL.TransactionItemDTO> items;
    Context context;
    private static LayoutInflater inflater=null;


    public TransactionItemAdapter(ArrayList<TransactionItemsSQL.TransactionItemDTO> items, Context context, String viewTypeView, String countType) {
        this.viewTypeView = viewTypeView;
        this.countType = countType;
        this.items = items;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
            if (viewTypeView.equals("list"))
            {
                itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_transaction_items_list, parent, false);
            }
            else
            {
                itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_transaction_items_table, parent, false);
            }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
            if (countType.equals("physical"))
            {
                holder.lblLocation.setVisibility(View.VISIBLE);
                holder.lblDeliveryDate.setVisibility(View.GONE);
                holder.lblReturnDate.setVisibility(View.GONE);
            }
            else if (countType.equals("delivery"))
            {
                holder.lblLocation.setVisibility(View.GONE);
                holder.lblDeliveryDate.setVisibility(View.VISIBLE);
                holder.lblReturnDate.setVisibility(View.GONE);
            }
            else if (countType.equals("return"))
            {
                holder.lblLocation.setVisibility(View.GONE);
                holder.lblDeliveryDate.setVisibility(View.GONE);
                holder.lblReturnDate.setVisibility(View.VISIBLE);
            }

            if (viewTypeView.equals("list"))
            {
                holder.lblDescription.setText(items.get(position).getDescription());
                holder.lblUOM.setText("UOM: " + items.get(position).getEntry_uom());
                holder.lblLocation.setText("Location: "+items.get(position).getLocation());
                holder.lblDeliveryDate.setText("Delivery Date: "+items.get(position).getDelivery_date());
                holder.lblReturnDate.setText("Return Date: "+items.get(position).getReturn_date());
                holder.lblQTYCounted.setText("QTY: "+items.get(position).getEntry_qty()+"");
            }
            else
            {
                holder.lblDescription.setText(items.get(position).getDescription());
                holder.lblUOM.setText(items.get(position).getEntry_uom());
                holder.lblLocation.setText(items.get(position).getLocation());
                holder.lblDeliveryDate.setText(items.get(position).getDelivery_date());
                holder.lblReturnDate.setText(items.get(position).getReturn_date());
                holder.lblQTYCounted.setText(items.get(position).getEntry_qty()+"");
            }

            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhysicalCountDetails.transaction_items_id=items.get(position).getId();
                    context.startActivity(new Intent(context, PhysicalCountDetails.class));
                }
            });
    }

    @Override
    public int getItemCount() {
        if (items.size()>0)
        {
            return items.size();
        }
        return  0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView lblDescription, lblUOM, lblLocation, lblQTYCounted, lblDeliveryDate, lblReturnDate;
        ImageButton btnView;
        public MyViewHolder(View view)
        {
            super(view);
            lblDescription = view.findViewById(R.id.lblDescription);
            lblUOM = view.findViewById(R.id.lblUOM);
            lblLocation = view.findViewById(R.id.lblLocation);
            lblQTYCounted = view.findViewById(R.id.lblQTYCounted);
            lblDeliveryDate=view.findViewById(R.id.lblDeliveryDate);
            lblReturnDate=view.findViewById(R.id.lblReturnDate);
            btnView=view.findViewById(R.id.btnView);
        }
    }
}
