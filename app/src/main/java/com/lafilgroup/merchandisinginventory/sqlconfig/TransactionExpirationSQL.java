package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/7/2018.
 */

public class TransactionExpirationSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_MATERIAL_CODE= "material_code";
    public static final String FIELD_LOCATION_CODE= "location_code";
    public static final String FIELD_ENTRY_UOM= "entry_uom";
    public static final String FIELD_ENTRY_QTY= "entry_qty";
    public static final String FIELD_BASE_UOM="base_uom";
    public static final String FIELD_BASE_QTY="base_qty";
    public static final String FIELD_EXPIRATION_DATE= "expiration_date";
    public static final String FIELD_CUSTOMER_CODE= "customer_code";
    public static final String FIELD_DELIVERY_DATE="delivery_date";
    public static final String FIELD_RETURN_DATE="return_date";

    public static final String TBL_NAME= "transaction_items_expiration";

    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " "+FIELD_MATERIAL_CODE + " TEXT NOT NULL," +
                    " "+FIELD_LOCATION_CODE + " TEXT NOT NULL," +
                    " "+FIELD_ENTRY_UOM + " TEXT NOT NULL," +
                    " "+FIELD_ENTRY_QTY + " INTEGER NOT NULL," +
                    " "+FIELD_BASE_UOM + " TEXT NOT NULL," +
                    " "+FIELD_BASE_QTY + " INTEGER NOT NULL," +
                    " "+FIELD_EXPIRATION_DATE + " TEXT NOT NULL," +
                    " "+FIELD_CUSTOMER_CODE + " TEXT NOT NULL," +
                    " "+FIELD_DELIVERY_DATE + " TEXT NOT NULL," +
                    " "+FIELD_RETURN_DATE + " TEXT NOT NULL" +");";

    public TransactionExpirationSQL(Context context)
    {
        super(context);
    }

    public boolean deleteAllRecord(TransactionExpirationSQL obj)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,null,null)>0)
        {
            return  true;
        }
        return  false;
    }

    public boolean deleteCustomerExpiration(TransactionExpirationSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_CUSTOMER_CODE +"=?" ,new String[] {customer_code})>0)
        {
            return  true;
        }
        return  false;
    }


    public boolean deleteOneRecord(TransactionExpirationSQL obj, String material_code, String uom, String location_code, String customer_code, String delivery_date, String return_date)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_MATERIAL_CODE +"=? AND " +FIELD_ENTRY_UOM + "=? AND " +FIELD_LOCATION_CODE +"=? AND " +FIELD_CUSTOMER_CODE +"=? and " + FIELD_DELIVERY_DATE + " = ? and " + FIELD_RETURN_DATE + " = ?" ,new String[] {material_code, uom, location_code, customer_code, delivery_date, return_date})>0)
        {
            return  true;
        }
        return  false;
    }

    public long insertRecord(TransactionExpirationSQL obj , String...params)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(FIELD_MATERIAL_CODE,params[0]);
        contentValues.put(FIELD_LOCATION_CODE,params[1]);
        contentValues.put(FIELD_ENTRY_UOM,params[2]);
        contentValues.put(FIELD_ENTRY_QTY,params[3]);
        contentValues.put(FIELD_BASE_UOM,params[4]);
        contentValues.put(FIELD_BASE_QTY,params[5]);
        contentValues.put(FIELD_EXPIRATION_DATE,params[6]);
        contentValues.put(FIELD_CUSTOMER_CODE,params[7]);
        contentValues.put(FIELD_DELIVERY_DATE,params[8]);
        contentValues.put(FIELD_RETURN_DATE,params[9]);
        return myDb.insert(TBL_NAME,null,contentValues);
    }

    public ArrayList<TransactionExpirationDTO> searchItem(TransactionExpirationSQL obj, String material_code, String uom, String location_code, String customer_code, String delivery_date, String return_date)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE +" = ? and " + FIELD_ENTRY_UOM + " = ? and " + FIELD_LOCATION_CODE + " = ? and " + FIELD_CUSTOMER_CODE + " = ? and " + FIELD_DELIVERY_DATE + " = ? and " + FIELD_RETURN_DATE + " = ?" ,new String[] {""+material_code+"",""+uom+"",""+location_code+"",""+customer_code+"",""+delivery_date+"",""+return_date+""},null,null,null,null);
        TransactionExpirationDTO transactionExpirationDTO;
        ArrayList<TransactionExpirationDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionExpirationDTO=new TransactionExpirationDTO();
            transactionExpirationDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionExpirationDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionExpirationDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionExpirationDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionExpirationDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionExpirationDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionExpirationDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionExpirationDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionExpirationDTO.setExpiration_date(cursor.getString(cursor.getColumnIndex(FIELD_EXPIRATION_DATE)));
            transactionExpirationDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionExpirationDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionExpirationDTO);
        }
        return list;
    }

    public ArrayList<TransactionExpirationDTO> allItems(TransactionExpirationSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE + " = ?" ,new String[] {customer_code},null,null,null,null);
        TransactionExpirationDTO transactionExpirationDTO;
        ArrayList<TransactionExpirationDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionExpirationDTO=new TransactionExpirationDTO();
            transactionExpirationDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionExpirationDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionExpirationDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionExpirationDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionExpirationDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionExpirationDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionExpirationDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionExpirationDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionExpirationDTO.setExpiration_date(cursor.getString(cursor.getColumnIndex(FIELD_EXPIRATION_DATE)));
            transactionExpirationDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionExpirationDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionExpirationDTO);
        }
        return list;
    }

    public class TransactionExpirationDTO
    {
        public int id,entry_qty,base_qty;
        private String material_code, entry_uom, base_uom, location_code, customer_code, expiration_date,  delivery_date, return_date;;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMaterial_code() {
            return material_code;
        }

        public void setMaterial_code(String material_code) {
            this.material_code = material_code;
        }

        public String getLocation_code() {
            return location_code;
        }

        public void setLocation_code(String location_code) {
            this.location_code = location_code;
        }

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getExpiration_date() {
            return expiration_date;
        }

        public void setExpiration_date(String expiration_date) {
            this.expiration_date = expiration_date;
        }

        public String getDelivery_date() {
            return delivery_date;
        }

        public void setDelivery_date(String delivery_date) {
            this.delivery_date = delivery_date;
        }

        public String getReturn_date() {
            return return_date;
        }

        public void setReturn_date(String return_date) {
            this.return_date = return_date;
        }

        public int getEntry_qty() {
            return entry_qty;
        }

        public void setEntry_qty(int entry_qty) {
            this.entry_qty = entry_qty;
        }

        public int getBase_qty() {
            return base_qty;
        }

        public void setBase_qty(int base_qty) {
            this.base_qty = base_qty;
        }

        public String getEntry_uom() {
            return entry_uom;
        }

        public void setEntry_uom(String entry_uom) {
            this.entry_uom = entry_uom;
        }

        public String getBase_uom() {
            return base_uom;
        }

        public void setBase_uom(String base_uom) {
            this.base_uom = base_uom;
        }
    }
}
