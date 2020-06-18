package com.example.test;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class editprofile extends BasicActivity implements View.OnClickListener {
    SharedPreferences userdata;
    Gson gson;
    String contact_User;

    private ImageView imgMain;
    private Button btnCamera, btnAlbum;

    private ImageView profileImageVIew;
    //  private RelativeLayout loaderLayout;
    private RelativeLayout buttonBackgroundLayout;
    private String profilePath;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private static int finishcount;

    private Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);
        checkPermissions();
        initView();

    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void initView() {
        String orneckname = getIntent().getStringExtra("orneckname");
        String orimage = getIntent().getStringExtra("orimage");
         EditText editTextneckname = findViewById(R.id.necknameEditText);
         editTextneckname.setText(orneckname);

        profileImageVIew = findViewById(R.id.profileImageView);
        buttonBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        final String neckname = ((EditText) findViewById(R.id.necknameEditText)).getText().toString();
        buttonBackgroundLayout.setOnClickListener(this);
        profileImageVIew.setOnClickListener(this);

        Glide.with(editprofile.this)
                .load(orimage)
                .into(profileImageVIew);

        findViewById(R.id.checkButton).setOnClickListener(this);
        findViewById(R.id.picture).setOnClickListener(this);
        findViewById(R.id.gallery).setOnClickListener(this);


    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(editprofile.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(editprofile.this,
                    "dongster.cameranostest.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "nostest_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkButton:
                storageUploader();
                break;
            case R.id.picture:
                takePhoto();
                break;
            case R.id.gallery:
                goToAlbum();
                break;
            case R.id.profileImageView:
                buttonBackgroundLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonsBackgroundLayout:
                buttonBackgroundLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(editprofile.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            profileImageVIew.setImageURI(null);
            profileImageVIew.setImageURI(photoUri);
            buttonBackgroundLayout.setVisibility(View.GONE);
            finishcount=1;
        }
    }

    public void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            photoUri = FileProvider.getUriForFile(editprofile.this,
                    "dongster.cameranostest.provider", tempFile);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);

        }
    }


    private void storageUploader() {
        final String neckname = ((EditText) findViewById(R.id.necknameEditText)).getText().toString();


        if (neckname.length() > 0) {
            final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
            loaderlayout.setVisibility(View.VISIBLE);
            // loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");




            if (finishcount != 1) {

                final FirebaseFirestore mStore = FirebaseFirestore.getInstance();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put(FirebaseID.neckname, neckname);
                mStore.collection(FirebaseID.user).document(user.getUid()).update(userMap);
                userdata = getSharedPreferences("shared",MODE_PRIVATE);
                final String orimage = getIntent().getStringExtra("orimage");
                gson = new GsonBuilder().create();
                contact_User = "";
                final String Uid = user.getUid();
                String name =neckname;
                String photourl = orimage;
                User user1 = new User(Uid,name, photourl);
                contact_User = gson.toJson(user1,User.class);
                SharedPreferences.Editor editor = userdata.edit();
                editor.putString("contact_User",contact_User);
                editor.commit();
                mStore.collection("post")
                        .whereEqualTo("documentId", Uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put(FirebaseID.documentId, user.getUid());
                                        userMap.put(FirebaseID.neckname, neckname);
                                        userMap.put(FirebaseID.photoUrl, orimage);

                                        mStore.collection("post").document(document.getId()).update(userMap);

                                    }

                                    mStore.collection("coment")
                                            .whereEqualTo("documentId", Uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            Map<String, Object> userMap = new HashMap<>();
                                                            userMap.put(FirebaseID.documentId, user.getUid());
                                                            userMap.put(FirebaseID.neckname, neckname);

                                                            mStore.collection("coment").document(document.getId()).update(userMap);
                                                            loaderlayout.setVisibility(View.GONE);
                                                            finish();
                                                        }


                                                    } else {


                                                    }
                                                }
                                            });


                                } else {


                                }
                            }
                        });


                    } else {
                       UploadTask uploadTask = mountainImagesRef.putFile(photoUri);
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
                            final String image =  downloadUri.toString();
                            final FirebaseFirestore mStore = FirebaseFirestore.getInstance();

                             Map<String, Object> userMap = new HashMap<>();
                            userMap.put(FirebaseID.documentId, user.getUid());
                            userMap.put(FirebaseID.neckname, neckname);
                            userMap.put(FirebaseID.photoUrl, image);

                            mStore.collection(FirebaseID.user).document(user.getUid()).update(userMap);


                            userdata = getSharedPreferences("shared",MODE_PRIVATE);

                            gson = new GsonBuilder().create();
                            contact_User = "";
                            final String Uid = user.getUid();
                            String name =neckname;
                            String photourl = image;
                            User user1 = new User(Uid,name, photourl);
                            contact_User = gson.toJson(user1,User.class);
                            SharedPreferences.Editor editor = userdata.edit();
                            editor.putString("contact_User",contact_User);
                            editor.commit();
                            mStore.collection("post")
                                    .whereEqualTo("documentId", Uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put(FirebaseID.documentId, user.getUid());
                                                    userMap.put(FirebaseID.neckname, neckname);
                                                    userMap.put(FirebaseID.photoUrl, image);

                                                    mStore.collection("post").document(document.getId()).update(userMap);

                                                }

                                                mStore.collection("coment")
                                                        .whereEqualTo("documentId", Uid)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                                        Map<String, Object> userMap = new HashMap<>();
                                                                        userMap.put(FirebaseID.documentId, user.getUid());
                                                                        userMap.put(FirebaseID.neckname, neckname);

                                                                        mStore.collection("coment").document(document.getId()).update(userMap);
                                                                        loaderlayout.setVisibility(View.GONE);
                                                                        finish();
                                                                    }


                                                                } else {


                                                                }
                                                            }
                                                        });


                                            } else {


                                            }
                                        }
                                    });

                        } else {
                            loaderlayout.setVisibility(View.GONE);
                            Toast.makeText(editprofile.this, "실패하셨습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(editprofile.this, "회원정보를 입력해주세요.",
                    Toast.LENGTH_SHORT).show();
        }
        }


    }

