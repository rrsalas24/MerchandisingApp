package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 4/17/2018.
 */

public class MaterialSQL extends DBHelper
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_MATERIAL_CODE= "material_code";
    public static final String FIELD_MATERIAL_DESCRIPTION= "material_description";
    public static final String FIELD_UPC_NUMBER= "upc_number";
    public static final String FIELD_ALTERNATIVE_UNIT= "alternative_unit";
    public static final String FIELD_BASE_UNIT= "base_unit";
    public static final String FIELD_NUMERATOR= "numerator";
    public static final String FIELD_DENOMINATOR= "denominator";

    public static final String TBL_NAME= "material_master_data";
    public static final String CREATE_TABLE=
            "CREATE TABLE " +TBL_NAME+ " ("+FIELD_ID+" INTEGER PRIMARY KEY," +
                    " "+FIELD_MATERIAL_CODE + " TEXT NOT NULL," +
                    " "+FIELD_MATERIAL_DESCRIPTION + " TEXT NOT NULL," +
                    " "+FIELD_UPC_NUMBER + " TEXT NOT NULL," +
                    " "+FIELD_ALTERNATIVE_UNIT + " TEXT NOT NULL," +
                    " "+FIELD_BASE_UNIT + " TEXT NOT NULL," +
                    " "+FIELD_NUMERATOR + " INTEGER NOT NULL," +
                    " "+FIELD_DENOMINATOR + " INTEGER NOT NULL" +");";

    public MaterialSQL(Context context)
    {
        super(context);
    }

    public long insertRecord(MaterialSQL obj , String...params)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(FIELD_ID,params[0]);
        contentValues.put(FIELD_MATERIAL_CODE,params[1]);
        contentValues.put(FIELD_MATERIAL_DESCRIPTION,params[2]);
        contentValues.put(FIELD_UPC_NUMBER,params[3]);
        contentValues.put(FIELD_ALTERNATIVE_UNIT,params[4]);
        contentValues.put(FIELD_BASE_UNIT,params[5]);
        contentValues.put(FIELD_NUMERATOR,params[6]);
        contentValues.put(FIELD_DENOMINATOR,params[7]);
        return myDb.insert(TBL_NAME,null,contentValues);
    }

    public boolean deleteRecord(MaterialSQL obj)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        if (myDb.delete(TBL_NAME,null,null)>0)
        {
            return  true;
        }
        return  false;
    }

    public ArrayList<MaterialDTO>searchItem(MaterialSQL obj,String search)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_UPC_NUMBER +" like ? or " + FIELD_MATERIAL_CODE + " like ? or " + FIELD_MATERIAL_DESCRIPTION + " like ?" ,new String[] {"%"+search+"%","%"+search+"%","%"+search+"%"},FIELD_MATERIAL_CODE,null,null,null);
        MaterialDTO dto;
        ArrayList<MaterialDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new MaterialDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            dto.setMaterial_description(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_DESCRIPTION)));
            dto.setUpc_number(cursor.getString(cursor.getColumnIndex(FIELD_UPC_NUMBER)));
            dto.setAlternative_unit(cursor.getString(cursor.getColumnIndex(FIELD_ALTERNATIVE_UNIT)));
            dto.setBase_unit(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UNIT)));
            dto.setNumerator(cursor.getInt(cursor.getColumnIndex(FIELD_NUMERATOR)));
            dto.setDenominator(cursor.getInt(cursor.getColumnIndex(FIELD_DENOMINATOR)));
            list.add(dto);
        }
        return list;
    }

    public ArrayList<MaterialDTO>noPhysicalCount(MaterialSQL obj,String[] args, String materialsParam)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE + " not in ("+ materialsParam +")",args,FIELD_MATERIAL_CODE,null,null,null);
        MaterialDTO dto;
        ArrayList<MaterialDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new MaterialDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            dto.setMaterial_description(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_DESCRIPTION)));
            dto.setUpc_number(cursor.getString(cursor.getColumnIndex(FIELD_UPC_NUMBER)));
            dto.setAlternative_unit(cursor.getString(cursor.getColumnIndex(FIELD_ALTERNATIVE_UNIT)));
            dto.setBase_unit(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UNIT)));
            dto.setNumerator(cursor.getInt(cursor.getColumnIndex(FIELD_NUMERATOR)));
            dto.setDenominator(cursor.getInt(cursor.getColumnIndex(FIELD_DENOMINATOR)));
            list.add(dto);
        }
        return list;
    }
    public ArrayList<MaterialDTO>getUOM(MaterialSQL obj,String search)
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_UPC_NUMBER +" like ? or " + FIELD_MATERIAL_CODE + " like ? or " + FIELD_MATERIAL_DESCRIPTION + " like ?" ,new String[] {"%"+search+"%","%"+search+"%","%"+search+"%"},null,null,FIELD_ALTERNATIVE_UNIT + " DESC",null);
        MaterialDTO dto;
        ArrayList<MaterialDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new MaterialDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            dto.setMaterial_description(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_DESCRIPTION)));
            dto.setUpc_number(cursor.getString(cursor.getColumnIndex(FIELD_UPC_NUMBER)));
            dto.setAlternative_unit(cursor.getString(cursor.getColumnIndex(FIELD_ALTERNATIVE_UNIT)));
            dto.setBase_unit(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UNIT)));
            dto.setNumerator(cursor.getInt(cursor.getColumnIndex(FIELD_NUMERATOR)));
            dto.setDenominator(cursor.getInt(cursor.getColumnIndex(FIELD_DENOMINATOR)));
            list.add(dto);
        }
        return list;
    }


    public ArrayList<MaterialDTO>getMultiplier(MaterialSQL obj,String material_code, String uom )
    {
        SQLiteDatabase myDb= obj.getWritableDatabase();
        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE +" = ? and " + FIELD_ALTERNATIVE_UNIT + " = ?" ,new String[] {material_code,uom},null,null,null,null);
        MaterialDTO dto;
        ArrayList<MaterialDTO> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            dto=new MaterialDTO();
            dto.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            dto.setMaterial_code(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_CODE)));
            dto.setMaterial_description(cursor.getString(cursor.getColumnIndex(FIELD_MATERIAL_DESCRIPTION)));
            dto.setUpc_number(cursor.getString(cursor.getColumnIndex(FIELD_UPC_NUMBER)));
            dto.setAlternative_unit(cursor.getString(cursor.getColumnIndex(FIELD_ALTERNATIVE_UNIT)));
            dto.setBase_unit(cursor.getString(cursor.getColumnIndex(FIELD_BASE_UNIT)));
            dto.setNumerator(cursor.getInt(cursor.getColumnIndex(FIELD_NUMERATOR)));
            dto.setDenominator(cursor.getInt(cursor.getColumnIndex(FIELD_DENOMINATOR)));
            list.add(dto);
        }
        return list;
    }

//    public String getBaseUOM(MaterialSQL obj,String material_code)
//    {
//        SQLiteDatabase myDb= obj.getWritableDatabase();
//        Cursor cursor=myDb.query(TBL_NAME,null,FIELD_MATERIAL_CODE + " = ?" ,new String[] {material_code},FIELD_MATERIAL_CODE,null,null,null);
//        return cursor.getString(cursor.getColumnIndex(FIELD_BASE_UNIT));
//    }
    //=======================DTO===============
    public class MaterialDTO
    {
        private int id,numerator,denominator;
        private String material_code,material_description,upc_number,alternative_unit,base_unit;

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

        public String getMaterial_description() {
            return material_description;
        }

        public void setMaterial_description(String material_description) {
            this.material_description = material_description;
        }

        public String getUpc_number() {
            return upc_number;
        }

        public void setUpc_number(String upc_number) {
            this.upc_number = upc_number;
        }

        public String getAlternative_unit() {
            return alternative_unit;
        }

        public void setAlternative_unit(String alternative_unit) {
            this.alternative_unit = alternative_unit;
        }

        public int getNumerator() {
            return numerator;
        }

        public void setNumerator(int numerator) {
            this.numerator = numerator;
        }

        public int getDenominator() {
            return denominator;
        }

        public void setDenominator(int denominator) {
            this.denominator = denominator;
        }

        public String getBase_unit() {
            return base_unit;
        }

        public void setBase_unit(String base_unit) {
            this.base_unit = base_unit;
        }
    }
}
