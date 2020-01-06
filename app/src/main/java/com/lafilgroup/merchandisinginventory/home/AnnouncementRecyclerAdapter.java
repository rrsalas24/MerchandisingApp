package com.lafilgroup.merchandisinginventory.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementsDTO;
import com.lafilgroup.merchandisinginventory.config.CircleTransform;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.squareup.picasso.Picasso;

/**
 * Created by RR SALAS on 4/5/2018.
 */

public class AnnouncementRecyclerAdapter extends RecyclerView.Adapter<AnnouncementRecyclerAdapter.MyViewHolder>
{
    AnnouncementsDTO announcementsDTO;
    Context context;
    private static LayoutInflater inflater=null;

    public AnnouncementRecyclerAdapter(AnnouncementsDTO announcementsDTO, Context context)
    {
        this.announcementsDTO = announcementsDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_announcements, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.lblName.setText(announcementsDTO.getItems().get(position).getFirst_name() + " " +announcementsDTO.getItems().get(position).getLast_name());
        holder.lblDate.setText(GlobalVar.toDatetimeToString(announcementsDTO.getItems().get(position).created_at));
        holder.lblMessage.setText(announcementsDTO.getItems().get(position).getMessage());
        Picasso.with(context).load(context.getString(R.string.host) + context.getString(R.string.image_url)+announcementsDTO.getItems().get(position).getImage_path()).transform(new CircleTransform()).placeholder(R.mipmap.ic_launcher_round).into(holder.imgPhoto);
    }

    @Override
    public int getItemCount()
    {
        return announcementsDTO.getItems().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView lblName, lblDate, lblMessage;
        ImageView imgPhoto;
        public MyViewHolder(View view)
        {
            super(view);
            lblName = view.findViewById(R.id.lblName);
            lblDate =  view.findViewById(R.id.lblDate);
            lblMessage = view.findViewById(R.id.lblMessage);
            imgPhoto = view.findViewById(R.id.imgPhoto);
        }
    }


}
