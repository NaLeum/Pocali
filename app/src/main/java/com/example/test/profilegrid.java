package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.data.model.PostItem;
import com.example.test.recyclerview.MypageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class profilegrid extends BasicActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    private String neckname, photoUrl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;
    private RecyclerView mypage;
    private MypageAdapter mypageAdapter;

    private GridLayoutManager gridLayoutManager;
    SharedPreferences userdata;
    Gson gson;
    String contact_User;

    private static final String PREFS_FILE = "shared";

    private ArrayList<PostItem> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilegrid);
        mAuth = FirebaseAuth.getInstance();
        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        passPushTokenToServer();
      //  logSharedPreferences(this);
        final FirebaseUser user = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        userdata = getSharedPreferences("shared",MODE_PRIVATE);

        gson = new GsonBuilder().create();
        contact_User = userdata.getString("contact_User","");


        final CardView cardView = findViewById(R.id.profile_cardview);
        cardView.setVisibility(View.GONE);
        drawerLayout = findViewById(R.id.drawer_lot);
        drawerView = findViewById(R.id.drawer);

        ImageButton btn_open = findViewById(R.id.ibt_hamberger);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });





        Button button1 = findViewById(R.id.btn_edit_profile);
        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        drawerLayout.closeDrawers();
                        userdata = getSharedPreferences("shared",MODE_PRIVATE);

                        gson = new GsonBuilder().create();
                        contact_User = userdata.getString("contact_User","");

                        User user1 = gson.fromJson(contact_User,User.class);

                        Intent intent = new Intent(profilegrid.this, editprofile.class);
                        intent.putExtra("orneckname",user1.getNeckname());
                        intent.putExtra("orimage",user1.getPhotoUrl());
                        //액티비티 시작!
                        startActivity(intent);


                    }
                }
        );


        Button button2 = findViewById(R.id.btn_profilecamera);
        button2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(profilegrid.this, CameraActivity.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );
        Button button10 = findViewById(R.id.mask);
        button10.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(profilegrid.this, maskmain.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );


        Button button4 = findViewById(R.id.memos);
        button4.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(profilegrid.this, downgaesi.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );
        Button button5 = findViewById(R.id.memocafe);
        button5.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(profilegrid.this, memocafe.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );
        Button button6 = findViewById(R.id.btn_chatrooms);
        button6.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(profilegrid.this, chatrooms.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );

        Button button = findViewById(R.id.btn_logout);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(profilegrid.this,"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(profilegrid.this, login.class);

                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );

        Button btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

       drawerLayout.addDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mypage = findViewById(R.id.rv_mypage);







      /*  ImageButton button = findViewById(R.id.hamberger);
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent = new Intent(profilegrid.this, navi_draw.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );*/

      /*  public void onImageClick(View view){
            Toast.makeText((profilegrid.this,"이미지 클릭",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(intent,1000);
        }*/
      //  imageview = findViewById(R.id.Iv_uploader);
       // imageview.setOnClickListener(new View.OnClickListener() {
     //       public void onClick(View v) {

       //         Intent intent = new Intent(Intent.ACTION_PICK);
        //        intent. setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
          //      startActivityForResult(intent, GET_GALLERY_IMAGE);
           // }
       // });



    }
    void passPushTokenToServer(){

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Map<String,Object> map = new HashMap<>();
                        map.put("pushToken", token);
                        FirebaseFirestore.getInstance().collection("user").document(uid).update(map);
                        // Log and toast
                    }
                });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);

        }
    }
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {

        }

        @Override
        public void onDrawerClosed(@NonNull View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {

        }
    };




    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("그리드","리스타트");
        mypageAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("그리드","스타트");
        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        final FirebaseUser user = mAuth.getCurrentUser();
        userdata = getSharedPreferences("shared",MODE_PRIVATE);

        gson = new GsonBuilder().create();
        contact_User = userdata.getString("contact_User","");

        if(!contact_User.equals("")){
            User user1 = gson.fromJson(contact_User,User.class);
            if(user1.getUid().equals(user.getUid())){
                TextView textView = findViewById(R.id.tv_nickname);
                textView.setText(user1.getNeckname());
                imageview = findViewById(R.id.Iv_uploader);
                Glide.with(profilegrid.this)
                        .load(user1.getPhotoUrl()).centerCrop()
                        .into(imageview);
                Log.e("쉐어드","유저"+ contact_User);

            }else{
                mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult() != null){
                                    neckname=(String)task.getResult().getData().get(FirebaseID.neckname);
                                    photoUrl=(String)task.getResult().getData().get(FirebaseID.photoUrl);
                                    imageview = findViewById(R.id.Iv_uploader);
                                    TextView textView = findViewById(R.id.tv_nickname);
                                    Glide.with(profilegrid.this)
                                            .load(photoUrl).centerCrop()
                                            .into(imageview);

                                    textView.setText(neckname);
                                    loaderlayout.setVisibility(View.GONE);

                                    contact_User = "";
                                    String Uid = user.getUid();
                                    String name =neckname;
                                    String photourl = photoUrl;
                                    User user1 = new User(Uid,name, photourl);
                                    contact_User = gson.toJson(user1,User.class);
                                    SharedPreferences.Editor editor = userdata.edit();
                                    editor.putString("contact_User",contact_User);
                                    editor.commit();


                                }
                            }
                        });


            }

        }else {
            loaderlayout.setVisibility(View.VISIBLE);
            mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                neckname=(String)task.getResult().getData().get(FirebaseID.neckname);
                                photoUrl=(String)task.getResult().getData().get(FirebaseID.photoUrl);
                                imageview = findViewById(R.id.Iv_uploader);
                                TextView textView = findViewById(R.id.tv_nickname);
                                Glide.with(profilegrid.this)
                                        .load(photoUrl)
                                        .into(imageview);

                                textView.setText(neckname);
                                loaderlayout.setVisibility(View.GONE);

                                contact_User = "";
                                String Uid = user.getUid();
                                String name =neckname;
                                String photourl = photoUrl;
                                User user1 = new User(Uid,name, photourl);
                                contact_User = gson.toJson(user1,User.class);
                                SharedPreferences.Editor editor = userdata.edit();
                                editor.putString("contact_User",contact_User);
                                editor.commit();


                            }
                        }
                    });
        }



        arrayList =new ArrayList<>();


        arrayList.clear();
        final CollectionReference cafeRef = mStore.collection("post");
        mStore.collection("post")
                .whereEqualTo("documentId", mAuth.getCurrentUser().getUid())
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            arrayList.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String postId = String.valueOf(shot.get(FirebaseID.postid));
                                String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                final String username = String.valueOf(shot.get(FirebaseID.neckname));
                                String userImgUrl = String.valueOf((shot.get(FirebaseID.photoUrl)));
                                String postImgUrl = String.valueOf((shot.get(FirebaseID.gaesiUrl)));
                                String camera = String.valueOf(shot.get(FirebaseID.camera));
                                String camerafilter = String.valueOf(shot.get(FirebaseID.camerafilter));
                                String contents = String.valueOf(shot.get(FirebaseID.contents));
                                String cafeId = String.valueOf(shot.get("cafeId"));
                                String userlike = String.valueOf(shot.get("userlike"));
                                PostItem data = new PostItem(documentId,username ,userImgUrl,postImgUrl,contents,camera, camerafilter,postId,cafeId,userlike);
                                arrayList.add(data);
                            }
                            mypageAdapter = new MypageAdapter(arrayList,profilegrid.this);
                            final int numberOfColumns =3 ;
                            mypage.setHasFixedSize(true);
                            mypage.setLayoutManager(new GridLayoutManager(profilegrid.this, numberOfColumns));

                            arrayList =new ArrayList<>();

                            mypage.setAdapter(mypageAdapter);                      }
                    }
                });
    }
    public static void logSharedPreferences(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            Log.e("map values", key + ": " + value);
        }
    }
}



