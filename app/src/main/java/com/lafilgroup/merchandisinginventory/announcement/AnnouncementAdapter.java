package com.lafilgroup.merchandisinginventory.announcement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CircleTransform;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.squareup.picasso.Picasso;

/**
 * Created by RR SALAS on 4/3/2018.
 */

public class AnnouncementAdapter extends BaseAdapter
{
    public static String viewType;
    AnnouncementsDTO announcementsDTO;
    Context context;
    private static LayoutInflater inflater=null;

    public AnnouncementAdapter(AnnouncementsDTO announcementsDTO, Context context)
    {
        this.announcementsDTO = announcementsDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(announcementsDTO.getItems().size()>0)
        {
            return announcementsDTO.getItems().size();
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
        TextView lblDate, lblName, lblMessage;
        ImageView imgPhoto;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Holder h = new Holder();
        View v;
        if (viewType=="table")
        {
             v=inflater.inflate(R.layout.row_announcements_table,null);
        }
        else
        {
            v=inflater.inflate(R.layout.row_announcements,null);
        }

        h.lblName=v.findViewById(R.id.lblName);
        h.lblDate=v.findViewById(R.id.lblDate);
        h.lblMessage=v.findViewById(R.id.lblMessage);
        h.imgPhoto=v.findViewById(R.id.imgPhoto);

        h.lblName.setText(announcementsDTO.getItems().get(i).getFirst_name() + " " +announcementsDTO.getItems().get(i).getLast_name());
        h.lblDate.setText(GlobalVar.toDatetimeToString(announcementsDTO.getItems().get(i).created_at));
        h.lblMessage.setText(announcementsDTO.getItems().get(i).getMessage());
        Picasso.with(context).load(context.getString(R.string.host) + context.getString(R.string.image_url)+announcementsDTO.getItems().get(i).getImage_path()).transform(new CircleTransform()).placeholder(R.mipmap.ic_launcher_round).into(h.imgPhoto);
        return v;
    }
}
