package com.lafilgroup.merchandisinginventory.sendfeedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.announcement.AnnouncementsDTO;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.home.AnnouncementRecyclerAdapter;

import java.util.List;

public class MessageItems extends AppCompatActivity
{
    public static String message_id, title;
    Button btnSend;
    EditText txtMessage;
    MessageItemsDTO messageItemsDTO;
    MessageItemsAdapter messageItemsAdapter;
    ListView lvwMessageItems;
    WaitingDialog waitingDialog;
    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_items);
        this.setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnSend=findViewById(R.id.btnSend);
        txtMessage=findViewById(R.id.txtMessage);
        lvwMessageItems=findViewById(R.id.lvwMessageItems);
        getMessageItems();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!txtMessage.getText().toString().equals(""))
                {
                    sendReply(txtMessage.getText().toString());
                }
                else
                {
                    messageDialog=new MessageDialog(MessageItems.this,"","Kindly input message.");
                    messageDialog.messageError();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.mybutton)
        {
            getMessageItems();
        }
        else
        {
            Messages.mSent=false;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Messages.mSent=false;
    }

    public void getMessageItems()
    {
        waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        //String qry = "Select * from vw_merchandiser_message_items where message_id = '" + message_id + "' order by created_at asc";
        String qry = "call p_message_items('"+message_id+"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(MessageItems.this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                jsonToGSon(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(MessageItems.this,"Error","No network connectivity.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }

    private void jsonToGSon(String response)
    {
        try
        {
            Gson gson =new Gson();
            messageItemsDTO=gson.fromJson(response, MessageItemsDTO.class);
            if(messageItemsDTO.getItems().size()==0)
            {
                messageItemsDTO.getItems().clear();
            }
            else
            {

            }
            messageItemsAdapter=new MessageItemsAdapter(messageItemsDTO,this);
            lvwMessageItems.setAdapter(messageItemsAdapter);
        }
        catch (Exception error)
        {
            messageDialog=new MessageDialog(this,"Error","Error occurred. (Details: Error during messages conversation page DTO) "+error.getMessage());
            messageDialog.messageError();
        }
    }

    public void sendReply(String message)
    {
        String url=getString(R.string.host) + getString(R.string.exec_url) + GlobalVar.api_token;
//        String qry = "insert into merchandiser_message_items (message_id, merchandiser_id, message, created_at) " +
//                "values ('" + message_id + "','" + GlobalVar.user_id + "','" + message + "','" +  GlobalVar.getCurrentDateTime() + "')";
        String qry = "call p_insert_message_items ('" + message_id + "','" + GlobalVar.user_id + "','" + message + "','" +  GlobalVar.getCurrentDateTime() + "')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                if (response.toString().equals("success"))
                {
                    updateMessageToSeen(message_id);
                    getMessageItems();
                    txtMessage.setText("");
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog=new MessageDialog(MessageItems.this,"Error","Failed to send message.");
                    messageDialog.messageError();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                messageDialog=new MessageDialog(MessageItems.this,"Error","No network connectivity.");
                messageDialog.messageError();
            }
        });
    }

    public void updateMessageToSeen(String message_id)
    {
        String url=getString(R.string.host) + getString(R.string.exec_url) + GlobalVar.api_token;
        //String qry = "Update merchandiser_message_header set seen_by_receiver = 'no' where message_id ='" + message_id +"'";
        String qry = "call p_update_message_header1('"+message_id+"','no')";
        CustomStringRequest customStringRequest=new CustomStringRequest(MessageItems.this);
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
                messageDialog=new MessageDialog(MessageItems.this,"Error","No network connectivity.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }
}
