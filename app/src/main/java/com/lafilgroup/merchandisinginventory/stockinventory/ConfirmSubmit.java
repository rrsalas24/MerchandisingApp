package com.lafilgroup.merchandisinginventory.stockinventory;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.AppContoller;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.VolleyMultipartRequest;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.CustomerSubmitSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.MaterialBalanceSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.OfflineInventoryHeaderSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionExpirationSQL;
import com.lafilgroup.merchandisinginventory.sqlconfig.TransactionItemsSQL;
import com.lafilgroup.merchandisinginventory.storelogin.LoginTakePicture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfirmSubmit extends AppCompatActivity implements View.OnClickListener
{
    ImageView imgPicture;
    EditText txtRemarks, txtTransactionNumber;
    Button btnConfirm, btnCamera;

    int PHOTO_CODE=0;
    Bitmap bitmap;
    Uri imageUri;

    TransactionItemsSQL transactionItemsSQL;
    CustomerSubmitSQL customerSubmitSQL;
    TransactionExpirationSQL transactionExpirationSQL;
    MaterialBalanceSQL materialBalanceSQL;

    public static String transaction_number;
    MessageDialog messageDialog;
    WaitingDialog waitingDialog;
    SweetAlertDialog sweetAlertDialog;
    OfflineInventoryHeaderSQL offlineInventoryHeaderSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_submit);
        this.setTitle("Confirmation");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        castObj();
        txtTransactionNumber.setText(transaction_number);
    }


    public void castObj()
    {
        imgPicture=findViewById(R.id.imgPicture);
        txtRemarks=findViewById(R.id.txtRemarks);
        txtTransactionNumber=findViewById(R.id.txtTransactionNumber);
        btnConfirm=findViewById(R.id.btnConfirm);
        btnCamera=findViewById(R.id.btnCamera);
        btnConfirm.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PHOTO_CODE && resultCode == RESULT_OK)
        {
            try
            {
                compressImage(imageUri);
                imgPicture.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            btnConfirm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCamera:
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PHOTO_CODE);
                break;
            case R.id.btnConfirm:
                uploadImage();
                break;
        }
    }

    private void uploadImage()
    {
        final WaitingDialog waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialogWithMessage("Image uploading. Please wait...");
        String url=getString(R.string.host) + getString(R.string.submit_transaction_image) + GlobalVar.api_token;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse response)
                    {
                        waitingDialog.closeDialog();
                        confirmTransaction();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        waitingDialog.closeDialog();
                        MessageDialog messageDialog=new MessageDialog(ConfirmSubmit.this,"Error","There is an error upon uploading of image. Try again later.");
                        messageDialog.messageError();
                        offlineSave();
                    }
                })
            {
            protected Map<String, DataPart> getByteData()
            {
                Map<String, DataPart> params = new HashMap<>();
                String imageName ="image.png";
                params.put("img", new DataPart(imageName, bitmapToFile()));

                return params;
            }
            protected Map<String,String> getParams()
            {
                Map <String, String> params =new HashMap<>();
                params.put("transaction_number",txtTransactionNumber.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public void confirmTransaction()
    {
        //header insert statement================================================
        String header = "call p_insert_inventory_header('" + txtTransactionNumber.getText().toString() + "','" + GlobalVar.user_id + "','" + GlobalVar.schedule_id + "','" + GlobalVar.customer_code + "','" + txtRemarks.getText().toString() + "','" + GlobalVar.getCurrentDateTime() + "')";
        //item insert statement==================================================
        transactionItemsSQL=new TransactionItemsSQL(this);
        ArrayList<TransactionItemsSQL.TransactionItemDTO> dto=transactionItemsSQL.allItems(transactionItemsSQL, GlobalVar.customer_code);
        String item_values="";
        String val="";
        int counter=0;
        while (counter<dto.size())
        {
            if (dto.get(counter).getLocation_code().equals("4"))
            {
                val="('" + txtTransactionNumber.getText().toString() + "','" + dto.get(counter).getMaterial_code().toString() + "','" + dto.get(counter).getLocation_code().toString() + "','" + dto.get(counter).getEntry_uom().toString() + "','" + dto.get(counter).getEntry_qty() + "','" + dto.get(counter).getBase_uom().toString() + "','" + dto.get(counter).getBase_qty() + "','" + dto.get(counter).getRemarks().toString() + "','" + GlobalVar.toStringToDate(dto.get(counter).getDelivery_date().toString()) + "'," + null + "),";
            }
            else if (dto.get(counter).getLocation_code().equals("5"))
            {
                val="('" + txtTransactionNumber.getText().toString() + "','" + dto.get(counter).getMaterial_code().toString() + "','" + dto.get(counter).getLocation_code().toString() + "','" + dto.get(counter).getEntry_uom().toString() + "','" + dto.get(counter).getEntry_qty() + "','" + dto.get(counter).getBase_uom().toString() + "','" + dto.get(counter).getBase_qty() + "','" + dto.get(counter).getRemarks().toString() + "'," + null + ",'" + GlobalVar.toStringToDate(dto.get(counter).getReturn_date().toString()) + "'),";
            }
            else
            {
                val="('" + txtTransactionNumber.getText().toString() + "','" + dto.get(counter).getMaterial_code().toString() + "','" + dto.get(counter).getLocation_code().toString() + "','" + dto.get(counter).getEntry_uom().toString() + "','" + dto.get(counter).getEntry_qty() + "','" + dto.get(counter).getBase_uom().toString() + "','" + dto.get(counter).getBase_qty() + "','" + "Physical Count" + "'," + null + "," + null + "),";
            }
            item_values = item_values+val;
            counter++;
        }
        String item = "call p_insert_inventory_items(\"" +item_values.substring(0,item_values.length()-1)+"\")";
        //expiration insert statement======================================================
        transactionExpirationSQL = new TransactionExpirationSQL(this);
        ArrayList<TransactionExpirationSQL.TransactionExpirationDTO> ex_dto=transactionExpirationSQL.allItems(transactionExpirationSQL, GlobalVar.customer_code);
        int ex_counter=0;
        String ex_values="";
        String ex_val="";
        while (ex_counter<ex_dto.size())
        {
            ex_val="('" + txtTransactionNumber.getText().toString() + "','" + ex_dto.get(ex_counter).getMaterial_code().toString() + "','" + ex_dto.get(ex_counter).getLocation_code().toString() + "','" + ex_dto.get(ex_counter).getEntry_uom().toString() + "','" + ex_dto.get(ex_counter).getEntry_qty() + "','" + ex_dto.get(ex_counter).getBase_uom().toString() + "','" + ex_dto.get(ex_counter).getBase_qty() + "','" + GlobalVar.toStringToDate(ex_dto.get(ex_counter).getExpiration_date().toString()) + "'),";
            ex_values=ex_values+ex_val;
            ex_counter++;
        }
        String expiration = "";

        if (ex_dto.size()==0)
        {
            expiration="call p_bo_remarks";
        }
        else
        {
            expiration = "call p_insert_inventory_expiration(\"" +ex_values.substring(0,ex_values.length()-1)+"\")";
        }
        //beginning balance insert statement================================================
        materialBalanceSQL =new MaterialBalanceSQL(this);
        ArrayList<MaterialBalanceSQL.MaterialBalanceDTO> bal_dto = materialBalanceSQL.getBalance(materialBalanceSQL,GlobalVar.customer_code);
        int bal_counter=0;
        String bal_values="";
        String bal_val="";
        while (bal_counter<bal_dto.size())
        {
            bal_val="('" + txtTransactionNumber.getText().toString() + "','" + bal_dto.get(bal_counter).getMaterial_code().toString() + "','" + "6" + "','" + bal_dto.get(bal_counter).getBase_uom().toString() + "','" + bal_dto.get(bal_counter).getEnding_balance() + "','" + bal_dto.get(bal_counter).getBase_uom().toString() + "','" + bal_dto.get(bal_counter).getEnding_balance() + "','" + "Beginning Balance" + "'," + null + "," + null + "),";
            bal_values=bal_values+bal_val;
            bal_counter++;
        }
        String bal_item="";
        if (bal_dto.size()==0)
        {
            bal_item="call p_bo_remarks";
        }
        else
        {
            bal_item = "call p_insert_inventory_items(\"" +bal_values.substring(0,bal_values.length()-1)+"\")";
        }

        //transaction insert====================
        insertTransaction(txtTransactionNumber.getText().toString(),header,item,expiration,bal_item);
    }

    public void insertTransaction(final String transaction_number, final String header, final String item, final String expiration, final String beginning)
    {
        waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialogWithMessage("Inventory uploading. Please wait...");
        String url=getString(R.string.host)+ getString(R.string.submit_transaction) +GlobalVar.api_token;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                waitingDialog.closeDialog();
                if (response.toString().equals("success"))
                {
                    customerSubmitSQL=new CustomerSubmitSQL(ConfirmSubmit.this);
                    customerSubmitSQL.deleteRecord(customerSubmitSQL,GlobalVar.customer_code);
                    customerSubmitSQL.insertRecord(customerSubmitSQL,GlobalVar.customer_code, GlobalVar.getCurrentDate(0));

                    transactionItemsSQL=new TransactionItemsSQL(ConfirmSubmit.this);
                    transactionItemsSQL.deleteCustomerItem(transactionItemsSQL,GlobalVar.customer_code);

                    transactionExpirationSQL = new TransactionExpirationSQL(ConfirmSubmit.this);
                    transactionExpirationSQL.deleteCustomerExpiration(transactionExpirationSQL,GlobalVar.customer_code);

                    materialBalanceSQL=new MaterialBalanceSQL(ConfirmSubmit.this);
                    materialBalanceSQL.deleteRecord(materialBalanceSQL,GlobalVar.customer_code);

                    StockInventory.submitted_status=true;
                    finish();
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog=new MessageDialog(ConfirmSubmit.this,"Error","There is an error upon submission. Try again later.");
                    messageDialog.messageError();
                    offlineSave();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                waitingDialog.closeDialog();
                messageDialog=new MessageDialog(ConfirmSubmit.this,"Error","No network connectivity");
                messageDialog.messageError();
                error.printStackTrace();
                offlineSave();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map<String, String> params =new HashMap<>();
                params.put("transaction_number",transaction_number);
                params.put("header",header);
                params.put("item",item);
                params.put("expiration",expiration);
                params.put("beginning",beginning);
                return params;
            }
        };
        AppContoller.getmInstance(this).addRequestQue(stringRequest);
    }
    //=========================================image compress============================================================================================
    private byte[] bitmapToFile()
    {
        ByteArrayOutputStream bAO = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bAO);
        return bAO.toByteArray();
    }

    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String compressImage(Uri imageUris) {
        String filePath = getRealPathFromURI(imageUris);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap = scaledBitmap;
        return filename;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
    //============================================================================================================================
    public void offlineSave()
    {
        sweetAlertDialog= new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setContentText("Do you want to save in offline mode?");

        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitle("Network is unreachable.");
        sweetAlertDialog.setCancelText("Cancel");
        sweetAlertDialog.setConfirmText("Confirm");

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
            {
                sweetAlertDialog.cancel();
                offlineInventoryHeaderSQL=new OfflineInventoryHeaderSQL(ConfirmSubmit.this);
                offlineInventoryHeaderSQL.insertRecord(offlineInventoryHeaderSQL,GlobalVar.customer_code,GlobalVar.customer_id,GlobalVar.customer_name, GlobalVar.schedule_id,GlobalVar.getCurrentDate(0),GlobalVar.getCurrentDateTime());
                StockInventory.offline_save=true;
                finish();

            }
        });

        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
            }
        });

        sweetAlertDialog.show();
    }
}
