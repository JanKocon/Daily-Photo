package com.example.jan.praca;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.List;


public class GalleryMainActivity extends AppCompatActivity {


    public List<String> galleryItems;
    private static final int RC_READ_STORAGE = 5;
    GalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        RecyclerView recyclerViewGallery = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 3));
        mGalleryAdapter = new GalleryAdapter(this);
        recyclerViewGallery.setAdapter(mGalleryAdapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            galleryItems = Utils.getImages();
            if(galleryItems.size() == 0) {
                Toast.makeText(getApplicationContext(),"Galeria jest pusta",Toast.LENGTH_LONG).show();
                finish();
            }
            else
                mGalleryAdapter.addGalleryItems(galleryItems);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_READ_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryItems = Utils.getImages();
                mGalleryAdapter.addGalleryItems(galleryItems);
            } else {
                Toast.makeText(this, "Nie uzyskano uprawnien", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
