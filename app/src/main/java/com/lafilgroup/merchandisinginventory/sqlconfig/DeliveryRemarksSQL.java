package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 9/13/2018.
 */

public class DeliveryRemarksSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_REMARKS= "remarks";
    public static final String TBL_NAME= "delivery_remarks";

    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY," +
                    " "+FIELD_REMARKS + " TEXT NOT NULL" +");";
    public DeliveryRemarksSQL(Context context) {
        super(context);
    }

    public long insertRecord(DeliveryRemarksSQL obj, String...params)
    {
        SQLiteDatabase mydb=obj.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(FIELD_ID,params[0]);
        cv.put(FIELD_REMARKS,params[1]);
        return mydb.insert(TBL_NAME,null,cv);
    }

    public boolean deleteRecord(DeliveryRemarksSQL obj)
    {
        SQLiteDatabase mydb =obj.getWritableDatabase();
        if (mydb.delete(TBL_NAME,null,null)>0)
        {
            return  true;
        }
        return  false;
    }

    public ArrayList<DeliveryRemarksDTO> searchItem(DeliveryRemarksSQL obj)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,null,null,null,null,FIELD_REMARKS,null);
        DeliveryRemarksDTO dto;
        ArrayList<DeliveryRemarksDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new DeliveryRemarksDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            list.add(dto);
        }
        return list;
    }

    public class DeliveryRemarksDTO
    {
        private int id;
        private String remarks;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
