package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class editprofile2 extends BasicActivity {
    private static final String TAG = "MemberInitActivity";

    private ImageView profileImageVIew;
    //  private RelativeLayout loaderLayout;
    private RelativeLayout buttonBackgroundLayout;
    private String profilePath;
    SharedPreferences userdata;
    Gson gson;
    String contact_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);





        //  loaderLayout = findViewById(R.id.loaderLayout);
        profileImageVIew = findViewById(R.id.profileImageView);
        buttonBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        final String neckname = ((EditText)findViewById(R.id.necknameEditText)).getText().toString();
        buttonBackgroundLayout.setOnClickListener(onClickListener);
        profileImageVIew.setOnClickListener(onClickListener);

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("크롭사진받기","사진받기"+profilePath);
                    Uri myUri =    getUriFromPath(profilePath);
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(myUri, "image/*");

                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    intent.putExtra("return-data", true);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission( "com.example.test", myUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, 1);


                }
                break;
            }
            case 1:{

                buttonBackgroundLayout.setVisibility(View.GONE);


                Log.e("사진받기", "에러: " + profilePath);
                //  Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                try
                {
                    // 비트맵 이미지로 가져온다
                    profilePath = data.getStringExtra("profilePath");
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);

                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(profilePath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    bmp = rotate(bmp, exifDegree);

                    // 변환된 이미지 사용

                    Glide.with(this )
                            .load(bmp).centerCrop().override(500)
                            .into(profileImageVIew);
                }
                catch(Exception e)
                {

                    Toast.makeText(editprofile2.this, "오류발생",
                            Toast.LENGTH_SHORT).show();

                }

            }
            break;

        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkButton:
                    storageUploader();
                    break;
                case R.id.profileImageView:
                    buttonBackgroundLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.buttonsBackgroundLayout:
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.picture:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.gallery:

                    if (ContextCompat.checkSelfPermission(editprofile2.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(editprofile2.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        if (ActivityCompat.shouldShowRequestPermissionRationale(editprofile2.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else {
                            Toast.makeText(editprofile2.this, "권한을 허용해주세요.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        myStartActivity(GalleryActivity.class);
                    }


                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);

                } else {
                    Toast.makeText(editprofile2.this, "권한을 허용해주세요.",
                            Toast.LENGTH_SHORT).show();

                }

            }
        }
    }

    private void storageUploader() {
        final String neckname = ((EditText)findViewById(R.id.necknameEditText)).getText().toString();



        if (neckname.length() > 0) {
            final   RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
            loaderlayout.setVisibility(View.VISIBLE);
            // loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                Toast.makeText(editprofile2.this, "사진을 등록해주세요.",
                        Toast.LENGTH_SHORT).show();
            } else {
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
                                final String image =  downloadUri.toString();
                                final FirebaseFirestore mStore = FirebaseFirestore.getInstance();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put(FirebaseID.documentId, user.getUid());
                                userMap.put(FirebaseID.neckname, neckname);
                                userMap.put(FirebaseID.photoUrl, image);

                                mStore.collection(FirebaseID.user).document(user.getUid()).update(userMap);
                                loaderlayout.setVisibility(View.GONE);

                                userdata = getSharedPreferences("shared",MODE_PRIVATE);

                                gson = new GsonBuilder().create();
                                contact_User = "";
                                String Uid = user.getUid();
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
                                                } else {


                                                }
                                            }
                                        });
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

                                                    }
                                                } else {


                                                }
                                            }
                                        });


                                finish();

                            } else {
                                loaderlayout.setVisibility(View.GONE);
                                Toast.makeText(editprofile2.this, "실패하셨습니다.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            Toast.makeText(editprofile2.this, "회원정보를 입력해주세요.",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);


}




    public Uri getUriFromPath(String filePath) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null);

        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        return uri;
    }
    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }




}

