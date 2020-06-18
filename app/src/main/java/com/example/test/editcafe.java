package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class editcafe extends BasicActivity {
    private ImageView imageview;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mCafename, mCafelocation;
    private String neckname, photoUrl, gaesiUrl;
    private String profilePath;
    private StorageReference mountainImagesRef;

    private String Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcafe);

        mCafename = findViewById(R.id.edit_cafename);
        mCafelocation = findViewById(R.id.edit_cafelocation);



        if (mAuth.getCurrentUser() != null) {

            mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                neckname = (String) task.getResult().getData().get(FirebaseID.neckname);
                                photoUrl = (String) task.getResult().getData().get(FirebaseID.photoUrl);
                            }
                        }
                    });
        }

        Button editbutton = findViewById(R.id.btn_editcafe);
        editbutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() != null) {

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            final FirebaseFirestore mStore = FirebaseFirestore.getInstance();


                            final String cafeId = mStore.collection("cafe").document().getId();
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                                if (profilePath != null) {
                                    final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
                                    loaderlayout.setVisibility(View.VISIBLE);
                                    try {


                                            mountainImagesRef = storageRef.child("cafes/" + cafeId + ".jpg");


                                        InputStream stream = new FileInputStream(new File(profilePath));
                                        UploadTask uploadTask = mountainImagesRef.putStream(stream);
                                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw Objects.requireNonNull(task.getException());
                                                }
                                                return mountainImagesRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    String image = downloadUri.toString();

                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put(FirebaseID.documentId, mAuth.getCurrentUser().getUid());
                                                    data.put("cafename", mCafename.getText().toString());
                                                    data.put("cafelocation", mCafelocation.getText().toString());
                                                    data.put("cafeimage", image);
                                                        data.put("cafeId", cafeId);
                                                        data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());

                                                        mStore.collection("cafe").document(cafeId).set(data, SetOptions.merge());

                                                    loaderlayout.setVisibility(View.GONE);
                                                    finish();

                                                } else {
                                                    loaderlayout.setVisibility(View.GONE);
                                                    Toast.makeText(editcafe.this, "실패하셨습니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } catch (FileNotFoundException e) {
                                        Log.e("로그", "에러: " + e.toString());
                                    }







                            //       FirebaseID firebaseID = new FirebaseID(neckname,"");
                            //     storeUploader(firebaseID);
                        } else {
                            Toast.makeText(editcafe.this, "사진을 등록해주세요.",
                                    Toast.LENGTH_SHORT).show();


                        }


                    }}

                }

        );
        imageview = findViewById(R.id.Iv_cafeimage);
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(editcafe.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(editcafe.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);


                    if (ActivityCompat.shouldShowRequestPermissionRationale(editcafe.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {



                    } else {

                        Toast.makeText(editcafe.this, "권한을 허용해주세요.",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Intent intent2 = new Intent(editcafe.this, GalleryActivity.class);

                    //액티비티 시작!
                    startActivityForResult(intent2, 0);
                }

            }

            // Intent intent = new Intent(Intent.ACTION_PICK);
            // intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            // startActivityForResult(intent, GET_GALLERY_IMAGE);
            //  }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {

                    // profilePath = data.getStringExtra("profilePath");
                    // Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                    profilePath = data.getStringExtra("profilePath");



                    Log.e("로그", "에러: " + profilePath);
                    Glide.with(this )
                            .load(profilePath).centerCrop().override(500)
                            .into(imageview);

                }
                break;
            }
        }

    }
}


