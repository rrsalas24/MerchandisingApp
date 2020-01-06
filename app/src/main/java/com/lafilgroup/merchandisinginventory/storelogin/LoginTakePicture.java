package com.lafilgroup.merchandisinginventory.storelogin;

import android.content.ContentValues;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.CustomStringRequest;
import com.lafilgroup.merchandisinginventory.config.GlobalVar;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.config.VolleyMultipartRequest;
import com.lafilgroup.merchandisinginventory.config.WaitingDialog;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginTakePicture extends AppCompatActivity implements View.OnClickListener
{
    ImageView imgPicture;
    Button btnConfirm, btnCamera;
    LinearLayout lnrProgress;
    Uri imageUri;
    TextView txtCustomer;

    int PHOTO_CODE=0;
    Bitmap bitmap;

    MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_take_picture);
        this.setTitle("Take Picture");
        btnConfirm=findViewById(R.id.btnConfirm);
        btnCamera=findViewById(R.id.btnCamera);
        imgPicture=findViewById(R.id.imgPicture);
        lnrProgress=findViewById(R.id.lnrProgress);
        txtCustomer=findViewById(R.id.txtCustomer);
        txtCustomer.setText(GlobalVar.customer_name);
        btnConfirm.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
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
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PHOTO_CODE);
                break;
            case R.id.btnConfirm:
                uploadImage();
                break;
        }
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

    private void uploadImage()
    {
        final WaitingDialog waitingDialog=new WaitingDialog(this);
        waitingDialog.openDialog();
        String url=getString(R.string.host) + getString(R.string.attendance_image) + GlobalVar.api_token;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse response)
                    {
                        waitingDialog.closeDialog();
                        insertAttendance();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        waitingDialog.closeDialog();
                        messageDialog=new MessageDialog(LoginTakePicture.this,"Error",error.toString());
                        messageDialog.messageError();
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
                params.put("schedule_id",GlobalVar.schedule_id);
                return params;
            }
        };
        final int TIMEOUT_MS = 100000;
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy. DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public void insertAttendance()
    {
        String url=getString(R.string.host) + getString(R.string.exec_url) + GlobalVar.api_token;
        //String qry = "insert into merchandiser_attendance (schedule_id, time_in, created_at) " +
                //"values ('" + GlobalVar.schedule_id + "','" + GlobalVar.getCurrentTime() + "','"  + GlobalVar.getCurrentDateTime() + "')";
        String qry="call p_insert_merchandiser_attendance ('" + GlobalVar.schedule_id + "','" + GlobalVar.getCurrentTime() + "','"  + GlobalVar.getCurrentDateTime() +"')";
        CustomStringRequest customStringRequest=new CustomStringRequest(this);
        customStringRequest.qryValue(qry, url, new CustomStringRequest.dataCallback() {
            @Override
            public void onSuccess(String response)
            {
                if (response.toString().equals("success"))
                {
                    //Login.sharedLoginPref(GlobalVar.user_id,GlobalVar.login_type, GlobalVar.full_name,GlobalVar.api_token,GlobalVar.customer_name,GlobalVar.customer_code,GlobalVar.schedule_id,GlobalVar.customer_id,GlobalVar.getCurrentTime());
                    //startActivity(new Intent(LoginTakePicture.this, MainActivity.class));
                    //finish();
                    startActivity(new Intent(LoginTakePicture.this, DownloadData.class));
                    finish();
                }
                else if (response.toString().equals("failed"))
                {
                    messageDialog=new MessageDialog(LoginTakePicture.this,"Error","Failed to login. " + response.toString() );
                    messageDialog.messageError();
                }
            }
            @Override
            public void onError(VolleyError error)
            {
                messageDialog=new MessageDialog(LoginTakePicture.this,"Error","No network connectivity.");
                messageDialog.messageError();
            }
        });
    }

    //=============compress image=============================================================================================
    public String getRealPathFromURI(Uri contentUri) {
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

    private byte[] bitmapToFile()
    {
        ByteArrayOutputStream bAO = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bAO);
        return bAO.toByteArray();
    }
}
