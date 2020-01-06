package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/6/2018.
 */

public class TransactionItemsSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_BARCODE= "barcode";
    public static final String FIELD_MATERIAL_CODE= "material_code";
    public static final String FIELD_DESCRIPTION= "description";
    public static final String FIELD_LOCATION_CODE= "location_code";
    public static final String FIELD_LOCATION= "location";
    public static final String FIELD_ENTRY_UOM= "entry_uom";
    public static final String FIELD_ENTRY_QTY= "entry_qty";
    public static final String FIELD_BASE_UOM="base_uom";
    public static final String FIELD_BASE_QTY="base_qty";
    public static final String FIELD_CUSTOMER_CODE= "customer_code";
    public static final String FIELD_REMARKS= "remarks";
    public static final String FIELD_DELIVERY_DATE="delivery_date";
    public static final String FIELD_RETURN_DATE="return_date";

    public static final String TBL_NAME= "transaction_items";

    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " "+FIELD_BARCODE + " TEXT NOT NULL," +
                    " "+FIELD_MATERIAL_CODE + " TEXT NOT NULL," +
                    " "+FIELD_DESCRIPTION + " TEXT NOT NULL," +
                    " "+FIELD_LOCATION_CODE + " TEXT NOT NULL," +
                    " "+FIELD_LOCATION + " TEXT NOT NULL," +
                    " "+FIELD_ENTRY_UOM + " TEXT NOT NULL," +
                    " "+FIELD_ENTRY_QTY + " INTEGER NOT NULL," +
                    " "+FIELD_BASE_UOM + " TEXT NOT NULL," +
                    " "+FIELD_BASE_QTY + " INTEGER NOT NULL," +
                    " "+FIELD_CUSTOMER_CODE + " TEXT NOT NULL," +
                    " "+FIELD_REMARKS + " TEXT NOT NULL," +
                    " "+FIELD_DELIVERY_DATE + " TEXT NOT NULL," +
                    " "+FIELD_RETURN_DATE + " TEXT NOT NULL" +");";

    public TransactionItemsSQL(Context context)
    {
        super(context);
    }

    public long insertRecord(TransactionItemsSQL obj , String...params)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(FIELD_BARCODE,params[0]);
        contentValues.put(FIELD_MATERIAL_CODE,params[1]);
        contentValues.put(FIELD_DESCRIPTION,params[2]);
        contentValues.put(FIELD_LOCATION_CODE,params[3]);
        contentValues.put(FIELD_LOCATION,params[4]);
        contentValues.put(FIELD_ENTRY_UOM,params[5]);
        contentValues.put(FIELD_ENTRY_QTY,params[6]);
        contentValues.put(FIELD_BASE_UOM,params[7]);
        contentValues.put(FIELD_BASE_QTY,params[8]);
        contentValues.put(FIELD_CUSTOMER_CODE,params[9]);
        contentValues.put(FIELD_REMARKS,params[10]);
        contentValues.put(FIELD_DELIVERY_DATE,params[11]);
        contentValues.put(FIELD_RETURN_DATE,params[12]);
        return myDb.insert(TBL_NAME,null,contentValues);
    }

    public boolean deleteAllRecord(TransactionItemsSQL obj)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,null,null)>0)
        {
            return  true;
        }
        return  false;
    }

    public boolean deleteOneRecord(TransactionItemsSQL obj, Integer id)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_ID +"="+ id,null)>0)
        {
            return  true;
        }
        return  false;
    }

    public boolean deleteCustomerItem(TransactionItemsSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,FIELD_CUSTOMER_CODE +"=?" ,new String[] {customer_code})>0)
        {
            return  true;
        }
        return  false;
    }

    public void updateRecord(TransactionItemsSQL obj, Integer id, Integer entry_qty, Integer base_qty)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FIELD_ENTRY_QTY,entry_qty);
        cv.put(FIELD_BASE_QTY,base_qty);
        myDb.update(TBL_NAME,cv," id="+id,null);
    }

    public ArrayList<TransactionItemDTO>physicalCount(TransactionItemsSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        String[] args = new String[]{customer_code,"1","2","3"};
        //Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE +" like ? or " + FIELD_UNIT_OF_MEASUREMENT + " like ? or " + FIELD_LOCATION_CODE + " like ? or " + FIELD_CUSTOMER_CODE + " like ?" ,new String[] {"%"+material_code+"%","%"+uom+"%","%"+location_code+"%","%"+customer_code+"%"},null,null,null,null);
        cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE + " = ? and " +FIELD_LOCATION_CODE +" in (?,?,?)",args,null,null,null);

        TransactionItemDTO transactionItemDTO;
        ArrayList<TransactionItemDTO> list= new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionItemDTO=new TransactionItemDTO();
            transactionItemDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionItemDTO.setBarcode(cursor.getString(cursor.getColumnIndex(FIELD_BARCODE)));
            transactionItemDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionItemDTO.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
            transactionItemDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionItemDTO.setLocation(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION)));
            transactionItemDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionItemDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionItemDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionItemDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionItemDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionItemDTO.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            transactionItemDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionItemDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionItemDTO);
        }
        return  list;
    }

    public ArrayList<TransactionItemDTO>totalPhysicalCount(TransactionItemsSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        String[] args = new String[]{customer_code,"1","2","3"};

        //Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE +" like ? or " + FIELD_UNIT_OF_MEASUREMENT + " like ? or " + FIELD_LOCATION_CODE + " like ? or " + FIELD_CUSTOMER_CODE + " like ?" ,new String[] {"%"+material_code+"%","%"+uom+"%","%"+location_code+"%","%"+customer_code+"%"},null,null,null,null);
        cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE + " = ? and " +FIELD_LOCATION_CODE +" in (?,?,?)",args,FIELD_MATERIAL_CODE,null,null);

        TransactionItemDTO transactionItemDTO;
        ArrayList<TransactionItemDTO> list= new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionItemDTO=new TransactionItemDTO();
            transactionItemDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionItemDTO.setBarcode(cursor.getString(cursor.getColumnIndex(FIELD_BARCODE)));
            transactionItemDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionItemDTO.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
            transactionItemDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionItemDTO.setLocation(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION)));
            transactionItemDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionItemDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionItemDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionItemDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionItemDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionItemDTO.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            transactionItemDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionItemDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionItemDTO);
        }
        return  list;
    }

    public ArrayList<TransactionItemDTO>searchItem(TransactionItemsSQL obj,boolean validation, String material_code, String uom, String location_code, String customer_code, String date_delivered, String date_returned)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        //Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE +" like ? or " + FIELD_UNIT_OF_MEASUREMENT + " like ? or " + FIELD_LOCATION_CODE + " like ? or " + FIELD_CUSTOMER_CODE + " like ?" ,new String[] {"%"+material_code+"%","%"+uom+"%","%"+location_code+"%","%"+customer_code+"%"},null,null,null,null);
        if (validation==true)
        {
            cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE +" = ? and " + FIELD_ENTRY_UOM + " = ? and " + FIELD_LOCATION_CODE + " = ? and " + FIELD_CUSTOMER_CODE + " = ?  and " + FIELD_DELIVERY_DATE + " = ?  and " + FIELD_RETURN_DATE + " = ?" ,new String[] {""+material_code+"",""+uom+"",""+location_code+"",""+customer_code+"",""+date_delivered+"",""+date_returned+""},null,null,null,null);
        }
        else
        {
            cursor=myDb.query(TBL_NAME,null,FIELD_LOCATION_CODE + " = ? and " + FIELD_CUSTOMER_CODE + " = ?" ,new String[] {location_code,customer_code},null,null,null,null);
        }

        TransactionItemDTO transactionItemDTO;
        ArrayList<TransactionItemDTO> list= new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionItemDTO=new TransactionItemDTO();
            transactionItemDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionItemDTO.setBarcode(cursor.getString(cursor.getColumnIndex(FIELD_BARCODE)));
            transactionItemDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionItemDTO.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
            transactionItemDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionItemDTO.setLocation(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION)));
            transactionItemDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionItemDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionItemDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionItemDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionItemDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionItemDTO.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            transactionItemDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionItemDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionItemDTO);
        }
        return  list;
    }

    public ArrayList<TransactionItemDTO>specificDetails(TransactionItemsSQL obj, Integer id)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        cursor=myDb.query(TBL_NAME,null,FIELD_ID +" = ? ",new String[]{""+id+""},null,null,null,null);

        TransactionItemDTO transactionItemDTO;
        ArrayList<TransactionItemDTO> list= new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionItemDTO=new TransactionItemDTO();
            transactionItemDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionItemDTO.setBarcode(cursor.getString(cursor.getColumnIndex(FIELD_BARCODE)));
            transactionItemDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionItemDTO.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
            transactionItemDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionItemDTO.setLocation(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION)));
            transactionItemDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionItemDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionItemDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionItemDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionItemDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionItemDTO.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            transactionItemDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionItemDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionItemDTO);
        }
        return  list;
    }

    public ArrayList<TransactionItemDTO>allItems(TransactionItemsSQL obj, String customer_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        cursor=myDb.query(TBL_NAME,null,FIELD_CUSTOMER_CODE +" = ? ",new String[]{customer_code},null,null,null,null);

        TransactionItemDTO transactionItemDTO;
        ArrayList<TransactionItemDTO> list= new ArrayList<>();
        while (cursor.moveToNext())
        {
            transactionItemDTO=new TransactionItemDTO();
            transactionItemDTO.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            transactionItemDTO.setBarcode(cursor.getString(cursor.getColumnIndex(FIELD_BARCODE)));
            transactionItemDTO.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            transactionItemDTO.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
            transactionItemDTO.setLocation_code(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION_CODE)));
            transactionItemDTO.setLocation(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION)));
            transactionItemDTO.setEntry_uom(cursor.getString(cursor.getColumnIndex(FIELD_ENTRY_UOM)));
            transactionItemDTO.setEntry_qty(cursor.getInt(cursor.getColumnIndex(FIELD_ENTRY_QTY)));
            transactionItemDTO.setBase_uom(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UOM)));
            transactionItemDTO.setBase_qty(cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY)));
            transactionItemDTO.setCustomer_code(cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_CODE)));
            transactionItemDTO.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD_REMARKS)));
            transactionItemDTO.setDelivery_date(cursor.getString(cursor.getColumnIndex(FIELD_DELIVERY_DATE)));
            transactionItemDTO.setReturn_date(cursor.getString(cursor.getColumnIndex(FIELD_RETURN_DATE)));
            list.add(transactionItemDTO);
        }
        return  list;
    }

    public Integer totalPhysicalQuantity(TransactionItemsSQL obj, String customer_code, String material_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        String[] args = new String[]{material_code,customer_code,"1","2","3"};
        cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE + " LIKE ? and " +FIELD_CUSTOMER_CODE + " = ? and " +FIELD_LOCATION_CODE +" in (?,?,?)",args,null,null,null);
        int count=0;
        while (cursor.moveToNext())
        {
            count=count+cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY));
        }
        return count;
    }

    public Integer totalDeliveryQuantity(TransactionItemsSQL obj, String customer_code, String material_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        String[] args = new String[]{material_code,customer_code,"4"};
        cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE + " LIKE ? and " +FIELD_CUSTOMER_CODE + " = ? and " +FIELD_LOCATION_CODE +" = ?",args,null,null,null);
        int count=0;
        while (cursor.moveToNext())
        {
            count=count+cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY));
        }
        return count;
    }

    public Integer totalReturnQuantity(TransactionItemsSQL obj, String customer_code, String material_code)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor;
        String[] args = new String[]{material_code,customer_code,"5"};
        cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE + " LIKE ? and " +FIELD_CUSTOMER_CODE + " = ? and " +FIELD_LOCATION_CODE +" = ?",args,null,null,null);
        int count=0;
        while (cursor.moveToNext())
        {
            count=count+cursor.getInt(cursor.getColumnIndex(FIELD_BASE_QTY));
        }
        return count;
    }

    public class TransactionItemDTO
    {
        public int id,entry_qty,base_qty;
        private String barcode, material_code, description, entry_uom, base_uom,location,location_code,customer_code, remarks, delivery_date, return_date;


        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation_code() {
            return location_code;
        }

        public void setLocation_code(String location_code) {
            this.location_code = location_code;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getMaterial_code() {
            return material_code;
        }

        public void setMaterial_code(String material_code) {
            this.material_code = material_code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
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
