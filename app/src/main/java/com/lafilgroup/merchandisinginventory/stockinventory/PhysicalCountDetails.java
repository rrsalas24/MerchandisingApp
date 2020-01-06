package com.lafilgroup.merchandisinginventory.stockinventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lafilgroup.merchandisinginventory.Login;
import com.lafilgroup.merchandisinginventory.MainActivity;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.sendfeedback.SendFeedback;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialBalanceSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionExpirationSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;

import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PhysicalCountDetails extends AppCompatActivity
{
    public static Integer transaction_items_id;
    String location_code;
    TextView lblBarcode, lblMaterialCode, lblDescription, lblUOM, lblLocation, lblRemarks, lblLoc;
    EditText txtQTYCounted,txtDateDelivered, txtDateReturned;
    LinearLayout lnrRemarks,lnrDateDelivered, lnrDateReturned, lnrColumn;
    Button btnAddExpiration, btnSave;
    MessageDialog messageDialog;

    //=======for expiration======================
    public static RecyclerView rvwExpiration;
    public static RecyclerView.LayoutManager layoutManager;
    public static ArrayList<String> expirationDate=new ArrayList<>();
    public static ArrayList<String> expirationQTY=new ArrayList<>();
    public static ArrayList<ExpirationDetailsDTO> arrayList=new ArrayList<>();
    public static ExpirationAdapter expirationAdapter;

    TransactionItemsSQL transactionItemsSQL;
    TransactionExpirationSQL transactionExpirationSQL;
    MaterialSQL materialSQL;
    MaterialBalanceSQL materialBalanceSQL;

    Integer inputted_base_qty;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_count_details);
        this.setTitle("Update");
        ExpirationAdapter.remove_type="edit_physical";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        castObj();
        itemDetails();
        getExpiration();
        expirationDetails();

        //===========add expiration=====================
        btnAddExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LayoutInflater layoutInflater = LayoutInflater.from(PhysicalCountDetails.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_add_expiration,null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PhysicalCountDetails.this);
                alertDialogBuilder.setView(promptView);

                final EditText txtDate,txtQTY;

                txtDate = promptView.findViewById(R.id.txtDate);
                txtQTY =  promptView.findViewById(R.id.txtQTY);

                txtDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog;
                        final Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        datePickerDialog = new DatePickerDialog(PhysicalCountDetails.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                            {
                                String d=i +"-"  + (i1+1) +"-" +i2;
                                txtDate.setText(GlobalVar.toDateToString(d));
                                txtQTY.requestFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        }, year, month, day);
                        datePickerDialog.setTitle("Select Expiration Date");
                        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Select",datePickerDialog);
                        datePickerDialog.setCancelable(false);
                        datePickerDialog.show();
                    }
                });
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

                final AlertDialog alert = alertDialogBuilder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if (!txtDate.getText().toString().equals("") && !txtQTY.getText().toString().equals(""))
                        {
                            expirationDate.add(txtDate.getText().toString());
                            expirationQTY.add(txtQTY.getText().toString());
                            expirationDetails();
                            alert.dismiss();
                        }
                        else
                        {
                            messageDialog=new MessageDialog(PhysicalCountDetails.this,"","Kindly input expiration date and quantity.");
                            messageDialog.messageError();
                            //Toast.makeText(PhysicalCountDetails.this, "Kindly input expiration date and quantity.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                transactionItemsSQL = new TransactionItemsSQL(PhysicalCountDetails.this);
                transactionExpirationSQL = new TransactionExpirationSQL(PhysicalCountDetails.this);
                materialBalanceSQL=new MaterialBalanceSQL(PhysicalCountDetails.this);

                Integer total_physical_quantity= transactionItemsSQL.totalPhysicalQuantity(transactionItemsSQL,GlobalVar.customer_code,lblMaterialCode.getText().toString());
                Integer total_delivery_quantity= transactionItemsSQL.totalDeliveryQuantity(transactionItemsSQL,GlobalVar.customer_code,lblMaterialCode.getText().toString());
                Integer total_return_quantity= transactionItemsSQL.totalReturnQuantity(transactionItemsSQL,GlobalVar.customer_code,lblMaterialCode.getText().toString());
                Integer total_beginning_quantity=materialBalanceSQL.totalBalanceQuantity(materialBalanceSQL,lblMaterialCode.getText().toString(),GlobalVar.customer_code);

                Integer outstanding_balance=total_beginning_quantity+total_delivery_quantity-total_return_quantity;

                if (!txtQTYCounted.getText().toString().equals(""))
                {
                    //===============update delivery===============================================================================================================
                    if (location_code.equals("4"))
                    {
                        if ((total_physical_quantity) <= (outstanding_balance-inputted_base_qty)+getBaseQTY())
                        {
                            transactionExpirationSQL.deleteOneRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString(),location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                            transactionItemsSQL.updateRecord(transactionItemsSQL,transaction_items_id,Integer.parseInt(txtQTYCounted.getText().toString()),getBaseQTY());
                            StockInventory.entry_update=true;
                            clearData();
                            finish();
                        }
                        else
                        {
                            outstandingBalance(getBaseUOM(),total_beginning_quantity,total_delivery_quantity,total_return_quantity, outstanding_balance, total_physical_quantity);
                            messageDialog=new MessageDialog(PhysicalCountDetails.this,"","Unable to update. Outstanding balance must be equal or greater than in physical count.");
                            messageDialog.messageError();
                        }
                    }
                    else
                    {
                        if (Integer.parseInt(txtQTYCounted.getText().toString()) == expirationSumQTY())
                        {
                            //===============update return===============================================================================================================
                            if (location_code.equals("5"))
                            {
                                if ((total_physical_quantity) <= (outstanding_balance+inputted_base_qty)- getBaseQTY())
                                {
                                    transactionExpirationSQL.deleteOneRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString(),location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                                    transactionItemsSQL.updateRecord(transactionItemsSQL,transaction_items_id,Integer.parseInt(txtQTYCounted.getText().toString()),getBaseQTY());
                                    int ex_base_qty=0;
                                    int counter=0;
                                    while (counter<expirationDate.size())
                                    {
                                        if (lblUOM.getText().toString().equals(getBaseUOM()))
                                        {
                                            ex_base_qty=Integer.parseInt(expirationQTY.get(counter));
                                        }
                                        else
                                        {
                                            ex_base_qty=Integer.parseInt(expirationQTY.get(counter))*getMultiplier();
                                        }
                                        transactionExpirationSQL.insertRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(), location_code, lblUOM.getText().toString(), expirationQTY.get(counter), getBaseUOM(),ex_base_qty+"", expirationDate.get(counter),GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                                        counter++;
                                    }
                                    StockInventory.entry_update=true;
                                    clearData();
                                    finish();
                                }
                                else
                                {
                                    outstandingBalance(getBaseUOM(),total_beginning_quantity,total_delivery_quantity,total_return_quantity-inputted_base_qty, outstanding_balance+inputted_base_qty, total_physical_quantity);
                                    messageDialog=new MessageDialog(PhysicalCountDetails.this,"","Entered return count exceeds remaining inventory balance.");
                                    messageDialog.messageError();
                                }
                            }
                            //===============update physical===============================================================================================================
                            else
                            {
                                if ((total_physical_quantity-inputted_base_qty)+getBaseQTY() <= (outstanding_balance))
                                {
                                    transactionExpirationSQL.deleteOneRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString(),location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                                    transactionItemsSQL.updateRecord(transactionItemsSQL,transaction_items_id,Integer.parseInt(txtQTYCounted.getText().toString()),getBaseQTY());
                                    int ex_base_qty=0;
                                    int counter=0;
                                    while (counter<expirationDate.size())
                                    {
                                        if (lblUOM.getText().toString().equals(getBaseUOM()))
                                        {
                                            ex_base_qty=Integer.parseInt(expirationQTY.get(counter));
                                        }
                                        else
                                        {
                                            ex_base_qty=Integer.parseInt(expirationQTY.get(counter))*getMultiplier();
                                        }
                                        transactionExpirationSQL.insertRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(), location_code, lblUOM.getText().toString(), expirationQTY.get(counter), getBaseUOM(),ex_base_qty+"", expirationDate.get(counter),GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                                        counter++;
                                    }
                                    StockInventory.entry_update=true;
                                    clearData();
                                    finish();
                                }
                                else
                                {
                                    outstandingBalance(getBaseUOM(),total_beginning_quantity,total_delivery_quantity,total_return_quantity, outstanding_balance, total_physical_quantity-inputted_base_qty);
                                    messageDialog=new MessageDialog(PhysicalCountDetails.this,"","Entered physical count exceeds remaining inventory balance.");
                                    messageDialog.messageError();
                                }
                            }
                        }
                        else
                        {
                            messageDialog=new MessageDialog(PhysicalCountDetails.this,"","QTY counted must be equal to total QTY of expiration details.");
                            messageDialog.messageError();
                        }
                    }
                }
                else
                {
                    messageDialog=new MessageDialog(PhysicalCountDetails.this,"","Kindly input quantity.");
                    messageDialog.messageError();
                }
            }
        });
    }

    public void clearData()
    {
        expirationDate.clear();
        expirationQTY.clear();
        arrayList.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.items_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.mybutton)
        {
            SweetAlertDialog sweetAlertDialog= new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setContentText("Do you want delete?");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitle("Delete");
            sweetAlertDialog.setCancelText("No");
            sweetAlertDialog.setConfirmText("Yes");

            //========================confirm=================================================
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
            {
                @Override
                public void onClick(SweetAlertDialog sDialog)
                {
                    transactionItemsSQL = new TransactionItemsSQL(PhysicalCountDetails.this);
                    transactionExpirationSQL = new TransactionExpirationSQL(PhysicalCountDetails.this);
                    materialBalanceSQL=new MaterialBalanceSQL(PhysicalCountDetails.this);

                    Integer total_physical_quantity= transactionItemsSQL.totalPhysicalQuantity(transactionItemsSQL,GlobalVar.customer_code,lblMaterialCode.getText().toString());
                    Integer total_delivery_quantity= transactionItemsSQL.totalDeliveryQuantity(transactionItemsSQL,GlobalVar.customer_code,lblMaterialCode.getText().toString());
                    Integer total_return_quantity= transactionItemsSQL.totalReturnQuantity(transactionItemsSQL,GlobalVar.customer_code,lblMaterialCode.getText().toString());
                    Integer total_beginning_quantity=materialBalanceSQL.totalBalanceQuantity(materialBalanceSQL,lblMaterialCode.getText().toString(),GlobalVar.customer_code);

                    Integer outstanding_balance=total_beginning_quantity+total_delivery_quantity-total_return_quantity;

                    if (location_code.equals("4"))
                    {
                        if ((total_physical_quantity) <= (outstanding_balance-inputted_base_qty))
                        {
                            transactionItemsSQL.deleteOneRecord(transactionItemsSQL,transaction_items_id);
                            transactionExpirationSQL.deleteOneRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString(),location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                            StockInventory.entry_delete=true;
                            clearData();
                            finish();
                        }
                        else
                        {
                            outstandingBalance(getBaseUOM(),total_beginning_quantity,total_delivery_quantity,total_return_quantity, outstanding_balance, total_physical_quantity);
                            messageDialog=new MessageDialog(PhysicalCountDetails.this,"","Unable to delete. Outstanding balance must be equal or greater than in physical count.");
                            messageDialog.messageError();
                        }
                    }
                    else
                    {
                        transactionItemsSQL.deleteOneRecord(transactionItemsSQL,transaction_items_id);
                        transactionExpirationSQL.deleteOneRecord(transactionExpirationSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString(),location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());
                        StockInventory.entry_delete=true;
                        clearData();
                        finish();
                    }
                }
            });

            //========close==========================================================
            sweetAlertDialog.showCancelButton(true);
            sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.cancel();
                }
            });
            sweetAlertDialog.show();

        }
        else
        {
            clearData();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        clearData();
        super.onBackPressed();
    }

    public void castObj()
    {
        lblBarcode=findViewById(R.id.lblBarcode);
        lblMaterialCode=findViewById(R.id.lblMaterialCode);
        lblDescription=findViewById(R.id.lblDescription);
        lblUOM=findViewById(R.id.lblUOM);
        lblLocation=findViewById(R.id.lblLocation);
        lblRemarks=findViewById(R.id.lblRemarks);
        lblLoc=findViewById(R.id.lblLoc);
        txtQTYCounted=findViewById(R.id.txtQTYCounted);
        txtDateDelivered=findViewById(R.id.txtDateDelivered);
        txtDateReturned=findViewById(R.id.txtDateReturned);
        lnrRemarks=findViewById(R.id.lnrRemarks);
        lnrDateDelivered=findViewById(R.id.lnrDateDelivered);
        lnrDateReturned=findViewById(R.id.lnrDateReturned);
        btnAddExpiration=findViewById(R.id.btnAddExpiration);
        rvwExpiration=findViewById(R.id.rvwExpiration);
        btnSave=findViewById(R.id.btnSave);
        lnrColumn=findViewById(R.id.lnrColumn);
    }

    public void itemDetails()
    {
        transactionItemsSQL = new TransactionItemsSQL(this);
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.specificDetails(transactionItemsSQL,transaction_items_id);
        lblBarcode.setText(dto.get(0).getBarcode().toString());
        lblMaterialCode.setText(dto.get(0).getMaterial_code().toString());
        lblDescription.setText(dto.get(0).getDescription().toString());
        lblUOM.setText(dto.get(0).getEntry_uom().toString());
        lblLocation.setText(dto.get(0).getLocation().toString());
        txtQTYCounted.setText(dto.get(0).getEntry_qty()+"");
        location_code=dto.get(0).getLocation_code();
        inputted_base_qty=dto.get(0).getBase_qty();

        if (dto.get(0).getLocation_code().equals("3"))
        {
            lnrRemarks.setVisibility(View.VISIBLE);
            lnrDateDelivered.setVisibility(View.GONE);
            lnrDateReturned.setVisibility(View.GONE);
            lblRemarks.setText(dto.get(0).getRemarks().toString());
            lblLoc.setText("Location: ");
            btnAddExpiration.setVisibility(View.VISIBLE);
            lnrColumn.setVisibility(View.VISIBLE);
        }
        else if (dto.get(0).getLocation_code().equals("4"))
        {
            lnrRemarks.setVisibility(View.VISIBLE);
            lnrDateDelivered.setVisibility(View.VISIBLE);
            lnrDateReturned.setVisibility(View.GONE);
            lblLoc.setText("Type: ");
            lblRemarks.setText(dto.get(0).getRemarks().toString());
            txtDateDelivered.setText(dto.get(0).getDelivery_date());
            btnAddExpiration.setVisibility(View.GONE);
            lnrColumn.setVisibility(View.GONE);
        }

        else if (dto.get(0).getLocation_code().equals("5"))
        {
            lnrRemarks.setVisibility(View.VISIBLE);
            lnrDateDelivered.setVisibility(View.GONE);
            lnrDateReturned.setVisibility(View.VISIBLE);
            lblLoc.setText("Type: ");
            lblRemarks.setText(dto.get(0).getRemarks().toString());
            txtDateReturned.setText(dto.get(0).getReturn_date());
            btnAddExpiration.setVisibility(View.VISIBLE);
            lnrColumn.setVisibility(View.VISIBLE);
        }
        else
        {
            lblLoc.setText("Location: ");
            lnrRemarks.setVisibility(View.GONE);
            lnrDateDelivered.setVisibility(View.GONE);
            lnrDateReturned.setVisibility(View.GONE);
            btnAddExpiration.setVisibility(View.VISIBLE);
            lnrColumn.setVisibility(View.VISIBLE);
        }
    }

    public void getExpiration()
    {
        transactionExpirationSQL=new TransactionExpirationSQL(this);
        ArrayList<TransactionExpirationSQL.TransactionExpirationDTO> dto =transactionExpirationSQL.searchItem(transactionExpirationSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString(),location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), txtDateReturned.getText().toString());

        int counter=0;
        while (counter<dto.size())
        {
            expirationDate.add(dto.get(counter).getExpiration_date());
            expirationQTY.add(dto.get(counter).getEntry_qty()+"");
            counter++;
        }
    }

    public void expirationDetails()
    {
        int counter=0;
        arrayList.clear();
        while (counter<expirationDate.size())
        {
            ExpirationDetailsDTO expirationDetailsDTO=new ExpirationDetailsDTO();
            expirationDetailsDTO.setExpiration_id(counter +"");
            expirationDetailsDTO.setExpiration_date(expirationDate.get(counter));
            expirationDetailsDTO.setExpiration_qty(expirationQTY.get(counter));
            arrayList.add(expirationDetailsDTO);
            counter++;
        }
        expirationAdapter=new ExpirationAdapter(arrayList,this);
        layoutManager= new LinearLayoutManager(this);
        rvwExpiration.setLayoutManager(layoutManager);
        rvwExpiration.setAdapter(expirationAdapter);
    }

    public int expirationSumQTY()
    {
        int counter=0;
        int sum=0;
        while (counter<expirationQTY.size())
        {
            sum=sum+ Integer.parseInt(expirationQTY.get(counter));
            counter++;
        }
        return sum;
    }

    public String getBaseUOM()
    {
        materialSQL =new MaterialSQL(this);
        ArrayList<MaterialSQL.MaterialDTO> dto=materialSQL.searchItem(materialSQL,lblMaterialCode.getText().toString());
        return dto.get(0).getBase_unit();
    }

    public Integer getBaseQTY()
    {
        materialSQL =new MaterialSQL(this);
        ArrayList<MaterialSQL.MaterialDTO> dto=materialSQL.searchItem(materialSQL,lblMaterialCode.getText().toString());
        if (lblUOM.getText().toString().equals(dto.get(0).getBase_unit()))
        {
            return Integer.parseInt(txtQTYCounted.getText().toString());
        }
        else
        {
            return (Integer.parseInt(txtQTYCounted.getText().toString())*getMultiplier());
        }
    }

    public Integer getMultiplier()
    {
        materialSQL =new MaterialSQL(this);
        ArrayList<MaterialSQL.MaterialDTO> dto=materialSQL.getMultiplier(materialSQL,lblMaterialCode.getText().toString(),lblUOM.getText().toString());
        return dto.get(0).getNumerator()/dto.get(0).getDenominator();
    }

    public void outstandingBalance(String base_uom, Integer beginning_balance, Integer inputted_delivery, Integer inputted_return, Integer outstanding_balance, Integer physical_qty)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_outstanding,null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        TextView lblBaseUOM, lblBeginningBalance,lblInputtedDelivery,lblInputtedReturn,lblOutstandingBalance, lblInputtedPhysical, lblRemainingBalance;

        lblBaseUOM=promptView.findViewById(R.id.lblBaseUOM);
        lblBeginningBalance=promptView.findViewById(R.id.lblBeginningBalance);
        lblInputtedDelivery=promptView.findViewById(R.id.lblInputtedDelivery);
        lblInputtedReturn=promptView.findViewById(R.id.lblInputtedReturn);
        lblOutstandingBalance=promptView.findViewById(R.id.lblOutstandingBalance);
        lblInputtedPhysical=promptView.findViewById(R.id.lblInputtedPhysical);
        lblRemainingBalance=promptView.findViewById(R.id.lblRemainingBalance);

        lblBaseUOM.setText("Base UOM: " + base_uom);
        lblBeginningBalance.setText("Beginning Balance: " + beginning_balance);
        lblInputtedDelivery.setText("Inputted Delivery: " + inputted_delivery);
        lblInputtedReturn.setText("Inputted Return: " + inputted_return);
        lblOutstandingBalance.setText("Outstanding Balance: " + outstanding_balance);
        lblInputtedPhysical.setText("Inputted Physical Count QTY: " +physical_qty);
        lblRemainingBalance.setText("Remaining Balance: " +((beginning_balance-physical_qty-inputted_return)+inputted_delivery));

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
