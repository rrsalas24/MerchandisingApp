package com.lafilgroup.merchandisinginventory.sendfeedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;

/**
 * Created by RR SALAS on 6/4/2018.
 */

public class MessageItemsAdapter extends BaseAdapter
{
    MessageItemsDTO messageItemsDTO;
    Context context;
    private static LayoutInflater inflater=null;

    public MessageItemsAdapter(MessageItemsDTO messageItemsDTO, Context context)
    {
        this.messageItemsDTO = messageItemsDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        if (messageItemsDTO.getItems().size() >0)
        {
            return messageItemsDTO.getItems().size();
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
        TextView lblName, lblDate, lblMessage;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Holder h = new Holder();
        View v;
        if (messageItemsDTO.getItems().get(i).getMerchandiser_id().equals(GlobalVar.user_id))
        {
            v=inflater.inflate(R.layout.row_message_items_sender,null);
        }
        else
        {
            v=inflater.inflate(R.layout.row_message_items_receiver,null);
        }

        h.lblName = v.findViewById(R.id.lblName);
        h.lblDate =  v.findViewById(R.id.lblDate);
        h.lblMessage = v.findViewById(R.id.lblMessage);

        h.lblName.setText(messageItemsDTO.getItems().get(i).getFirst_name() + " " +messageItemsDTO.getItems().get(i).getLast_name());
        h.lblDate.setText(GlobalVar.toDatetimeToString(messageItemsDTO.getItems().get(i).getCreated_at()));
        h.lblMessage.setText(messageItemsDTO.getItems().get(i).getMessage());
        return v;
    }
}
