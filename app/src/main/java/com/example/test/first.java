package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.data.model.PostItem;
import com.example.test.recyclerview.MypageAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class first extends BasicActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    private RecyclerView rv_best,rv_recent;
    private MypageAdapter mypageAdapter;
    private ArrayList<PostItem> arrayList;
    private ArrayList<PostItem> arrayList2;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       rv_best = findViewById(R.id.rv_best);
       rv_recent = findViewById(R.id.rv_recent);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

      final  FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        loaderlayout.setVisibility(View.VISIBLE);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        if(user==null){

            Intent intent = new Intent(first.this, login.class);

            //액티비티 시작!
            startActivity(intent);
        }else{

            DocumentReference docRef = mStore.collection("user").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document  != null){
                            if (document.exists()) {

                            } else {
                                Intent intent = new Intent(first.this, MemberinitActivity2.class);

                                //액티비티 시작!
                                startActivity(intent);

                        }}

                    } else {

                    }
                }
            });





            }

        loaderlayout.setVisibility(View.GONE);



        Button button = findViewById(R.id.btn_sc_ca);
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성


                        Intent intent = new Intent(first.this, cafesearch.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );

        ImageButton button1 = findViewById(R.id.btn_profile);
        button1.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent = new Intent(first.this, profilegrid.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("재훈","onStart");
        final int[] eggcount = {0};
        TextView egg = findViewById(R.id.egg);
        egg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eggcount[0]++;
                if (eggcount[0] ==5){
                    Intent intent = new Intent(first.this, minigame.class);

                    //액티비티 시작!
                    startActivity(intent);

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        Log.e("재훈","onResume");

        arrayList =new ArrayList<>();


        arrayList.clear();

        mStore.collection("post")
                .orderBy("userlike", Query.Direction.DESCENDING).limit(3)
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
                            mypageAdapter = new MypageAdapter(arrayList,first.this);
                            final int numberOfColumns =3 ;
                            rv_best.setHasFixedSize(true);
                            rv_best.setLayoutManager(new GridLayoutManager(first.this, numberOfColumns));
                            rv_best.setAdapter(mypageAdapter);                      }
                    }
                });


        arrayList2 =new ArrayList<>();


        arrayList2.clear();

        mStore.collection("post")
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING).limit(3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            arrayList2.clear();
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
                                arrayList2.add(data);
                            }
                            mypageAdapter = new MypageAdapter(arrayList2,first.this);
                            final int numberOfColumns =3 ;
                            rv_recent.setHasFixedSize(true);
                            rv_recent.setLayoutManager(new GridLayoutManager(first.this, numberOfColumns));
                            rv_recent.setAdapter(mypageAdapter);                      }
                    }
                });








    }
    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e("재훈","onRestart");
    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.e("재훈","onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();

        Log.e("재훈","onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("재훈","onDestroy");
    }





}
