package com.lafilgroup.merchandisinginventory.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CircleTransform;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class Profile extends Fragment implements View.OnClickListener
{
    TextView lblName, lblAgencyName, lblMerchandiserID,lblDateCreated,lblUsername,lblGender, lblBirthdate, lblEmail, lblContactNumber,lblAddress;
    Button btnChangePicture, btnChangePassword, btnUpdateContact;
    public static boolean contact_updated;
    public static boolean password_updated;
    ScrollView svProfile;
    ImageView imgUserPhoto;
    LinearLayout lnrNoNetwork;
    MessageDialog messageDialog;
    WaitingDialog waitingDialog;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,null);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        lblName=view.findViewById(R.id.lblName);
        lblAgencyName=view.findViewById(R.id.lblAgencyName);
        lblMerchandiserID=view.findViewById(R.id.lblMerchandiserID);
        lblDateCreated=view.findViewById(R.id.lblDateCreated);
        lblUsername=view.findViewById(R.id.lblUserName);
        lblGender=view.findViewById(R.id.lblGender);
        lblBirthdate=view.findViewById(R.id.lblBirthdate);
        lblEmail=view.findViewById(R.id.lblEmail);
        lblContactNumber=view.findViewById(R.id.lblContactNumber);
        lblAddress=view.findViewById(R.id.lblAddress);
        imgUserPhoto=view.findViewById(R.id.imgUserPhoto);
        btnChangePicture=view.findViewById(R.id.btnChangePicture);
        btnChangePassword=view.findViewById(R.id.btnChangePassword);
        btnUpdateContact=view.findViewById(R.id.btnUpdateContact);
        lnrNoNetwork=view.findViewById(R.id.lnrNoNetwork);


        btnChangePicture.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnUpdateContact.setOnClickListener(this);

        svProfile=view.findViewById(R.id.svProfile);

        contact_updated=false;
        getUserInfo();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (contact_updated)
        {
            contact_updated=false;
            messageDialog=new MessageDialog(getActivity(),"Success","Contact information updated.");
            messageDialog.messageSuccess();
//            Snackbar snackbar = Snackbar.make(svProfile, "Contact information updated.", Snackbar.LENGTH_LONG);
//            snackbar.show();
            getUserInfo();
        }

        if (password_updated)
        {
            password_updated=false;
            messageDialog=new MessageDialog(getActivity(),"Success","Password changed.");
            messageDialog.messageSuccess();
//            Snackbar snackbar = Snackbar.make(svProfile, "Password changed.", Snackbar.LENGTH_LONG);
//            snackbar.show();
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnChangePicture:
                startActivity(new Intent(getActivity(), ChangePicture.class));
                break;
            case R.id.btnChangePassword:
                startActivity(new Intent(getActivity(), ChangePassword.class));
                break;
            case R.id.btnUpdateContact:
                Intent intent= new Intent(getActivity(), UpdateContact.class);
                intent.putExtra("email",lblEmail.getText().toString());
                intent.putExtra("contactnumber",lblContactNumber.getText().toString());
                intent.putExtra("address",lblAddress.getText().toString());
                startActivity(intent);
                break;
        }
    }

    public void getUserInfo()
    {
        waitingDialog=new WaitingDialog(getActivity());
        waitingDialog.openDialog();

        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
        String qry = "call p_merchandiser('" + GlobalVar.user_id +  "')";

        CustomStringRequest customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.GONE);
                btnUpdateContact.setVisibility(View.VISIBLE);
                try
                {
                    JSONObject parentjsonObject =new JSONObject(response);
                    JSONArray jsonArray = parentjsonObject.getJSONArray("items");
                    JSONObject jsonObject =jsonArray.getJSONObject(0);
                    lblName.setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
                    lblAgencyName.setText(jsonObject.getString("agency_name"));
                    lblMerchandiserID.setText(jsonObject.getString("merchandiser_id"));
                    lblDateCreated.setText(GlobalVar.toDatetimeToString(jsonObject.getString("created_at")));
                    lblUsername.setText(jsonObject.getString("username"));
                    lblGender.setText(jsonObject.getString("gender"));
                    lblBirthdate.setText(GlobalVar.toDateToString(jsonObject.getString("birth_date")));
                    lblEmail.setText(jsonObject.getString("email"));
                    lblContactNumber.setText(jsonObject.getString("contact_number"));
                    lblAddress.setText(jsonObject.getString("address"));
                    Picasso.with(getActivity()).load(   getString(R.string.host) + getString(R.string.image_url)+jsonObject.getString("image_path")).transform(new CircleTransform()).placeholder(R.mipmap.ic_launcher_round).into(imgUserPhoto);
                }
                catch (JSONException e)
                {
                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.VISIBLE);
                //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                btnUpdateContact.setVisibility(View.GONE);
                messageDialog=new MessageDialog(getActivity(),"Error","No network connectivity.");
                messageDialog.messageError();
            }
        });
    }
}
