package com.lafilgroup.merchandisinginventory.stockinventory;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialSQL;

import java.util.ArrayList;

public class SearchItem extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{
    public static String count_type;
    Button btnSearch;
    EditText txtSearch;
    TextView lblTotal;
    ListView lvwItems;
    MaterialSQL materialSQL;
    SearchItemAdapter searchItemAdapter;
    LinearLayout lnrColumn;

    RadioGroup rgOption;
    RadioButton rbListView,rbTableView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);
        this.setTitle("Search Item");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        castObj();
        rgOption.setOnCheckedChangeListener(this);
        searchItems("","list");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (rbListView.isChecked())
                {
                    searchItems(txtSearch.getText().toString(),"list");
                }
                else if (rbTableView.isChecked())
                {
                    searchItems(txtSearch.getText().toString(),"table");
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    public void castObj()
    {
        btnSearch=findViewById(R.id.btnSearch);
        txtSearch=findViewById(R.id.txtSearch);
        lvwItems=findViewById(R.id.lvwItems);
        lnrColumn=findViewById(R.id.lnrColumn);
        rgOption=findViewById(R.id.rgOption);
        rbListView=findViewById(R.id.rbListView);
        rbTableView=findViewById(R.id.rbTableView);
        lblTotal=findViewById(R.id.lblTotal);
    }

    public void searchItems(String search, final String viewType)
    {
        SearchItemAdapter.viewType=viewType;
        materialSQL=new MaterialSQL(this);
        ArrayList<MaterialSQL.MaterialDTO> dto = materialSQL.searchItem(materialSQL,search);
        searchItemAdapter =new SearchItemAdapter(SearchItem.this,dto);
        lvwItems.setAdapter(searchItemAdapter);
        lblTotal.setText("Total: " + dto.size() + " item(s)");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i)
    {
        switch (radioGroup.getId())
        {
            case R.id.rgOption:

                if (i==R.id.rbListView)
                {
                    lnrColumn.setVisibility(View.GONE);
                    lvwItems.setAdapter(null);
                    lvwItems.setDivider(null);
                    searchItems(txtSearch.getText().toString(),"list");
                }
                else if (i==R.id.rbTableView)
                {
                    lvwItems.setAdapter(null);
                    lnrColumn.setVisibility(View.VISIBLE);
                    ColorDrawable colorDrawable=new ColorDrawable(this.getResources().getColor(R.color.divider));
                    lvwItems.setDivider(colorDrawable);
                    lvwItems.setDividerHeight(1);
                    searchItems(txtSearch.getText().toString(),"table");
                }
                break;
        }
    }
}
