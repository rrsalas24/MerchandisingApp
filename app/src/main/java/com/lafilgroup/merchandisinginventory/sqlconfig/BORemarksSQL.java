package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/8/2018.
 */

public class BORemarksSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_REMARKS= "remarks";
    public static final String TBL_NAME= "bo_remarks";
    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY," +
                    " "+FIELD_REMARKS + " TEXT NOT NULL" +");";

    public BORemarksSQL(Context context) {
        super(context);
    }

    public long insertRecord(BORemarksSQL obj, String...params)
    {
        SQLiteDatabase mydb=obj.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(FIELD_ID,params[0]);
        cv.put(FIELD_REMARKS,params[1]);
        return mydb.insert(TBL_NAME,null,cv);
    }

    public boolean deleteRecord(BORemarksSQL obj)
    {
        SQLiteDatabase mydb =obj.getWritableDatabase();
        if (mydb.delete(TBL_NAME,null,null)>0)
        {
            return  true;
        }
        return  false;
    }

    public ArrayList<BORemarksDTO> searchItem(BORemarksSQL obj)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,null,null,null,null,FIELD_REMARKS,null);
        BORemarksDTO dto;
        ArrayList<BORemarksDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new BORemarksDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            list.add(dto);
        }
        return list;
    }

    public class BORemarksDTO
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
