package com.lafilgroup.merchandisinginventory.stockinventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanBarcode extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    MaterialSQL materialSQL;
    public static String count_type;
    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        this.setTitle("Scan Barcode");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        scanBarcode();
    }

    private  void scanBarcode()
    {
        ZXingScannerView scannerView;
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.setCameraDistance(10);
        scannerView.startCamera();
        scannerView.clearAnimation();
    }

    @Override
    public void handleResult(Result result)
    {
        if (count_type.equals("physical_count"))
        {
            AddPhysicalCount.uomList.clear();
        }
        else if (count_type.equals("delivery_count"))
        {
            AddDeliveryCount.uomList.clear();
        }
        else if (count_type.equals("return_count"))
        {
            AddReturnCount.uomList.clear();
        }


        materialSQL=new MaterialSQL(this);

        ArrayList<MaterialSQL.MaterialDTO> dto = materialSQL.getUOM(materialSQL,result.toString());
        if (dto.size()!=0)
        {
            int count=0;
            if (count_type.equals("physical_count"))
            {
                AddPhysicalCount.upc_number=dto.get(0).getUpc_number();
                AddPhysicalCount.material_code=dto.get(0).getMaterial_code();
                AddPhysicalCount.description=dto.get(0).getMaterial_description();
                AddPhysicalCount.alternative_unit =dto.get(0).getAlternative_unit();
            }
            else if (count_type.equals("delivery_count"))
            {
                AddDeliveryCount.upc_number=dto.get(0).getUpc_number();
                AddDeliveryCount.material_code=dto.get(0).getMaterial_code();
                AddDeliveryCount.description=dto.get(0).getMaterial_description();
                AddDeliveryCount.alternative_unit =dto.get(0).getAlternative_unit();
            }
            else if (count_type.equals("return_count"))
            {
                AddReturnCount.upc_number=dto.get(0).getUpc_number();
                AddReturnCount.material_code=dto.get(0).getMaterial_code();
                AddReturnCount.description=dto.get(0).getMaterial_description();
                AddReturnCount.alternative_unit =dto.get(0).getAlternative_unit();
            }


            while (count<dto.size())
            {
                if (count_type.equals("physical_count"))
                {
                    AddPhysicalCount.uomList.add(dto.get(count).getAlternative_unit());
                }
                else if (count_type.equals("delivery_count"))
                {
                    AddDeliveryCount.uomList.add(dto.get(count).getAlternative_unit());
                }
                else if (count_type.equals("return_count"))
                {
                    AddReturnCount.uomList.add(dto.get(count).getAlternative_unit());
                }
                count++;
            }
        }
        else
        {
            messageDialog=new MessageDialog(this,"Error","Item not found.");
            messageDialog.messageError();
        }
        this.finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }
}
