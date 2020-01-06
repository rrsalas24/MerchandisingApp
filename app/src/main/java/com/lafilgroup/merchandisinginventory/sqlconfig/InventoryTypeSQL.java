package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lafilgroup.merchandisinginventory.config.GlobalVar;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 5/7/2018.
 */

public class InventoryTypeSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_TYPE= "type";
    public static final String TBL_NAME= "inventory_type";
    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY," +
                    " "+FIELD_TYPE + " TEXT NOT NULL" +");";

    public InventoryTypeSQL(Context context)
    {
        super(context);
    }


    public long insertRecord(InventoryTypeSQL obj, String...params)
    {
        SQLiteDatabase mydb=obj.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(FIELD_ID,params[0]);
        cv.put(FIELD_TYPE,params[1]);
        return mydb.insert(TBL_NAME,null,cv);
    }

    public boolean deleteRecord(InventoryTypeSQL obj)
    {
        SQLiteDatabase mydb =obj.getWritableDatabase();
        if (mydb.delete(TBL_NAME,null,null)>0)
        {
            return  true;
        }
        return  false;
    }


    public ArrayList<InventoryTypeSQL.InventoryTypeDTO> searchItem(InventoryTypeSQL obj, Boolean physicalCount, String type)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        String[] args = new String[]{"001","002","003"};

        if (physicalCount==true)
        {
            cursor=myDb.query(TBL_NAME,null,FIELD_ID +" in (?,?,?)" ,args,null,null,null,null);
        }
        else
        {
            cursor=myDb.query(TBL_NAME,null,FIELD_ID +" = ?",new String[]{type},null,null,null,null);
        }
        InventoryTypeSQL.InventoryTypeDTO dto;
        ArrayList<InventoryTypeSQL.InventoryTypeDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new InventoryTypeSQL.InventoryTypeDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setType(cursor.getString(cursor.getColumnIndex(FIELD_TYPE)));
            list.add(dto);
        }
        return list;
    }

    public class InventoryTypeDTO
    {
        private int id;
        private String type;

        public int getId()
        {
            return id;
        }
        public void setId(int id)
        {
            this.id = id;
        }
        public String getType()
        {
            return type;
        }
        public void setType(String type)
        {
            this.type = type;
        }
    }
}
