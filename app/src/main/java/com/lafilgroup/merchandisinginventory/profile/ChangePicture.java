package com.lafilgroup.merchandisinginventory.profile;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lafilgroup.merchandisinginventory.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChangePicture extends AppCompatActivity implements View.OnClickListener
{
    ImageView imgPicture;
    Button btnUpload, btnGallery, btnCamera;
    LinearLayout lnrProgress;
    Uri imageUri;

    int PHOTO_CODE=0;
    int GALLERY_CODE=1;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_picture);
        this.setTitle("Change Picture");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnUpload=findViewById(R.id.btnUpload);
        btnGallery=findViewById(R.id.btnGallery);
        btnCamera=findViewById(R.id.btnCamera);
        imgPicture=findViewById(R.id.imgPicture);
        lnrProgress=findViewById(R.id.lnrProgress);
        btnUpload.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        return;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCamera:
//               Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//               startActivityForResult(cameraIntent,PHOTO_CODE);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PHOTO_CODE);
                break;

            case R.id.btnGallery:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Photo"),GALLERY_CODE);
                break;
            case R.id.btnUpload:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PHOTO_CODE && resultCode == RESULT_OK)
        {
//           bitmap = (Bitmap)data.getExtras().get("data");
//           imgPicture.setImageBitmap(bitmap);
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //bitmap = scaleBitmap(bitmap);
                imgPicture.setImageBitmap(bitmap);
                //imgPicture = getRealPathFromURI(imageUri);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            btnUpload.setVisibility(View.VISIBLE);
        }

        else if (requestCode==GALLERY_CODE && resultCode == RESULT_OK)
        {
            Uri uri=data.getData();
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                if (bitmap != null)
                {
                    imgPicture.setImageBitmap(bitmap);
                    btnUpload.setVisibility(View.VISIBLE);
                }
                else
                {
                    btnUpload.setVisibility(View.GONE);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
