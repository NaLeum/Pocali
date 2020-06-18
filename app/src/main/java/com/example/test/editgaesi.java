package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class editgaesi extends BasicActivity {
  //  private final int GET_GALLERY_IMAGE = 200;
  private static int finishcount;

    private String phoexuri;
    private String myAttribute;
    private ImageView imageview;
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mCamera , mCameraFilter, mContents;
    private String neckname, photoUrl,gaesiUrl;
    private String profilePath;
    private StorageReference mountainImagesRef;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private String Path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editgaesi);
         String cafeId = getIntent().getStringExtra("cafeId");
        mCamera = findViewById(R.id.edit_camera);
        mCameraFilter = findViewById(R.id.edit_camerafilter);
        mContents = findViewById(R.id.edit_contents);
        String id = getIntent().getStringExtra("id");
        if(id != null){
            final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
            loaderlayout.setVisibility(View.VISIBLE);
            mStore.collection("post").document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                               String camera=(String)task.getResult().getData().get(FirebaseID.camera);
                                String camerafilter=(String)task.getResult().getData().get(FirebaseID.camerafilter);
                                gaesiUrl=(String)task.getResult().getData().get(FirebaseID.gaesiUrl);
                                String contents=(String)task.getResult().getData().get(FirebaseID.contents);
                                imageview = findViewById(R.id.Iv_gaesi);
                                Glide.with(editgaesi.this)
                                        .load(gaesiUrl)
                                        .into(imageview);
                                mCamera.setText(camera);
                                mCameraFilter.setText(camerafilter);
                                mContents.setText(contents);
                                loaderlayout.setVisibility(View.GONE);
                            }
                        }
                    });
        }

        if(mAuth.getCurrentUser() !=null){

            mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.getResult() != null){
                           neckname=(String)task.getResult().getData().get(FirebaseID.neckname);
                           photoUrl=(String)task.getResult().getData().get(FirebaseID.photoUrl);
                       }
                        }
                    });
        }

        Button editbutton = findViewById(R.id.btn_edit_finish);
        editbutton.setOnClickListener(
                new Button.OnClickListener(){
                    public  void  onClick(View v){
                     if( mAuth.getCurrentUser()!= null){

                         FirebaseStorage storage = FirebaseStorage.getInstance();
                         StorageReference storageRef = storage.getReference();
                      final    FirebaseFirestore mStore = FirebaseFirestore.getInstance();


                       final   String postId = mStore.collection(FirebaseID.post).document().getId();
                         final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                         if (finishcount ==1 || gaesiUrl!=null ) {
                             if(finishcount ==1){
                                 final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
                                 loaderlayout.setVisibility(View.VISIBLE);
                                 String id = getIntent().getStringExtra("id");
                                 final String cafeId = getIntent().getStringExtra("cafeId");
                                 if(id != null){
                                     mountainImagesRef = storageRef.child("posts/" + user.getUid() + "/"+id+".jpg");
                                     Log.e("로그", "수정: " + id);
                                 }else{
                                    mountainImagesRef = storageRef.child("posts/" + user.getUid() + "/"+postId+".jpg");
                                 }


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
                                             String image =  downloadUri.toString();

                                             Map<String,Object> data = new HashMap<>();
                                             data.put(FirebaseID.documentId,mAuth.getCurrentUser().getUid());
                                             data.put(FirebaseID.neckname, neckname);
                                             data.put(FirebaseID.photoUrl, photoUrl);
                                             data.put(FirebaseID.gaesiUrl, image);
                                             data.put(FirebaseID.camera, mCamera.getText().toString());
                                             data.put(FirebaseID.camerafilter, mCameraFilter.getText().toString());
                                             data.put(FirebaseID.contents, mContents.getText().toString());

                                             String id = getIntent().getStringExtra("id");

                                             Log.e("로그", "수정: " + id);



                                             if(id == null){
                                                 data.put("cafeId",cafeId);
                                                 data.put(FirebaseID.postid,postId);
                                                 data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());
                                                 data.put("userlike",0);
                                                 mStore.collection(FirebaseID.post).document(postId).set(data, SetOptions.merge());
                                             }else{

                                                 mStore.collection("post").document(id).update(data);

                                             }


                                             loaderlayout.setVisibility(View.GONE);
                                             finish();

                                         } else {
                                             loaderlayout.setVisibility(View.GONE);
                                             Toast.makeText(editgaesi.this, "실패하셨습니다.",
                                                     Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 });

                             }else{
                                 final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
                                 loaderlayout.setVisibility(View.VISIBLE);
                                 Map<String,Object> data = new HashMap<>();
                                 data.put(FirebaseID.documentId,mAuth.getCurrentUser().getUid());
                                 data.put(FirebaseID.neckname, neckname);
                                 data.put(FirebaseID.photoUrl, photoUrl);
                                 data.put(FirebaseID.gaesiUrl, gaesiUrl);
                                 data.put(FirebaseID.camera, mCamera.getText().toString());
                                 data.put(FirebaseID.camerafilter, mCameraFilter.getText().toString());
                                 data.put(FirebaseID.contents, mContents.getText().toString());

                                 String id = getIntent().getStringExtra("id");
                                 mStore.collection("post").document(id).update(data);




                                 loaderlayout.setVisibility(View.GONE);
                                 finish();


                             }


                         }


                             //       FirebaseID firebaseID = new FirebaseID(neckname,"");
                             //     storeUploader(firebaseID);
                         } else {
                         Toast.makeText(editgaesi.this, "사진을 등록해주세요.",
                                 Toast.LENGTH_SHORT).show();


                     }


                    }


                }
        );

        Button button = findViewById(R.id.btn_sc_ca);
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent1 = new Intent(editgaesi.this, cafesearch.class);

                        //액티비티 시작!
                        startActivity(intent1);
                    }
                }
        );

        ImageButton button1 = findViewById(R.id.btn_profile);
        button1.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent2 = new Intent(editgaesi.this, profilegrid.class);

                        //액티비티 시작!
                        startActivity(intent2);
                    }
                }
        );

        imageview = findViewById(R.id.Iv_gaesi);
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(editgaesi.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(editgaesi.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);


                    if (ActivityCompat.shouldShowRequestPermissionRationale(editgaesi.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {



                    } else {

                        Toast.makeText(editgaesi.this, "권한을 허용해주세요.",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_FROM_ALBUM);
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
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(profilePath);
                        showExif(exif);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Log.e("로그", "에러: " + profilePath);
                    Glide.with(this )
                            .load(profilePath).centerCrop().override(500)
                            .into(imageview);

                }
                break;
            }
            case PICK_FROM_ALBUM:{
                if (resultCode == Activity.RESULT_OK) {

                    if (data == null) {
                        return;
                    }

                    ExifInterface exif = null;

                    photoUri = data.getData();
                    String name_Str = getImageNameToUri(data.getData());


                    String filename = Environment.getExternalStorageDirectory()
                            .getPath() + name_Str;
                    phoexuri = getRealPathFromURI(photoUri);
                    Log.e("사진경로", "" + phoexuri);
                    try {
                        exif = new ExifInterface(phoexuri);
                        showExif(exif);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    cropImage();
                }else  if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            }
            case CROP_FROM_CAMERA:{
                if (resultCode == Activity.RESULT_OK) {
                    finishcount=1;
                    imageview.setImageURI(photoUri);
                  mCamera.setText(myAttribute);
                }else  if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            }
        }

    }

    private String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        return imgName;
    }

    private void showExif(ExifInterface exif) throws IOException {


        exif = new ExifInterface(phoexuri);
        Log.e("메타정보","여기오냐?"+"ㅇㅁㄴㅇㅁㄴ");
      //  Toast.makeText(this, exif.getAttribute(ExifInterface.TAG_DATETIME), Toast.LENGTH_SHORT).show();

        myAttribute = exif.getAttribute(ExifInterface.TAG_MODEL);

        Log.e("메타정보","메타메타"+myAttribute);

       
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
            intent.putExtra("aspectX", 4);
            intent.putExtra("aspectY", 3);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            photoUri = FileProvider.getUriForFile(editgaesi.this,
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



    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0; String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){ column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); }
        return cursor.getString(column_index); }







//    private String getTagString(String tag, ExifInterface exif) {
//        return exif.getAttribute(tag);
//    }
}

