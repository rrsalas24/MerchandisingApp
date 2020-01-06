package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 3/1/2019.
 */

public class OfflineInventoryHeaderSQL extends DBHelper
{
    public static final String FIELD_CUSTOMER_CODE = "customer_code";
    public static final String FIELD_CUSTOMER_ID= "customer_id";
    public static final String FIELD_CUSTOMER_NAME= "customer_name";
    public static final String FIELD_SCHEDULE_ID= "schedule_id";
    public static final String FIELD_DATE= "date";
    public static final String FIELD_DATE_TIME= "date_time";

    public static final String TBL_NAME= "offline_inventory_header";
    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_CUSTOMER_CODE+" TEXT NOT NULL," +
                    " "+FIELD_CUSTOMER_ID + " TEXT NOT NULL," +
                    " "+FIELD_CUSTOMER_NAME + " TEXT NOT NULL," +
                    " "+FIELD_SCHEDULE_ID + " TEXT NOT NULL," +
                    " "+FIELD_DATE + " TEXT NOT NULL," +
                    " "+FIELD_DATE_TIME + " TEXT NOT NULL" +");";

    public OfflineInventoryHeaderSQL(Context context)
    {
        super(context);
    }

    public boolean deleteOffline(OfflineInventoryHeaderSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_CUSTOMER_CODE +"=?" ,new String[] {customer_code})>0)
        {
            return  true;
        }
        return  false;
    }

    public long insertRecord(OfflineInventoryHeaderSQL obj , String...params)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(FIELD_CUSTOMER_CODE,params[0]);
        contentValues.put(FIELD_CUSTOMER_ID,params[1]);
        contentValues.put(FIELD_CUSTOMER_NAME,params[2]);
        contentValues.put(FIELD_SCHEDULE_ID,params[3]);
        contentValues.put(FIELD_DATE,params[4]);
        contentValues.put(FIELD_DATE_TIME,params[5]);
        return myDb.insert(TBL_NAME,null,contentValues);
    }

    public ArrayList<OfflineInventoryHeaderDTO> offlineList(OfflineInventoryHeaderSQL obj)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,null,null,null,null,null,null);
        OfflineInventoryHeaderDTO dto;
        ArrayList<OfflineInventoryHeaderDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new OfflineInventoryHeaderDTO();
            dto.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            dto.setCustomer_id(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_ID)));
            dto.setCustomer_name(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_NAME)));
            dto.setSchedule_id(cursor.getString(cursor.getColumnIndex(FIELD_SCHEDULE_ID)));
            dto.setDate(cursor.getString(cursor.getColumnIndex(FIELD_DATE)));
            dto.setDate_time(cursor.getString(cursor.getColumnIndex(FIELD_DATE_TIME)));
            list.add(dto);
        }
        return list;
    }

    public ArrayList<OfflineInventoryHeaderDTO> searchItem(OfflineInventoryHeaderSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE +" = ?",new String[] {customer_code},null,null,null,null);
        OfflineInventoryHeaderDTO dto;
        ArrayList<OfflineInventoryHeaderDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new OfflineInventoryHeaderDTO();
            dto.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            dto.setCustomer_id(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_ID)));
            dto.setCustomer_name(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_NAME)));
            dto.setSchedule_id(cursor.getString(cursor.getColumnIndex(FIELD_SCHEDULE_ID)));
            dto.setDate(cursor.getString(cursor.getColumnIndex(FIELD_DATE)));
            dto.setDate_time(cursor.getString(cursor.getColumnIndex(FIELD_DATE_TIME)));
            list.add(dto);
        }
        return list;
    }

    public class OfflineInventoryHeaderDTO
    {
        private String customer_code, customer_id, schedule_id, date, date_time, customer_name;

        public String getCustomer_code() {
            return customer_code;
        }


        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public String getSchedule_id() {
            return schedule_id;
        }

        public void setSchedule_id(String schedule_id) {
            this.schedule_id = schedule_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate_time() {
            return date_time;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }
    }
}
