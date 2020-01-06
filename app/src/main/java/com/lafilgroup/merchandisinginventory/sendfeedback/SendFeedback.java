package com.lafilgroup.merchandisinginventory.sendfeedback;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;


/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class SendFeedback extends AppCompatActivity
{
    Spinner spnSendTo;
    String send_to;
    Button btnSend;
    EditText txtFeedback, txtSubject;
    ScrollView svFeedback;
    WaitingDialog waitingDialog;
    MessageDialog messageDialog;
    TextView lblCounter;

    //use for spinner item
    ArrayList<String> accountType=new ArrayList<>();
    ArrayList<String> accountId=new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        this.setTitle("Send Message");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spnSendTo=findViewById(R.id.spnSendTo);
        spinnerItem();
        btnSend=findViewById(R.id.btnSend);
        txtFeedback=findViewById(R.id.txtFeedback);
        txtSubject=findViewById(R.id.txtSubject);
        svFeedback=findViewById(R.id.svFeedback);
        lblCounter=findViewById(R.id.lblCounter);

        spnSendTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                send_to=accountId.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               if (!txtSubject.getText().toString().equals(""))
               {
                   if (!txtFeedback.getText().toString().equals(""))
                   {
                       sendFeedback();
                   }
                   else
                   {
                       messageDialog=new MessageDialog(SendFeedback.this,"","Kindly input message.");
                       messageDialog.messageError();
                   }
               }
               else
               {
                   messageDialog=new MessageDialog(SendFeedback.this,"","Kindly input subject.");
                   messageDialog.messageError();
               }
            }
        });

        final TextWatcher txtWatcher =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lblCounter.setText(txtFeedback.length()+"/255");
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        txtFeedback.addTextChangedListener(txtWatcher);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        Messages.mSent=false;
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Messages.mSent=false;
    }


    public void sendFeedback()
    {
        waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.exec_url) + GlobalVar.api_token;
//        String qry = "insert into merchandiser_message_header (merchandiser_id, send_to, subject, message, status, seen_by_sender, seen_by_receiver, created_at) " +
//                "values ('" + GlobalVar.user_id + "','" + send_to + "','" + txtSubject.getText().toString() + "','" + txtFeedback.getText().toString() + "','" + "001" + "','" + "yes" + "','" + "no" + "','" + GlobalVar.getCurrentDateTime() + "')";
        String qry="call p_insert_message_header('" + GlobalVar.user_id + "','" + send_to + "','" + txtSubject.getText().toString() + "','" + txtFeedback.getText().toString() + "','" + "001" + "','" + "yes" + "','" + "no" + "','" + GlobalVar.getCurrentDateTime() + "')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
              waitingDialog.closeDialog();
                if (response.toString().equals("success"))
                {
                    txtFeedback.setText("");
                    Messages.mSent=true;
                    finish();
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog=new MessageDialog(SendFeedback.this,"Error","Failed to send message.");
                    messageDialog.messageError();
                    Messages.mSent=false;
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(SendFeedback.this,"Error","No network connectivity.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }

    public void spinnerItem()
    {
//        waitingDialog=new WaitingDialog(this);
//        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//        String qry = "Select * from account_type where id <> 3 order by id desc";
        String qry = "call p_account_type";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback()
        {
            @Override
            public void onSuccess(String response)
            {
                //waitingDialog.closeDialog();
                try
                {
                    int count =0;
                    JSONObject parentJsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentJsonObject.getJSONArray("items");

                    while (count<jsonArray.length())
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(count);
                        accountType.add(jsonObject.getString("type"));
                        accountId.add(jsonObject.getString("id"));
                        count++;
                    }
                    adapter=new ArrayAdapter(SendFeedback.this,R.layout.support_simple_spinner_dropdown_item,accountType);
                    spnSendTo.setAdapter(adapter);
                }
                catch (JSONException e)
                {
                    //waitingDialog.closeDialog();
                    messageDialog=new MessageDialog(SendFeedback.this,"Error","Error in parsing 'send to' details.");
                    messageDialog.messageError();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error)
            {
                //waitingDialog.closeDialog();
                messageDialog=new MessageDialog(SendFeedback.this,"Error","No network connectivity.");
                messageDialog.messageError();
                error.printStackTrace();
            }
        });
    }
}
