package com.example.android_picuture_saving_basic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 0;
    public Button btnPhoto;
    public Button btnSave;
    public int REQUEST_CODE = 100;

    public ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPhoto = (Button)findViewById(R.id.btnPhoto);
        btnSave = (Button)findViewById(R.id.btnSave);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    saveToGallery();

                } else {
                    askPermissions();
                }

                // createImageFile();
                    // takePicture();
                //dispatchTakePictureIntent();
                    // setPic();
                   //  galleryAddPic();
                    // Toast.makeText(getApplicationContext(), "Bild wurde gesichert.", Toast.LENGTH_SHORT).show();



            }
        });

    }

    private void askPermissions() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                saveToGallery();

            } else {
                Toast.makeText(getApplicationContext(), "Please provide the necessary Permission", Toast.LENGTH_SHORT).show();
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void takePicture() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            imagePreview.setImageBitmap(b);
        }
    }


    private void saveToGallery() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imagePreview.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        // VectorDrawable vd = (VectorDrawable) imagePreview.getDrawable();

        // Bitmap b = MainActivity.getBitmap((VectorDrawable) imagePreview.getDrawable());


        FileOutputStream fos = null;
        // File file = Environment.getExternalStorageDirectory();
        // File dir = new File(file.getAbsolutePath() + "/MyPics");
        // File dir = null;
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        // File dir = new File(Environment.DIRECTORY_DCIM, "SaveImage");
        File dir = new File(String.valueOf(getApplicationContext().getFilesDir()));

        File file = new File(dir, System.currentTimeMillis()+ ".jpg");

        // String filename = String.format("%d.jpg", System.currentTimeMillis());
        // File outFile = new File(dir, filename);
        // outFile.mkdirs();
        // outFile.createNewFile();

        dir.mkdirs();
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }



}