package com.lafilgroup.merchandisinginventory.stockinventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.BORemarksSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.DeliveryRemarksSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionExpirationSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;

import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SuccessTickView;

public class AddDeliveryCount extends AppCompatActivity implements View.OnClickListener
{
    public static String upc_number,material_code,description,alternative_unit;
    public static ArrayList<String> uomList=new ArrayList<>();

    String uom, location, location_code;
    //remarks;

    Button btnScanBarcode, btnSearchItem, btnAddExpiration,btnSave;
    TextView lblBarcode,lblMaterialCode,lblDescription;
    EditText txtQTYCounted, txtDateDelivered, txtRemarks;
    Spinner spnUOM;

    ArrayAdapter adapterUOM;

    //========for expiration======================
    public static RecyclerView rvwExpiration;
    public static RecyclerView.LayoutManager layoutManager;
    public static ArrayList<String> expirationDate=new ArrayList<>();
    public static ArrayList<String> expirationQTY=new ArrayList<>();
    public static ArrayList<ExpirationDetailsDTO> arrayList=new ArrayList<>();
    public static ExpirationAdapter expirationAdapter;

    TransactionItemsSQL transactionItemsSQL;
    TransactionExpirationSQL transactionExpirationSQL;
    MaterialSQL materialSQL;

    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_count);
        this.setTitle("Add Delivery Count");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        castObj();
        ExpirationAdapter.remove_type="add_delivery";
        location_code="4";
        location="Delivery";

        spnUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                uom=adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        clearData();
        finish();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        clearData();
        super.onBackPressed();
    }

    public void clearData()
    {
        upc_number="";
        material_code="";
        description="";
        uomList.clear();
        expirationDate.clear();
        expirationQTY.clear();
        arrayList.clear();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        lblBarcode.setText(upc_number);
        lblMaterialCode.setText(material_code);
        lblDescription.setText(description);
        spinnerUOM();
    }

    public void spinnerUOM()
    {
        adapterUOM=new ArrayAdapter(AddDeliveryCount.this,R.layout.support_simple_spinner_dropdown_item,uomList);
        spnUOM.setAdapter(adapterUOM);
    }

    public void castObj()
    {
        lblBarcode=findViewById(R.id.lblBarcode);
        lblMaterialCode=findViewById(R.id.lblMaterialCode);
        lblDescription=findViewById(R.id.lblDescription);

        txtQTYCounted=findViewById(R.id.txtQTYCounted);
        txtDateDelivered=findViewById(R.id.txtDateDelivered);
        txtRemarks=findViewById(R.id.txtRemarks);

        btnScanBarcode=findViewById(R.id.btnScanBarcode);
        btnSearchItem=findViewById(R.id.btnSearchItem);
        btnAddExpiration=findViewById(R.id.btnAddExpiration);
        btnSave=findViewById(R.id.btnSave);
        spnUOM=findViewById(R.id.spnUOM);

        rvwExpiration=findViewById(R.id.rvwExpiration);
        btnScanBarcode.setOnClickListener(this);
        btnSearchItem.setOnClickListener(this);
        txtDateDelivered.setOnClickListener(this);
        btnAddExpiration.setOnClickListener(this);
        btnSave.setOnClickListener(this);
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            //==========scan barcode================================================
            case R.id.btnScanBarcode:
                ScanBarcode.count_type="delivery_count";
                startActivity(new Intent(this, ScanBarcode.class));
                break;
            //==========search item================================================
            case R.id.btnSearchItem:
                SearchItem.count_type="delivery_count";
                startActivity(new Intent(this, SearchItem.class));
                break;
            //=========select date delivered=======================================
            case R.id.txtDateDelivered:
                DatePickerDialog datePickerDialog;
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(AddDeliveryCount.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        String d=i +"-"  + (i1+1) +"-" +i2;
                        txtDateDelivered.setText(GlobalVar.toDateToString(d));
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }, year, month, day);
                datePickerDialog.setTitle("Select Delivery Date");
                datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Select",datePickerDialog);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                break;
            //===============
            case R.id.btnAddExpiration:
                if (!lblDescription.getText().toString().equals(""))
                {
                    LayoutInflater layoutInflater = LayoutInflater.from(AddDeliveryCount.this);
                    View promptView = layoutInflater.inflate(R.layout.dialog_add_expiration,null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddDeliveryCount.this);
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
                            datePickerDialog = new DatePickerDialog(AddDeliveryCount.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener()
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
                                messageDialog=new MessageDialog(AddDeliveryCount.this,"","Kindly input expiration date and quantity.");
                                messageDialog.messageError();
                            }
                        }
                    });
                }
                else
                {
                    messageDialog=new MessageDialog(this,"","No item selected.");
                    messageDialog.messageError();
                }
                break;
            case R.id.btnSave:
                transactionItemsSQL = new TransactionItemsSQL(this);
                transactionExpirationSQL = new TransactionExpirationSQL(this);

                if (!lblDescription.getText().toString().equals(""))
                {
                    if (!txtDateDelivered.getText().toString().equals(""))
                    {
                        if (!txtQTYCounted.getText().toString().equals(""))
                        {
                            //if (Integer.parseInt(txtQTYCounted.getText().toString()) == expirationSumQTY())
                            //{
                            ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.searchItem(transactionItemsSQL, true, lblMaterialCode.getText().toString(), uom, location_code, GlobalVar.customer_code, txtDateDelivered.getText().toString(), "");
                            if (dto.size() <= 0)
                            {
                                transactionItemsSQL.insertRecord(transactionItemsSQL, lblBarcode.getText().toString(),
                                        lblMaterialCode.getText().toString(), lblDescription.getText().toString(),
                                        location_code, location, uom, txtQTYCounted.getText().toString(), getBaseUOM(), getBaseQTY() + "", GlobalVar.customer_code, txtRemarks.getText().toString(), txtDateDelivered.getText().toString(), "");
                                //int counter = 0;
                                //while (counter < expirationDate.size()) {
                                //transactionExpirationSQL.insertRecord(transactionExpirationSQL, lblMaterialCode.getText().toString(), uom, location_code, expirationQTY.get(counter), expirationDate.get(counter), GlobalVar.customer_code, txtDateDelivered.getText().toString(), "");
                                //counter++;
                                // }

                                StockInventory.entry_added = true;
                                clearData();
                                finish();
                            }
                            else
                            {
                                messageDialog=new MessageDialog(this,"","Entry already added.");
                                messageDialog.messageError();
                            }
                            //}
                            //else
                            //{
                                //messageDialog=new MessageDialog(this,"","QTY counted must be equal to total QTY of expiration details.");
                                //messageDialog.messageError();
                            //}
                        }
                        else
                        {
                            messageDialog=new MessageDialog(this,"","Kindly input quantity.");
                            messageDialog.messageError();
                        }
                    }
                    else
                    {
                        messageDialog=new MessageDialog(this,"","No date delivered selected.");
                        messageDialog.messageError();
                    }
                }
                else
                {
                    messageDialog=new MessageDialog(this,"","No item selected.");
                    messageDialog.messageError();
                }
                break;
        }
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
        if (uom.equals(dto.get(0).getBase_unit()))
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
        ArrayList<MaterialSQL.MaterialDTO> dto=materialSQL.getMultiplier(materialSQL,lblMaterialCode.getText().toString(),uom);
        return dto.get(0).getNumerator()/dto.get(0).getDenominator();
    }
}
