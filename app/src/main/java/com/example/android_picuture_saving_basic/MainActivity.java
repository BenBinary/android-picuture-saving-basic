package com.example.android_picuture_saving_basic;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContextWrapper;
import android.content.Intent;
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

                try {
                    saveToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
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

    String currentPhotoPath;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // ## Difficult Part ###
        Log.d("Method", "Create Image File");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();

        // String sStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/Foldername/";
        // File storageDir = new File(sStorageDir);

        // File storageDir =  new File("./");


        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        scanMedia(currentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Log.d("Method", "dispath  Take Picture Intent");

        // Ensure that there's a camera activity to handle the intent
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            Log.d("Step 1", "Intent has camera activity");

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("Create Image File", "Saving was successful");

            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("Create Image File", "Saving was NOT successful");
    
            }
            // Continue only if the File was successfully created
            Log.d("Picture", "Try to save picture");
            if (photoFile != null) {
                Log.d("Picture", "Possible to save the picture");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android_picuture_saving_basic.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            /*
        } else {
            Log.d("Dispatch Take Picture Intent ", "Cant finde Camera Activity");
        } */
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);


    }

    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(scanFileIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imagePreview.getWidth();
        int targetH = imagePreview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imagePreview.setImageBitmap(bitmap);
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", getApplicationContext().MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private void saveToGallery() throws IOException {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imagePreview.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        // VectorDrawable vd = (VectorDrawable) imagePreview.getDrawable();

        // Bitmap b = MainActivity.getBitmap((VectorDrawable) imagePreview.getDrawable());


        FileOutputStream fos = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/MyPics");

        if (dir.exists() == false) {
            dir.mkdir();
        }

        String filename = String.format("%d.png", System.currentTimeMillis());
        File outFile = new File(dir, filename);
        outFile.mkdirs();
        outFile.createNewFile();

        try {

            fos = new FileOutputStream(outFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }


    }



}