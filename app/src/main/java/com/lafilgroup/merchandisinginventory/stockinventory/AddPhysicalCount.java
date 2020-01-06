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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.BORemarksSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.InventoryTypeSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialBalanceSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionExpirationSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AddPhysicalCount extends AppCompatActivity implements View.OnClickListener
{
    public static String upc_number,material_code,description,alternative_unit;
    public static ArrayList<String> uomList=new ArrayList<>();

    String uom, location, location_code, remarks;

    Button btnScanBarcode, btnSearchItem, btnAddExpiration,btnSave;
    TextView lblBarcode,lblMaterialCode,lblDescription;
    EditText txtQTYCounted;
    Spinner spnLocation, spnUOM, spnRemarks;
    LinearLayout lnrRemarks;
    CheckBox chOutOfStock;

    InventoryTypeSQL inventoryTypeSQL;
    BORemarksSQL boRemarksSQL;
    MaterialSQL materialSQL;

    //==========for location======================
    ArrayList<String> typeList=new ArrayList<>();
    ArrayList<String> idList=new ArrayList<>();

    //==========for remarks======================
    ArrayList<String> remarksID=new ArrayList<>();
    ArrayList<String> remarksType=new ArrayList<>();

    //=========for location, uom, remarks details=======
    ArrayAdapter adapterLocation;
    ArrayAdapter adapterUOM;
    ArrayAdapter adapterRemarks;

    //========for expiration======================
    public static RecyclerView rvwExpiration;
    public static RecyclerView.LayoutManager layoutManager;
    public static ArrayList<String> expirationDate=new ArrayList<>();
    public static ArrayList<String> expirationQTY=new ArrayList<>();
    public static ArrayList<ExpirationDetailsDTO> arrayList=new ArrayList<>();
    public static ExpirationAdapter expirationAdapter;

    TransactionItemsSQL transactionItemsSQL;
    TransactionExpirationSQL transactionExpirationSQL;
    MaterialBalanceSQL materialBalanceSQL;

    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_physical_count);
        this.setTitle("Add Physical Count");
        ExpirationAdapter.remove_type="add_physical";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        castObj();
        spinnerLocation();

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

        spnLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                location=typeList.get(i).toString();
                location_code=idList.get(i).toString();

                if (idList.get(i).toString().equals("3"))
                {
                    selectRemarks();
                }
                else
                {
                   lnrRemarks.setVisibility(View.GONE);
                   remarks="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void castObj()
    {
        lblBarcode=findViewById(R.id.lblBarcode);
        lblMaterialCode=findViewById(R.id.lblMaterialCode);
        lblDescription=findViewById(R.id.lblDescription);

        txtQTYCounted=findViewById(R.id.txtQTYCounted);

        btnScanBarcode=findViewById(R.id.btnScanBarcode);
        btnSearchItem=findViewById(R.id.btnSearchItem);
        btnAddExpiration=findViewById(R.id.btnAddExpiration);
        btnSave=findViewById(R.id.btnSave);
        spnLocation=findViewById(R.id.spnLocation);
        spnUOM=findViewById(R.id.spnUOM);
        spnRemarks=findViewById(R.id.spnRemarks);

        lnrRemarks=findViewById(R.id.lnrRemarks);
        rvwExpiration=findViewById(R.id.rvwExpiration);
        chOutOfStock=findViewById(R.id.chOutOfStock);

        btnScanBarcode.setOnClickListener(this);
        btnSearchItem.setOnClickListener(this);
        btnAddExpiration.setOnClickListener(this);

        btnSave.setOnClickListener(this);
        chOutOfStock.setOnClickListener(this);
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

    public void spinnerLocation()
    {
        inventoryTypeSQL=new InventoryTypeSQL(this);
        ArrayList<InventoryTypeSQL.InventoryTypeDTO> dto=inventoryTypeSQL.searchItem(inventoryTypeSQL,true,"");

        int count =0;
        while (count<dto.size())
        {
            typeList.add(dto.get(count).getType());
            idList.add(dto.get(count).getId()+"");
            count++;
        }
        adapterLocation=new ArrayAdapter(AddPhysicalCount.this,R.layout.support_simple_spinner_dropdown_item,typeList);
        spnLocation.setAdapter(adapterLocation);
    }

    public void selectRemarks()
    {
        remarksID.clear();
        remarksType.clear();
        lnrRemarks.setVisibility(View.VISIBLE);
        adapterRemarks=null;
        spinnerRemarks();
        spnRemarks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                remarks=remarksType.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void spinnerRemarks()
    {
        boRemarksSQL=new BORemarksSQL(this);
        ArrayList<BORemarksSQL.BORemarksDTO> dto=boRemarksSQL.searchItem(boRemarksSQL);

        int count =0;
        while (count<dto.size())
        {
            remarksID.add(dto.get(count).getId()+"");
            remarksType.add(dto.get(count).getRemarks());
            count++;
        }
        adapterRemarks=new ArrayAdapter(AddPhysicalCount.this,R.layout.support_simple_spinner_dropdown_item,remarksType);
        spnRemarks.setAdapter(adapterRemarks);
    }

    public void spinnerUOM()
    {
        adapterUOM=new ArrayAdapter(AddPhysicalCount.this,R.layout.support_simple_spinner_dropdown_item,uomList);
        spnUOM.setAdapter(adapterUOM);
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
        switch (view.getId())
        {
            //==========scan barcode================================================
            case R.id.btnScanBarcode:
                ScanBarcode.count_type="physical_count";
                startActivity(new Intent(this, ScanBarcode.class));
                break;
            //==========search item================================================
            case R.id.btnSearchItem:
                SearchItem.count_type="physical_count";
                startActivity(new Intent(this, SearchItem.class));
                break;
            //==========add expiration details================================================
            case R.id.btnAddExpiration:
                if (!lblDescription.getText().toString().equals(""))
                {
                    LayoutInflater layoutInflater = LayoutInflater.from(AddPhysicalCount.this);
                    View promptView = layoutInflater.inflate(R.layout.dialog_add_expiration,null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddPhysicalCount.this);
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
                            datePickerDialog = new DatePickerDialog(AddPhysicalCount.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener()
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
                                messageDialog=new MessageDialog(AddPhysicalCount.this,"","Kindly input expiration date and quantity.");
                                messageDialog.messageError();
                            }
                        }
                    });
                }
                else
                {
                    messageDialog=new MessageDialog(AddPhysicalCount.this,"","No item selected.");
                    messageDialog.messageError();
                }
                break;
            //==========save inventory================================================
            case R.id.btnSave:

                transactionItemsSQL = new TransactionItemsSQL(this);
                transactionExpirationSQL = new TransactionExpirationSQL(this);
                materialBalanceSQL = new MaterialBalanceSQL(this);

                Integer total_physical_quantity = transactionItemsSQL.totalPhysicalQuantity(transactionItemsSQL, GlobalVar.customer_code, lblMaterialCode.getText().toString());
                Integer total_delivery_quantity = transactionItemsSQL.totalDeliveryQuantity(transactionItemsSQL, GlobalVar.customer_code, lblMaterialCode.getText().toString());
                Integer total_return_quantity = transactionItemsSQL.totalReturnQuantity(transactionItemsSQL, GlobalVar.customer_code, lblMaterialCode.getText().toString());
                Integer total_beginning_quantity = materialBalanceSQL.totalBalanceQuantity(materialBalanceSQL, lblMaterialCode.getText().toString(),GlobalVar.customer_code);

                Integer outstanding_balance = total_beginning_quantity + total_delivery_quantity - total_return_quantity;
                //Toast.makeText(this, total_physical_quantity+"-"+total_delivery_quantity+"-"+total_return_quantity+"-"+total_beginning_quantity, Toast.LENGTH_SHORT).show();


                if (!lblDescription.getText().toString().equals(""))
                {
                    if (!txtQTYCounted.getText().toString().equals(""))
                    {
                        if ((total_physical_quantity + getBaseQTY()) <= (outstanding_balance))
                        {
                            if (!(txtQTYCounted.getText().toString().equals("0") && !chOutOfStock.isChecked()))
                            {
                                if (Integer.parseInt(txtQTYCounted.getText().toString()) == expirationSumQTY())
                                {
                                    ArrayList<TransactionItemsSQL.TransactionItemDTO> dto = transactionItemsSQL.searchItem(transactionItemsSQL, true, lblMaterialCode.getText().toString(), uom, location_code, GlobalVar.customer_code, "", "");
                                    if (dto.size() <= 0)
                                    {
                                        transactionItemsSQL.insertRecord(transactionItemsSQL, lblBarcode.getText().toString(),
                                                lblMaterialCode.getText().toString(), lblDescription.getText().toString(),
                                                location_code, location, uom, txtQTYCounted.getText().toString(), getBaseUOM(), getBaseQTY() + "", GlobalVar.customer_code, remarks, "", "");

                                        int counter = 0;
                                        int ex_base_qty = 0;
                                        while (counter < expirationDate.size())
                                        {
                                            if (uom.equals(getBaseUOM()))
                                            {
                                                ex_base_qty = Integer.parseInt(expirationQTY.get(counter));
                                            }
                                            else
                                            {
                                                ex_base_qty = Integer.parseInt(expirationQTY.get(counter)) * getMultiplier();
                                            }
                                            transactionExpirationSQL.insertRecord(transactionExpirationSQL, lblMaterialCode.getText().toString(), location_code, uom, expirationQTY.get(counter), getBaseUOM(), ex_base_qty + "", expirationDate.get(counter), GlobalVar.customer_code, "", "");
                                            counter++;
                                        }
                                        StockInventory.entry_added = true;
                                        clearData();
                                        finish();
                                    }
                                    else
                                    {
                                        messageDialog = new MessageDialog(AddPhysicalCount.this, "", "Entry already added.");
                                        messageDialog.messageError();
                                    }
                                }
                                else
                                {
                                    messageDialog = new MessageDialog(AddPhysicalCount.this, "", "QTY counted must be equal to total QTY of expiration details.");
                                    messageDialog.messageError();
                                }
                            }
                            else
                            {
                                messageDialog = new MessageDialog(AddPhysicalCount.this, "", "Kindly check if the item is out of stock.");
                                messageDialog.messageError();
                            }
                        }
                        else
                        {
                            outstandingBalance(getBaseUOM(), total_beginning_quantity, total_delivery_quantity, total_return_quantity, outstanding_balance, total_physical_quantity);
                            messageDialog = new MessageDialog(AddPhysicalCount.this, "", "Entered physical count exceeds remaining inventory balance.");
                            messageDialog.messageError();
                        }
                    }
                    else
                    {
                        messageDialog = new MessageDialog(AddPhysicalCount.this, "", "Kindly input quantity.");
                        messageDialog.messageError();
                    }
                }
                else
                {
                    messageDialog = new MessageDialog(AddPhysicalCount.this, "", "No item selected.");
                    messageDialog.messageError();
                }
                break;

            case R.id.chOutOfStock:
                if (chOutOfStock.isChecked())
                {
                    txtQTYCounted.setEnabled(false);
                    txtQTYCounted.setText("0");
                }
                else
                {
                    txtQTYCounted.setEnabled(true);
                    txtQTYCounted.setText("");
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
