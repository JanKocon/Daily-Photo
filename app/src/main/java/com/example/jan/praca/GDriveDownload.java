package com.example.jan.praca;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class GDriveDownload extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_WRITE_PERMISSION = 111;
    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFolder mDriveFolder;
    int counter=0;
    RingProgressBar progressBar1,progressBar2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdrive_download);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            signIn();
            progressBar1 = (RingProgressBar) findViewById(R.id.pb1);
            progressBar1.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    finish();
                }
            });
            progressBar2 = (RingProgressBar) findViewById(R.id.pb2);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                signIn();
                progressBar1 = (RingProgressBar) findViewById(R.id.pb1);
                progressBar1.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        finish();
                    }
                });
                progressBar2 = (RingProgressBar) findViewById(R.id.pb2);

            } else {
                Toast.makeText(this, "Nie udzielono pozwoleń", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);

    }


    private void retriveGoogleFileId() {
        SortOrder sortOrder = new SortOrder.Builder().addSortDescending(SortableField.TITLE).build();
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.MIME_TYPE,"image/webp"))
                                         .setSortOrder(sortOrder).build();
        Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(mDriveFolder,query);
        queryTask.addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadata) {
                int size = 0;
                for(int i=0;i<metadata.getCount();i++)
                {
                    if(!metadata.get(i).isTrashed()) {
                        downloadTask(metadata.get(i).getDriveId(),metadata.get(i).getTitle());
                        size++;
                    }
                }
                if(size == 0) {
                    Toast.makeText(getApplicationContext(),"Nie znaleziono kopii zapasowej",Toast.LENGTH_LONG).show();
                    finish();
                }
                SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("count",size + 1);
                editor.apply();
                progressBar1.setMax(size);
                progressBar2.setMax(size);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Toast.makeText(getApplicationContext(),"Error: " + e.toString(),Toast.LENGTH_LONG).show();
               finish();
            }
        });
    }
    private void downloadTask(DriveId id,String fileName)
    {
        Task<DriveContents> downloadContents = mDriveResourceClient.openFile(id.asDriveFile(),
                                                                    DriveFile.MODE_READ_ONLY);
        downloadContents.addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents driveContents) {
                try {
                    File jFolder = new File(Utils.DIR_PATH );
                    if(!jFolder.exists()) jFolder.mkdirs();
                    File jFile = new File(Utils.DIR_PATH + "/" + fileName);
                    InputStream inputStream = driveContents.getInputStream();
                    FileOutputStream fileStream = new FileOutputStream(jFile);
                    byte buffer[] = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        fileStream.write(buffer, 0, length);
                    }
                    counter++;
                    progressBar1.setProgress(counter);
                    progressBar2.setProgress(counter);
                    fileStream.close();
                    inputStream.close();
                }catch (IOException e){
                    Toast.makeText(getApplicationContext(),
                            "Coś poszło nie tak", Toast.LENGTH_LONG).show();
                }
                mDriveResourceClient.discardContents(driveContents);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Coś poszło nie tak"
                        + e.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean result;
    public boolean checkIfFolderExist()
    {
        result = false;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "DailyPhoto"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE,"application/vnd.google-apps.folder"))
                .build();
        mDriveResourceClient.query(query).addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadata) {
                if(metadata.getCount() == 0) {
                   Toast.makeText(getApplicationContext(),"Dysk nie zawiera plków DailyPhoto",Toast.LENGTH_SHORT).show();
                   finish();
                }
                else{
                    mDriveFolder = metadata.get(0).getDriveId().asDriveFolder();
                    retriveGoogleFileId();
                }
                metadata.release();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Błąd! Sprawdź połączenie z internetem",Toast.LENGTH_LONG).show();

            }
        });

        return result;

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    checkIfFolderExist();
                }
                else{
                    Toast.makeText(getApplicationContext(),"SPRAWDZ POLACZENIE Z INTERNETEM",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}

