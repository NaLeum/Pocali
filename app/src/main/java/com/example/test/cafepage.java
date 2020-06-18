package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.data.model.CafeItem;
import com.example.test.data.model.PostItem;
import com.example.test.recyclerview.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class cafepage extends BasicActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private ArrayList<PostItem> listItem;
    private PostAdapter adapter;
    private RecyclerView mPostRecyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PostItem postItem;
    SharedPreferences cafedata;
    Gson gson;
    String contact_Cafe;
int moveposition;
    Thread thread;
    int onesize;
    int twosize;
    boolean isthread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafepage);
        mPostRecyclerView = findViewById(R.id.rv_list);
        final String cafeId = getIntent().getStringExtra("cafeId");

        cafedata = getSharedPreferences("shared",MODE_PRIVATE);
      //  Log.e("쉐어드","쉐어드"+cafedata);
        Log.e("쉐어드2","22"+cafedata.getString(cafeId,""));

        gson = new GsonBuilder().create();
        contact_Cafe = cafedata.getString(cafeId,"");

        if(!contact_Cafe.equals("")) {
            CafeItem cafeItem = gson.fromJson(contact_Cafe, CafeItem.class);

            CheckBox checkBox = findViewById(R.id.cb_cafe);
            ImageView imageview = findViewById(R.id.btn_cafeimage);

            Glide.with(cafepage.this)
                    .load(cafeItem.getCafeimg())
                    .into(imageview);
            TextView mcafename = findViewById(R.id.tv_cafename);
            TextView mcafelocation = findViewById(R.id.tv_cafelocation);
            mcafename.setText(cafeItem.getCafename());
            mcafelocation.setText(cafeItem.getCafelocation());
            checkBox.setChecked(cafeItem.isIscafelike());
            Log.e("쉐어드", "유저" + contact_Cafe);
        }else{

   //     if(cafeId != null){

            mStore.collection("cafe").document(cafeId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                String cafename=(String)task.getResult().getData().get("cafename");
                                String cafelocation=(String)task.getResult().getData().get("cafelocation");
                                String cafeimage=(String)task.getResult().getData().get("cafeimage");
                                final CheckBox checkBox = findViewById(R.id.cb_cafe);
                                ImageView imageview = findViewById(R.id.btn_cafeimage);
                                final boolean[] cafecheck = new boolean[1];
                                Glide.with(cafepage.this)
                                        .load(cafeimage)
                                        .into(imageview);
                                TextView mcafename = findViewById(R.id.tv_cafename);
                                TextView mcafelocation = findViewById(R.id.tv_cafelocation);
                                mcafename.setText(cafename);
                                mcafelocation.setText(cafelocation);

                                DocumentReference postRef = mStore.document("cafe/"+cafeId);
                                final CollectionReference likesRef = postRef.collection("find");

                                DocumentReference like = likesRef.document(mAuth.getUid());
                                likesRef.whereEqualTo("username", mAuth.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                if(task2.getResult().size()>0){
                                                    cafecheck[0] = true;
                                                    checkBox.setChecked(true);
                                                }else{
                                                    cafecheck[0] = false;
                                                    checkBox.setChecked(false);
                                                }

                                            }
                                        });


                                contact_Cafe = "";

                                CafeItem cafeItem = new CafeItem(mAuth.getUid(),cafeimage,cafename,cafelocation, cafeId, cafecheck[0]);
                                contact_Cafe = gson.toJson(cafeItem,CafeItem.class);
                                SharedPreferences.Editor editor = cafedata.edit();
                                editor.putString(cafeId,contact_Cafe);
                                editor.commit();


                            }
                        }
                    });
        }

      final TextView view = findViewById(R.id.tv_cafelocation);
      view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String cflo = view.getText().toString();
              TextView view2 = findViewById(R.id.tv_cafename);
              String cfna = view2.getText().toString();

              Intent intent5 = new Intent(cafepage.this, googlemap2.class);
              intent5.putExtra("cafelocation",cflo);
              intent5.putExtra("cafename",cfna);
              startActivity(intent5);
          }
      });


        Button button = findViewById(R.id.btn_sc_ca);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent1 = new Intent(cafepage.this, cafesearch.class);

                        //액티비티 시작!
                        startActivity(intent1);
                    }
                }
        );

        ImageButton button1 = findViewById(R.id.btn_profile);
        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent2 = new Intent(cafepage.this, profilegrid.class);

                        //액티비티 시작!
                        startActivity(intent2);
                    }
                }
        );

        ImageButton button2 = findViewById(R.id.btn_edit);
        button2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {


                        Intent intent3 = new Intent(cafepage.this, editgaesi.class);
                        intent3.putExtra("cafeId", cafeId);
                        startActivity(intent3);
                    }
                }
        );
        final CheckBox checkBox = findViewById(R.id.cb_cafe);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference postRef = mStore.document("cafe/"+cafeId);
                final CollectionReference likesRef = postRef.collection("find");



                DocumentReference like = likesRef.document(mAuth.getUid());
                likesRef.whereEqualTo("username", mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if(task2.getResult().size()>0){
                                    DocumentReference userlikeRef = likesRef.document(mAuth.getUid());
                                    userlikeRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.e("firestore", " removed");
                                            checkBox.setChecked(false);
                                     //       Toast.makeText(cafepage.this, "즐겨찾기 취소", Toast.LENGTH_SHORT).show();

                                            mStore.collection("cafe").document(cafeId)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if(task.getResult() != null){
                                                                String cafename=(String)task.getResult().getData().get("cafename");
                                                                String cafelocation=(String)task.getResult().getData().get("cafelocation");
                                                                String cafeimage=(String)task.getResult().getData().get("cafeimage");
                                                                final CheckBox checkBox = findViewById(R.id.cb_cafe);
                                                                ImageView imageview = findViewById(R.id.btn_cafeimage);
                                                                final boolean[] cafecheck = new boolean[1];

                                                                TextView mcafename = findViewById(R.id.tv_cafename);
                                                                TextView mcafelocation = findViewById(R.id.tv_cafelocation);

                                                                cafecheck[0] = false;

                                                                contact_Cafe = "";

                                                                CafeItem cafeItem = new CafeItem(mAuth.getUid(),cafeimage,cafename,cafelocation, cafeId, cafecheck[0]);
                                                                contact_Cafe = gson.toJson(cafeItem,CafeItem.class);
                                                                SharedPreferences.Editor editor = cafedata.edit();
                                                                editor.putString(cafeId,contact_Cafe);
                                                                editor.commit();


                                                            }
                                                        }
                                                    });

                                        }
                                    });
                                }else{

                                    Map<String,Object> likeMap = new HashMap<>();
                                    likeMap.put("username" , mAuth.getUid());
                                    likeMap.put("created at", new Date());
                                    likeMap.put("cafeId",cafeId);
                                    likesRef.document(mAuth.getUid()).set(likeMap, SetOptions.merge());
                        //            Toast.makeText(cafepage.this, "즐겨찾기 등록", Toast.LENGTH_SHORT).show();
                                    checkBox.setChecked(true);
                                    mStore.collection("cafe").document(cafeId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.getResult() != null){
                                                        String cafename=(String)task.getResult().getData().get("cafename");
                                                        String cafelocation=(String)task.getResult().getData().get("cafelocation");
                                                        String cafeimage=(String)task.getResult().getData().get("cafeimage");
                                                        final CheckBox checkBox = findViewById(R.id.cb_cafe);
                                                        ImageView imageview = findViewById(R.id.btn_cafeimage);
                                                        final boolean[] cafecheck = new boolean[1];

                                                        TextView mcafename = findViewById(R.id.tv_cafename);
                                                        TextView mcafelocation = findViewById(R.id.tv_cafelocation);

                                                        cafecheck[0] = true;

                                                        contact_Cafe = "";

                                                        CafeItem cafeItem = new CafeItem(mAuth.getUid(),cafeimage,cafename,cafelocation, cafeId, cafecheck[0]);
                                                        contact_Cafe = gson.toJson(cafeItem,CafeItem.class);
                                                        SharedPreferences.Editor editor = cafedata.edit();
                                                        editor.putString(cafeId,contact_Cafe);
                                                        editor.commit();


                                                    }
                                                }
                                            });
                                }

                            }
                        });

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        final PostItem postItem = new PostItem();
        listItem = new ArrayList<>();
        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        loaderlayout.setVisibility(View.VISIBLE);
        final String cafeId = getIntent().getStringExtra("cafeId");
        final String movepostid = getIntent().getStringExtra("movepostid");
//        final CollectionReference cafeRef = mStore.collection("cafe");
//        final DocumentReference cafeIdRef = cafeRef.document(cafeId);
//        final CollectionReference postRef = cafeIdRef.collection("post");
        mStore.collection(FirebaseID.post)
                .whereEqualTo("cafeId", cafeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mStore.collection(FirebaseID.post)
                                    .whereEqualTo("cafeId", cafeId)
                                    .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        private int likescount;

                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots != null) {
                                                listItem.clear();
                                                int count = 0;
                                                Log.e("",""+count +"");
                                                onesize = queryDocumentSnapshots.getDocuments().size();
                                                String onee = Integer.toString(onesize);
                                                Log.e("1사이즈","1사이즈"+onee);
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
                                                    String userlike = String.valueOf(shot.get("userlike"));
                                                    DocumentReference postRef =  snap.getReference();
                                                    if(movepostid!=null){
                                                        if(movepostid.equals(postId)){

                                                            moveposition = count;
                                                            Log.e("",""+ moveposition);

                                                        }
                                                    }

                                                    count++;
                                                    PostItem data = new PostItem(documentId,username ,userImgUrl,postImgUrl,contents,camera, camerafilter,postId,cafeId,userlike);
                                                    listItem.add(data);
                                                }
                                                adapter = new PostAdapter(cafepage.this, listItem);

                                                mPostRecyclerView.setLayoutManager(new LinearLayoutManager(cafepage.this, LinearLayoutManager.VERTICAL, false));
                                                mPostRecyclerView.setAdapter(adapter);
                                                mPostRecyclerView.getLayoutManager().scrollToPosition(moveposition);
                                            }
                                        }
                                    });

                        } else {

                        }
                    }
                });
//        postRef

       loaderlayout.setVisibility(View.GONE);
        thread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(isthread){
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("스레드","스레드 도는");
                    final String cafeId = getIntent().getStringExtra("cafeId");
                    mStore.collection(FirebaseID.post)
                            .whereEqualTo("cafeId", cafeId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        twosize=task.getResult().size();
                                        String too = Integer.toString(twosize);
                                        Log.e("2사이즈","2사이즈"+too);
                                        if(twosize>onesize){
                                            onesize=twosize;
                                            isthread =false;
                                            thread.interrupt();
                                            handler.sendEmptyMessage(0);

                                        }
                                    } else {

                                    }
                                }
                            });


                }
            }
        };
        isthread =true;
        thread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();
        isthread =false;
        thread.interrupt();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.e("메세지보","메세지 보냄");
            View layout = findViewById(R.id.cape);
            final Snackbar snackbar = Snackbar.make(layout, "새글이 있습니다.",Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPostRecyclerView.getLayoutManager().scrollToPosition(0);
                    isthread=true;
                    onStart();
                    snackbar.dismiss();
                }
            });
            View view = snackbar.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
// calculate actionbar height
            TypedValue tv = new TypedValue();
            int actionBarHeight=0;
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
           {
               actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
           }
//// set margin
           params.setMargins(0, actionBarHeight, 0, 0);

            view.setLayoutParams(params);

            snackbar.show();
        }





    };

}


