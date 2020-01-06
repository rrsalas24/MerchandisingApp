package com.lafilgroup.merchandisinginventory.sendfeedback;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.storelogin.LoginTakePicture;

/**
 * Created by RR SALAS on 6/1/2018.
 */

public class MessageAdapter extends BaseAdapter
{
    MessagesDTO messagesDTO;
    Context context;
    private static LayoutInflater inflater=null;

    public MessageAdapter(MessagesDTO messagesDTO, Context context)
    {
        this.messagesDTO = messagesDTO;
        this.context = context;
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount()
    {
        if (messagesDTO.getItems().size() >0)
        {
            return messagesDTO.getItems().size();
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
        TextView lblSendTo, lblDate, lblSubject;
        ImageView imgStatus;
        LinearLayout lnrMessage;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        Holder h =new Holder();
        View v;
        v=inflater.inflate(R.layout.row_message_header,null);
        h.lblSendTo=v.findViewById(R.id.lblSendTo);
        h.lblDate=v.findViewById(R.id.lblDate);
        h.lblSubject=v.findViewById(R.id.lblSubject);
        h.imgStatus=v.findViewById(R.id.imgStatus);
        h.lnrMessage=v.findViewById(R.id.lnrMessage);

        if (messagesDTO.getItems().get(i).getSeen_by_sender().equals("yes"))
        {
            h.imgStatus.setImageResource(R.drawable.message_open);
        }
        else
        {
            h.imgStatus.setImageResource(R.drawable.message_new);
        }

        h.lblSendTo.setText(messagesDTO.getItems().get(i).getSend_to());
        h.lblDate.setText(GlobalVar.toDatetimeToString(messagesDTO.getItems().get(i).getCreated_at()));
        h.lblSubject.setText(messagesDTO.getItems().get(i).getSubject());

        h.lnrMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (messagesDTO.getItems().get(i).getSeen_by_sender().toString().equals("no"))
                {
                    updateMessageToSeen(messagesDTO.getItems().get(i).getMessage_id().toString());
                    Messages.mSeen=true;
                }
                else
                {
                    Messages.mSeen=false;
                }
                MessageItems.message_id=messagesDTO.getItems().get(i).getMessage_id().toString();
                MessageItems.title=messagesDTO.getItems().get(i).getSubject().toString();
                context.startActivity(new Intent(context, MessageItems.class));
            }
        });
        return v;
    }

    public void updateMessageToSeen(String message_id)
    {
        String url=context.getString(R.string.host) + context.getString(R.string.exec_url) + GlobalVar.api_token;
        //String qry = "Update merchandiser_message_header set seen_by_sender = 'yes' where message_id ='" + message_id +"'";
        String qry = "call p_update_message_header('"+message_id+"','yes')";
        CustomStringRequest customStringRequest=new CustomStringRequest(context);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                if (response.toString().equals("success"))
                {

                }
                else if (response.toString().equals("failed"))
                {

                }
            }
            @Override
            public void onError(VolleyError error)
            {

                error.printStackTrace();
            }
        });
    }
}
