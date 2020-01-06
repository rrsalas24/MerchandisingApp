package com.lafilgroup.merchandisinginventory.stockinventory;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.MaterialBalanceAdapter;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GPSLocator;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.profile.ChangePassword;
import com.lafilgroup.merchandisinginventory.profile.ChangePicture;
import com.lafilgroup.merchandisinginventory.profile.Profile;
import com.lafilgroup.merchandisinginventory.profile.UpdateContact;
import com.lafilgroup.merchandisinginventory.sqlconfig.CustomerSubmitSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.DBHelper;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.OfflineInventoryHeaderSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionExpirationSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;
import com.lafilgroup.merchandisinginventory.storelogin.LoginTakePicture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class StockInventory extends Fragment implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    TextView lblCustomer, lblNoPhysicalCount, lblNoDeliveryCount, lblNoReturnCount;
    Button btnAddPhysicalCount, btnAddDeliveryCount, btnAddReturn, btnSubmit;

    RecyclerView rvwPhysicalCount, rvwDeliveryCount, rvwReturnCount;
    RecyclerView.LayoutManager layoutManager;

    RadioGroup rgOption;
    RadioButton rbListView, rbTableView;

    LinearLayout lnrPhysicalColumn, lnrDeliveryColumn, lnrReturnColumn;

    TransactionItemsSQL transactionItemsSQL;
    TransactionItemAdapter transactionItemAdapter;

    MaterialSQL materialSQL;
    OfflineInventoryHeaderSQL offlineInventoryHeaderSQL;

    public static Boolean submitted_status, entry_update, entry_delete, entry_added, offline_save;

    MessageDialog messageDialog;
    SweetAlertDialog sweetAlertDialog;
    WaitingDialog waitingDialog;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock_inventory, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lblCustomer = view.findViewById(R.id.lblCustomer);

        lblNoPhysicalCount = view.findViewById(R.id.lblNoPhysicalCount);
        lblNoDeliveryCount = view.findViewById(R.id.lblNoDeliveryCount);
        lblNoReturnCount = view.findViewById(R.id.lblNoReturnCount);

        btnAddPhysicalCount = view.findViewById(R.id.btnAddPhysicalCount);
        btnAddDeliveryCount = view.findViewById(R.id.btnAddDeliveryCount);
        btnAddReturn = view.findViewById(R.id.btnAddReturn);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        rgOption = view.findViewById(R.id.rgOption);
        rbListView = view.findViewById(R.id.rbListView);
        rbTableView = view.findViewById(R.id.rbTableView);

        lnrPhysicalColumn = view.findViewById(R.id.lnrPhysicalColumn);
        lnrDeliveryColumn = view.findViewById(R.id.lnrDeliveryColumn);
        lnrReturnColumn = view.findViewById(R.id.lnrReturnColumn);

        lblCustomer.setText("Customer: " + GlobalVar.customer_name);

        rvwPhysicalCount = view.findViewById(R.id.rvwPhysicalCount);
        rvwDeliveryCount = view.findViewById(R.id.rvwDeliveryCount);
        rvwReturnCount = view.findViewById(R.id.rvwReturnCount);

        btnAddPhysicalCount.setOnClickListener(this);
        btnAddDeliveryCount.setOnClickListener(this);
        btnAddReturn.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        rgOption.setOnCheckedChangeListener(this);

        submitted_status = false;
        entry_update = false;
        entry_delete = false;
        entry_added = false;
        offline_save=false;

        transactionPhysicalCount("list");
        transactionDeliveryCount("list");
        transactionReturnCount("list");
    }

    @Override
    public void onResume() {
        if (submitted_status == true) {
            submitted_status = false;
            messageDialog = new MessageDialog(getActivity(), "Success", "Inventory submitted to server successfully.");
            messageDialog.messageSuccess();
            getActivity().onBackPressed();
        } else {
            if (entry_update == true) {
                entry_update = false;
                messageDialog = new MessageDialog(getActivity(), "Success", "Entry updated.");
                messageDialog.messageSuccess();
            }

            if (entry_delete == true) {
                entry_delete = false;
                messageDialog = new MessageDialog(getActivity(), "Success", "Entry deleted.");
                messageDialog.messageSuccess();
            }

            if (entry_added == true) {
                entry_added = false;
                messageDialog = new MessageDialog(getActivity(), "Success", "Entry added.");
                messageDialog.messageSuccess();
            }

            if (rbListView.isChecked()) {
                transactionPhysicalCount("list");
                transactionDeliveryCount("list");
                transactionReturnCount("list");
            } else if (rbTableView.isChecked()) {
                transactionPhysicalCount("table");
                transactionDeliveryCount("table");
                transactionReturnCount("table");
            }
        }

        if (offline_save==true)
        {
            getActivity().onBackPressed();
            messageDialog = new MessageDialog(getActivity(), "Success", "Inventory saved in offline.");
            messageDialog.messageSuccess();
        }
        super.onResume();
    }

    public void transactionPhysicalCount(String viewType) {
        transactionItemsSQL = new TransactionItemsSQL(getActivity());
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.physicalCount(transactionItemsSQL, GlobalVar.customer_code);
        if (dto.size() == 0) {
            lblNoPhysicalCount.setVisibility(View.VISIBLE);
        } else {
            lblNoPhysicalCount.setVisibility(View.GONE);
        }
        transactionItemAdapter = new TransactionItemAdapter(dto, getActivity(), viewType, "physical");
        layoutManager = new LinearLayoutManager(getActivity());
        rvwPhysicalCount.setLayoutManager(layoutManager);
        rvwPhysicalCount.setAdapter(transactionItemAdapter);
    }

    public void transactionDeliveryCount(String viewType) {
        transactionItemsSQL = new TransactionItemsSQL(getActivity());
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.searchItem(transactionItemsSQL, false, "", "", "4", GlobalVar.customer_code, "", "");
        if (dto.size() == 0) {
            lblNoDeliveryCount.setVisibility(View.VISIBLE);
        } else {
            lblNoDeliveryCount.setVisibility(View.GONE);
        }
        transactionItemAdapter = new TransactionItemAdapter(dto, getActivity(), viewType, "delivery");
        layoutManager = new LinearLayoutManager(getActivity());
        rvwDeliveryCount.setLayoutManager(layoutManager);
        rvwDeliveryCount.setAdapter(transactionItemAdapter);
    }

    public void transactionReturnCount(String viewType) {
        transactionItemsSQL = new TransactionItemsSQL(getActivity());
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.searchItem(transactionItemsSQL, false, "", "", "5", GlobalVar.customer_code, "", "");
        if (dto.size() == 0) {
            lblNoReturnCount.setVisibility(View.VISIBLE);
        } else {
            lblNoReturnCount.setVisibility(View.GONE);
        }
        transactionItemAdapter = new TransactionItemAdapter(dto, getActivity(), viewType, "return");
        layoutManager = new LinearLayoutManager(getActivity());
        rvwReturnCount.setLayoutManager(layoutManager);
        rvwReturnCount.setAdapter(transactionItemAdapter);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getId()) {
            case R.id.rgOption:
                if (i == R.id.rbListView) {
                    lnrPhysicalColumn.setVisibility(View.GONE);
                    lnrDeliveryColumn.setVisibility(View.GONE);
                    lnrReturnColumn.setVisibility(View.GONE);

                    rvwPhysicalCount.setAdapter(null);
                    rvwDeliveryCount.setAdapter(null);
                    rvwReturnCount.setAdapter(null);

                    transactionPhysicalCount("list");
                    transactionDeliveryCount("list");
                    transactionReturnCount("list");
                } else if (i == R.id.rbTableView) {
                    lnrPhysicalColumn.setVisibility(View.VISIBLE);
                    lnrDeliveryColumn.setVisibility(View.VISIBLE);
                    lnrReturnColumn.setVisibility(View.VISIBLE);

                    rvwPhysicalCount.setAdapter(null);
                    rvwDeliveryCount.setAdapter(null);
                    rvwReturnCount.setAdapter(null);

                    transactionPhysicalCount("table");
                    transactionDeliveryCount("table");
                    transactionReturnCount("table");
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhysicalCount:
                startActivity(new Intent(getActivity(), AddPhysicalCount.class));
                break;
            case R.id.btnAddDeliveryCount:
                startActivity(new Intent(getActivity(), AddDeliveryCount.class));
                break;
            case R.id.btnAddReturn:
                startActivity(new Intent(getActivity(), AddReturnCount.class));
                break;
            case R.id.btnSubmit:

                if (lblNoPhysicalCount.getVisibility() == View.GONE) {
                    if (totalPhysicalCount() == totalCarriedMaterial()) {
                        //gps code
//                        GPSLocator gpsLocator;
//                        gpsLocator = new GPSLocator(getActivity());
//                        gpsLocator.getCoordinates(new GPSLocator.dataCallback() {
//                            @Override
//                            public void location(String lat, String lng) {
//                                checkLocation(GlobalVar.customer_code, lat, lng, GlobalVar.customer_name);
//                            }
//                        });
                        getTransactionNumber(GlobalVar.user_id, GlobalVar.customer_code, GlobalVar.customer_id);
                    } else {
                        noPhysicalCount();
                        messageDialog = new MessageDialog(getActivity(), "", "All carried materials must have physical count." + "(Carried: " + totalCarriedMaterial() + ")(Physical Count: " + totalPhysicalCount() + ")(No Count: " + (totalCarriedMaterial() - totalPhysicalCount()));
                        messageDialog.messageError();
                    }
                } else {
                    messageDialog = new MessageDialog(getActivity(), "", "No physical count.");
                    messageDialog.messageError();
                }
                break;
        }
    }

    public Integer totalCarriedMaterial() {
        materialSQL = new MaterialSQL(getActivity());
        ArrayList<MaterialSQL.MaterialDTO> dto = materialSQL.searchItem(materialSQL, "");
        return dto.size();
    }

    public Integer totalPhysicalCount() {
        transactionItemsSQL = new TransactionItemsSQL(getActivity());
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.totalPhysicalCount(transactionItemsSQL, GlobalVar.customer_code);
        return dto.size();
    }

    public void noPhysicalCount() {
        ArrayList<String> materialCode = new ArrayList<>();
        String materialParams = "";
        materialCode.clear();
        transactionItemsSQL = new TransactionItemsSQL(getActivity());
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.totalPhysicalCount(transactionItemsSQL, GlobalVar.customer_code);
        int count = 0;
        while (count < dto.size()) {
            materialCode.add(dto.get(count).getMaterial_code());
            materialParams = materialParams + "?,";
            count++;
        }

        materialParams = materialParams.substring(0, materialParams.length() - 1);
        String[] args = Arrays.copyOf(materialCode.toArray(), materialCode.toArray().length, String[].class);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_no_physical_count, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        ListView lvwNoPhysicalCount;

        lvwNoPhysicalCount = promptView.findViewById(R.id.lvwNoPhysicalCount);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        NoPhysicalCountAdapter noPhysicalCountAdapter;
        materialSQL = new MaterialSQL(getActivity());
        ArrayList<MaterialSQL.MaterialDTO> dto_no = materialSQL.noPhysicalCount(materialSQL, args, materialParams);

        noPhysicalCountAdapter = new NoPhysicalCountAdapter(getActivity(), dto_no);
        lvwNoPhysicalCount.setAdapter(noPhysicalCountAdapter);
    }

//    public void checkLocation(final String customer_code, final String lat, final String lng, final String customer_name) {
//        String url = getActivity().getString(R.string.host) + getActivity().getString(R.string.location) + GlobalVar.api_token;
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (response.toString().equals("success")) {
//                    getTransactionNumber(GlobalVar.user_id, GlobalVar.customer_code, GlobalVar.customer_id);
//                    //Toast.makeText(getActivity(), GlobalVar.user_id+" / " +GlobalVar.customer_code+" / "+GlobalVar.customer_id, Toast.LENGTH_SHORT).show();
//                } else if (response.toString().equals("failed")) {
//                    messageDialog = new MessageDialog(getActivity(), "", "You are not in " + customer_name + " area.");
//                    messageDialog.messageError();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //messageDialog = new MessageDialog(getActivity(), "Error", "No network connectivity.");
//                //messageDialog.messageError();
//                offlineSave();
//                error.printStackTrace();
//            }
//        }) {
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("customer_code", customer_code);
//                params.put("lat", lat);
//                params.put("lng", lng);
//                return params;
//            }
//        };
//        AppContoller.getmInstance(getActivity()).addRequestQue(stringRequest);
//    }

    public void getTransactionNumber(final String merchandiser_id, final String customer_code, final String customer_id) {
        waitingDialog = new WaitingDialog(getActivity());
        waitingDialog.openDialog();
        String url = getActivity().getString(R.string.host) + getActivity().getString(R.string.transaction_number) + GlobalVar.api_token;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ConfirmSubmit.transaction_number = response.toString();
                startActivity(new Intent(getActivity(), ConfirmSubmit.class));
                waitingDialog.closeDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //messageDialog = new MessageDialog(getActivity(), "Error", "No network connectivity.");
                //messageDialog.messageError();
                waitingDialog.closeDialog();
                offlineSave();
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("merchandiser_id", merchandiser_id);
                params.put("customer_code", customer_code);
                params.put("customer_id", customer_id);
                return params;
            }
        };
        AppContoller.getmInstance(getActivity()).addRequestQue(stringRequest);
    }

    public void offlineSave()
    {
        sweetAlertDialog= new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setContentText("Do you want to save in offline mode?");

        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitle("Network is unreachable.");
        sweetAlertDialog.setCancelText("Cancel");
        sweetAlertDialog.setConfirmText("Confirm");

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
            {
                sweetAlertDialog.cancel();
                offlineInventoryHeaderSQL=new OfflineInventoryHeaderSQL(getActivity());
                offlineInventoryHeaderSQL.insertRecord(offlineInventoryHeaderSQL,GlobalVar.customer_code,GlobalVar.customer_id,GlobalVar.customer_name, GlobalVar.schedule_id,GlobalVar.getCurrentDate(0),GlobalVar.getCurrentDateTime());
                getActivity().onBackPressed();
                messageDialog = new MessageDialog(getActivity(), "Success", "Inventory saved in offline.");
                messageDialog.messageSuccess();

            }
        });

        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
            }
        });
        sweetAlertDialog.show();
    }
}
