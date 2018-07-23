package com.example.jan.praca;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.echodev.resizer.Resizer;




public class Camera extends AppCompatActivity {
    public static final int REQUEST_PERMISSION = 123;
    public static final int REQUEST_START_CAMERA = 1;
    private int count;
    private String mImageFileLocation;
    private ArrayList<Date> lista;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            takePhoto();

        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                Log.i("TAG","shouldShowRequestPermissionRationale");
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mImageFileLocation);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void takePhoto()
    {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri outputFileUri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider", photoFile);   //Uri.fromFile(photoFile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, REQUEST_START_CAMERA);
    }

    File createImageFile() throws IOException
    {
        String name = "zdj" + String.format("%05d", count);
        File storageDir = new File(Utils.DIR_PATH);
        if(!storageDir.exists()) {
            storageDir.mkdirs();
            count = 1;
        }
        File image = new File(Utils.DIR_PATH + "/" + name + Utils.IMG_FORMAT);
        mImageFileLocation = image.getAbsolutePath();
        return image;

    }

        public void commpressImage(String path){
        SharedPreferences sharedPreferences =
                getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE);
        int quality = sharedPreferences.getInt("quality",80);
        Resizer resizer = new Resizer(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                File actualImage = new File(path);

                try {
                    resizer.setTargetLength(1080)
                            .setQuality(quality)
                            .setOutputFormat("WEBP")
                            .setOutputDirPath(Utils.DIR_PATH)
                            .setSourceImage(actualImage)
                            .getResizedFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
            else {
                Toast.makeText(getApplicationContext(),"Nie uzyskano uprawnien",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_START_CAMERA && resultCode == RESULT_OK)
        {
            count++;
            commpressImage(mImageFileLocation);
            galleryAddPic();
            saveData();
            Toast.makeText(getApplicationContext(), Html.fromHtml("&#x2714;"),Toast.LENGTH_LONG).show();
            finish();
        }
        else if (requestCode != RESULT_OK){
            finish();
        }

    }
    void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(lista!= null)
        {
            if(lista.size() >=1)
            {
                if(Utils.compareDate(lista.get(lista.size() -1))) {
                    lista.add(Utils.getToday());
                }
            }
            else {
                lista.add(Utils.getToday());
            }
        }
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        String json = gson.toJson(lista);
        editor.putString("daty",json);
        editor.apply();
    }

    void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", Activity.MODE_PRIVATE);
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        String json = sharedPreferences.getString("daty","");
        Type type = new TypeToken<ArrayList<Date>>(){}.getType();
        lista = gson.fromJson(json,type);
        if(lista == null) lista = new ArrayList<Date>();
        File[] files = new File(Utils.DIR_PATH).listFiles();
        if(files != null) count = files.length +1;
        else count = 1;
    }
}
