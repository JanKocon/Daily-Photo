package com.example.jan.praca;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.widget.DataBufferAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class GDriveUpload extends Activity {

    private static final String TAG = "TAG";
    private static final int REQUEST_CODE_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveFolder mDriveFolder;
    List<String> filesToSave;
    int counter=0;
    RingProgressBar progressBar1,progressBar2;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_gugl);
        filesToSave = Utils.getImages();
        if(filesToSave.size() > 0) {
            signIn();
            progressBar1 = (RingProgressBar) findViewById(R.id.progress_bar1);
            progressBar1.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    Toast.makeText(getApplicationContext(), "UDALO SIE", Toast.LENGTH_LONG).show();
                }
            });
            progressBar2 = (RingProgressBar) findViewById(R.id.progress_bar2);
            if(filesToSave.size() > 1) {
                progressBar1.setMax(filesToSave.size() - 1);
                progressBar2.setMax(filesToSave.size() - 1);
            }

        }else{
            Toast.makeText(getApplicationContext(),"Galeria zdjęć jest pusta", Toast.LENGTH_LONG).show();
            finish();
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

        private void createFile(Bitmap bitmap) {
            final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
            final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                Tasks.whenAll(rootFolderTask, createContentsTask)
                        .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                            @Override
                            public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                                DriveFolder parent = rootFolderTask.getResult();
                                DriveContents contents = createContentsTask.getResult();
                                OutputStream outputStream = contents.getOutputStream();
                                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bitmapStream);
                                try {
                                    outputStream.write(bitmapStream.toByteArray());
                                } catch (Exception e) {
                                }

                                String currentFile = filesToSave.get(counter);
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(currentFile.substring(currentFile.lastIndexOf("/") + 1))
                                        .setMimeType("image/webp")
                                        .setStarred(true)
                                        .build();

                                return mDriveResourceClient.createFile(mDriveFolder, changeSet, contents);
                            }
                        }).addOnSuccessListener(this,
                        driveFile -> {
                            Log.i(TAG,driveFile.toString());
                        })
                        .addOnFailureListener(this, e -> {
                            Toast.makeText(getApplicationContext(),"Coś poszło nie tak",Toast.LENGTH_LONG).show();
                            finish();
                        }).addOnCompleteListener(this, driveFile -> {
                            counter++;

                            metoda();

                });

    }
    void metoda()
    {
        if(counter<filesToSave.size()) {
            createFile(BitmapFactory.decodeFile(filesToSave.get(counter)));
            progressBar1.setProgress(counter);
            progressBar2.setProgress(counter);
        }
        else{
            Toast.makeText(getApplicationContext(),"Sukces",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void createFolder1() {
        mDriveResourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("DailyPhoto")
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();

                    return mDriveResourceClient.createFolder(parentFolder, changeSet);
                }).addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {

            }
        })
                .addOnFailureListener(this, e -> {

                    finish();
                }).addOnCompleteListener(new OnCompleteListener<DriveFolder>() {
            @Override
            public void onComplete(@NonNull Task<DriveFolder> task) {
                if(task.getResult()!=null) {
                    mDriveFolder = task.getResult();
                    metoda();
                }
            }
        });

    }
    public boolean folderExist()
    {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "DailyPhoto"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE,"application/vnd.google-apps.folder"))
                .build();
        mDriveResourceClient.query(query).addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadata) {
                Log.i(TAG,metadata.getCount() + "");
            }
        }).addOnCompleteListener(new OnCompleteListener<MetadataBuffer>() {
            @Override
            public void onComplete(@NonNull Task<MetadataBuffer> task) {
                if(task.getResult().getCount() == 0) createFolder1();
                else {
                    mDriveFolder = task.getResult().get(0).getDriveId().asDriveFolder();
                    metoda();
                }
            }
        });
return true;
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    folderExist();
                }
                else{
                    Toast.makeText(getApplicationContext(),"SPRAWDZ POLACZENIE",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}
