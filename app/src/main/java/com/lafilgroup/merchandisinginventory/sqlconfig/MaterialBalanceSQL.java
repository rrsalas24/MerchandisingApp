package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 7/19/2018.
 */

public class MaterialBalanceSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_MATERIAL_CODE= "material_code";
    public static final String FIELD_MATERIAL_DESCRIPTION= "material_description";
    public static final String FIELD_CUSTOMER_CODE= "customer_code";
    public static final String FIELD_BASE_UOM= "base_uom";
    public static final String FIELD_ENDING_BALANCE= "ending_balance";
    public static final String FIELD_CREATED_AT= "created_at";

    public static final String TBL_NAME= "material_balance";

    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " "+FIELD_MATERIAL_CODE + " TEXT NOT NULL," +
                    " "+FIELD_MATERIAL_DESCRIPTION + " TEXT NOT NULL," +
                    " "+FIELD_CUSTOMER_CODE + " TEXT NOT NULL," +
                    " "+FIELD_BASE_UOM + " TEXT NOT NULL," +
                    " "+FIELD_ENDING_BALANCE + " INTEGER NOT NULL," +
                    " "+FIELD_CREATED_AT + " TEXT NOT NULL" +");";

    public MaterialBalanceSQL(Context context)
    {
        super(context);
    }

    public long insertRecord(MaterialBalanceSQL obj , String...params)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(FIELD_MATERIAL_CODE,params[0]);
        contentValues.put(FIELD_MATERIAL_DESCRIPTION,params[1]);
        contentValues.put(FIELD_CUSTOMER_CODE,params[2]);
        contentValues.put(FIELD_BASE_UOM,params[3]);
        contentValues.put(FIELD_ENDING_BALANCE,params[4]);
        contentValues.put(FIELD_CREATED_AT,params[5]);
        return myDb.insert(TBL_NAME,null,contentValues);
    }

    public boolean deleteRecord(MaterialBalanceSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_CUSTOMER_CODE +"=?" ,new String[] {customer_code})>0)
        {
            return true;
        }
        return  false;
    }

    public ArrayList<MaterialBalanceDTO> getBalance(MaterialBalanceSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE +"=?",new String[] {customer_code},null,null,FIELD_MATERIAL_CODE + " ASC",null);
        MaterialBalanceDTO dto;
        ArrayList<MaterialBalanceDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new MaterialBalanceDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            dto.setMaterial_description(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_DESCRIPTION)));
            dto.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            dto.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            dto.setEnding_balance(cursor.getInt(cursor.getColumnIndex(FIELD_ENDING_BALANCE)));
            dto.setCreated_at(cursor.getString(cursor.getColumnIndex(FIELD_CREATED_AT)));
            list.add(dto);
        }
        return list;
    }

    public Integer totalBalanceQuantity(MaterialBalanceSQL obj,  String material_code, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE + " LIKE ? and " + FIELD_CUSTOMER_CODE + " = ?",new String[]{material_code, customer_code},null,null,null,null);
        int count=0;
        while (cursor.moveToNext())
        {
            count=count+cursor.getInt(cursor.getColumnIndex(FIELD_ENDING_BALANCE));
        }
        return count;
    }

    public class MaterialBalanceDTO
    {
        private int id,ending_balance;
        private String material_code,material_description,base_uom,created_at, customer_code;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getEnding_balance() {
            return ending_balance;
        }

        public void setEnding_balance(int ending_balance) {
            this.ending_balance = ending_balance;
        }

        public String getMaterial_code() {
            return material_code;
        }

        public void setMaterial_code(String material_code) {
            this.material_code = material_code;
        }

        public String getMaterial_description() {
            return material_description;
        }

        public void setMaterial_description(String material_description) {
            this.material_description = material_description;
        }

        public String getBase_uom() {
            return base_uom;
        }

        public void setBase_uom(String base_uom) {
            this.base_uom = base_uom;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }
    }
}
