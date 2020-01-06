package com.lafilgroup.merchandisinginventory.sqlconfig;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lafilgroup.merchandisinginventory.config.GlobalVar;

/**
 * Created by RR SALAS on 5/7/2018.
 */

public class DBHelper extends SQLiteOpenHelper
{
    public DBHelper(Context context)
    {
        super(context, GlobalVar.DB_NAME, null, GlobalVar.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(MaterialSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(InventoryTypeSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(TransactionItemsSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(TransactionExpirationSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(BORemarksSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(CustomerSubmitSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(MaterialBalanceSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(DeliveryRemarksSQL.CREATE_TABLE);
        sqLiteDatabase.execSQL(OfflineInventoryHeaderSQL.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MaterialSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryTypeSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TransactionItemsSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TransactionExpirationSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BORemarksSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CustomerSubmitSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MaterialBalanceSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DeliveryRemarksSQL.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OfflineInventoryHeaderSQL.TBL_NAME);
        onCreate(sqLiteDatabase);
    }
}
