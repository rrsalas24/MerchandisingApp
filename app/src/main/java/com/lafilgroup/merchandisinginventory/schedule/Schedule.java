package com.lafilgroup.merchandisinginventory.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class Schedule extends Fragment implements View.OnClickListener,RadioGroup.OnCheckedChangeListener
{
    ListView lvwSchedule;
    SchedulesDTO schedulesDTO;
    ScheduleAdapter scheduleAdapter;
    Button btnSearch, btnStartDate, btnEndDate;
    TextView lblStartDate, lblEndDate;
    DatePickerDialog datePickerDialog;
    LinearLayout lnrNoNetwork,lnrColumn;
    RadioGroup rgOption;
    RadioButton rbListView,rbTableView;
    MessageDialog messageDialog;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_schedule,null);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        lnrNoNetwork=view.findViewById(R.id.lnrNoNetwork);
        lnrColumn=view.findViewById(R.id.lnrColumn);

        lvwSchedule=view.findViewById(R.id.lvwSchedule);
        btnSearch=view.findViewById(R.id.btnSearch);

        btnStartDate=view.findViewById(R.id.btnStartDate);
        btnEndDate=view.findViewById(R.id.btnEndDate);

        lblStartDate=view.findViewById(R.id.lblStartDate);
        lblEndDate=view.findViewById(R.id.lblEndDate);

        rgOption=view.findViewById(R.id.rgOption);
        rbListView=view.findViewById(R.id.rbListView);
        rbTableView=view.findViewById(R.id.rbTableView);

        lblStartDate.setText(GlobalVar.toDateToString(GlobalVar.getCurrentDate(0)));
        lblEndDate.setText(GlobalVar.toDateToString(GlobalVar.getCurrentDate(10)));

        btnSearch.setOnClickListener(this);
        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        rgOption.setOnCheckedChangeListener(this);
        searchSchedule(false);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i)
    {
        switch (radioGroup.getId())
        {
            case R.id.rgOption:
                if (i==R.id.rbListView)
                {
                    searchSchedule(false);
                }
                else if (i==R.id.rbTableView)
                {
                    searchSchedule(true);
                }
                break;
        }
    }

    public void searchSchedule(Boolean column)
    {
        lvwSchedule.setAdapter(null);
        if (column==false)
        {
           getSchedule("list");
           lnrColumn.setVisibility(View.GONE);
           lvwSchedule.setDivider(null);
        }
        else
        {
            getSchedule("table");
            lnrColumn.setVisibility(View.VISIBLE);
            ColorDrawable colorDrawable=new ColorDrawable(this.getResources().getColor(R.color.divider));
            lvwSchedule.setDivider(colorDrawable);
            lvwSchedule.setDividerHeight(1);
        }
    }

    public static final long toStringToDate (String stringdate)
    {
        String generatedate;
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
        Date date;
        try
        {
            date = format.parse(stringdate);
            generatedate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            return Date.parse(generatedate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View view)
    {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        switch (view.getId())
        {
            case R.id.btnStartDate:
                datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        String d=i +"-"  + (i1+1) +"-" +i2;
                        lblStartDate.setText(GlobalVar.toDateToString(d));
                    }
                }, year, month, day);
                datePickerDialog.setTitle("Select Start Date");
                datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Select",datePickerDialog);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                break;

            case R.id.btnEndDate:
                datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        String d=i +"-"  + (i1+1) +"-" +i2;
                        lblEndDate.setText(GlobalVar.toDateToString(d));
                    }
                }, year, month, day);
                datePickerDialog.setTitle("Select End Date");
                datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Select",datePickerDialog);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                break;

            case R.id.btnSearch:
             if (rbListView.isChecked())
             {
                 searchSchedule(false);
             }
             else if (rbTableView.isChecked())
             {
                 searchSchedule(true);
             }
             break;
        }
    }

    public void getSchedule(final String viewType)
    {
        final WaitingDialog waitingDialog=new WaitingDialog(getActivity());
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.query_url) + GlobalVar.api_token;
//        String qry = "Select * from vw_schedules where merchandiser_id like '" + GlobalVar.user_id +
//                "' and (date between '" + GlobalVar.toStringToDate(lblStartDate.getText().toString()) +
//                "' and '" + GlobalVar.toStringToDate(lblEndDate.getText().toString()) + "') order by date asc";

        String qry = "call p_schedules('"+GlobalVar.user_id+"','"+GlobalVar.toStringToDate(lblStartDate.getText().toString())+"','"+GlobalVar.toStringToDate(lblEndDate.getText().toString())+"')";

        CustomStringRequest customStringRequest=new CustomStringRequest(getActivity());
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.GONE);
                ScheduleAdapter.viewType=viewType;
                jsonToGSon(response);
            }
            @Override
            public void onError(VolleyError error)
            {
                waitingDialog.closeDialog();
                lnrNoNetwork.setVisibility(View.VISIBLE);
                messageDialog=new MessageDialog(getActivity(),"Error","No network connectivity.");
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
            schedulesDTO=gson.fromJson(response, SchedulesDTO.class);
            if(schedulesDTO.getItems().size()==0)
            {
                schedulesDTO.getItems().clear();
                messageDialog=new MessageDialog(getActivity(),"","No record found.");
                messageDialog.messageInformation();
            }
            scheduleAdapter=new ScheduleAdapter(schedulesDTO,getActivity());
            lvwSchedule.setAdapter(scheduleAdapter);
        }
        catch (Exception error)
        {
            messageDialog=new MessageDialog(getActivity(),"Error","Error occurred. (Details: Error during schedule page DTO) "+error.getMessage());
            messageDialog.messageError();
        }
    }
}
