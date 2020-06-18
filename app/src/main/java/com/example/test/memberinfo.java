package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;


public class memberinfo extends BasicActivity {
    private ImageView profileimageview;

    private final int GET_GALLERY_IMAGE = 200;
private String profilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberinfo);

        final CardView cardView = findViewById(R.id.profile_cardview);
        cardView.setVisibility(View.GONE);
        profileimageview=findViewById(R.id.profileimageview);




        profileimageview.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {



                        if(cardView.getVisibility() == View.VISIBLE){
                            cardView.setVisibility(View.GONE);
                        }else{
                            cardView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        Button button2 = findViewById(R.id.btn_profilecamera);
        button2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent  = new Intent(memberinfo.this, CameraActivity.class);

                        //액티비티 시작!
                        startActivityForResult(intent,0);
                    }
                }
        );
        Button button3 = findViewById(R.id.btn_profilegallery);
        button3.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, GET_GALLERY_IMAGE);
                    }
                }
        );


        Button button = findViewById(R.id.btn_memberinfo);
            button.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {

                            final String neckname = ((EditText)findViewById(R.id.tv_neckname)).getText().toString();


                            if (neckname.length() > 0) {

                                // loaderLayout.setVisibility(View.VISIBLE);
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

                                //     if (profilePath == null) {
                                //       FirebaseID firebaseID = new FirebaseID(neckname,"");
                                //     storeUploader(firebaseID);
                                //  } else {
                                try {
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



                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                FirebaseID firebaseID = new FirebaseID(neckname, downloadUri.toString());
                                                db.collection("user").document(user.getUid()).set(firebaseID)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(memberinfo.this, "등록에 성공하였습니다.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                //loaderLayout.setVisibility(View.GONE);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(memberinfo.this, "실패하셨습니다.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                //   loaderLayout.setVisibility(View.GONE);

                                                            }
                                                        });
                                                // storeUploader(firebaseID);
                                            } else {
                                                Toast.makeText(memberinfo.this, "실패하셨습니다.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } catch (FileNotFoundException e) {
                                    Log.e("로그", "에러: " + e.toString());
                                }
                                // }
                            } else {
                                Toast.makeText(memberinfo.this, "회원정보를 입력해주세요.",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
     finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    //   profilePath = data.getStringExtra(INTENT_PATH);
                    //   Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                    // buttonBackgroundLayout.setVisibility(View.GONE);

                    profilePath = data.getStringExtra("profilePath");
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    profileimageview.setImageBitmap(bmp);
                }
                break;
            }
        }
    }
    }
