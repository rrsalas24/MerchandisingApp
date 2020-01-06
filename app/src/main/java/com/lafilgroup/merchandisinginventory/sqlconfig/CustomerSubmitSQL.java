package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/17/2018.
 */

public class CustomerSubmitSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_CUSTOMER_CODE= "customer_code";
    public static final String FIELD_DATE= "date";

    public static final String TBL_NAME= "customer_submit";
    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " "+FIELD_CUSTOMER_CODE + " TEXT NOT NULL," +
                    " "+FIELD_DATE + " TEXT NOT NULL" +");";

    public CustomerSubmitSQL(Context context)
    {
        super(context);
    }

    public long insertRecord(CustomerSubmitSQL obj , String...params)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(FIELD_CUSTOMER_CODE,params[0]);
        contentValues.put(FIELD_DATE,params[1]);
        return myDb.insert(TBL_NAME,null,contentValues);
    }

    public boolean deleteRecord(CustomerSubmitSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_CUSTOMER_CODE +"=?" ,new String[] {customer_code})>0)
        {
            return  true;
        }
        return  false;
    }

    public ArrayList<CustomerSubmitDTO> searchItem(CustomerSubmitSQL obj, String customer_code, String date)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE +" = ? and " + FIELD_DATE + " = ?" ,new String[] {customer_code,date},null,null,null,null);
        CustomerSubmitDTO customerSubmitDTO;
        ArrayList<CustomerSubmitDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            customerSubmitDTO=new CustomerSubmitDTO();
            customerSubmitDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            customerSubmitDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            customerSubmitDTO.setDate(cursor.getString(cursor.getColumnIndex(FIELD_DATE)));
            list.add(customerSubmitDTO);
        }
        return list;
    }

    public class CustomerSubmitDTO
    {
        Integer id;
        String customer_code, date;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
