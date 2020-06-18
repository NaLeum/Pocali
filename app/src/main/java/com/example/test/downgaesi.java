package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.data.model.PostItem;
import com.example.test.recyclerview.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class downgaesi extends BasicActivity{
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private ArrayList<PostItem> listItem;
    private PostAdapter adapter;
    private RecyclerView mPostRecyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PostItem postItem;
    SharedPreferences cafedata;
    Gson gson;
    String contact_Cafe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downgaesi);
        mPostRecyclerView = findViewById(R.id.rv_memolist);


        //  LinearLayout llScrollParent = findViewById(R.id.ll_list);

//        for (int i = 0; i < 5; i++) {
//
//            PostItem item = new PostItem(true, 125, "Jaehoon",
//                    "l", "l", "wow", "foodie", "맛있게4");
//            listItem.add(i, item);
//
//        }



      /*  for (PostItem item : listItem){
            View v = View.inflate(this,R.layout.gaesi, null);
            TextView tvUerName = v.findViewById(R.id.tv_username);
            TextView tvPostText = v.findViewById(R.id.tv_PostText);
            tvUerName.setText(item.getUsername());
            tvPostText.setText(item.getPostText());
            llScrollParent.addView(v);
        }*/


        Button button = findViewById(R.id.btn_sc_ca);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent1 = new Intent(downgaesi.this, cafesearch.class);

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

                        Intent intent2 = new Intent(downgaesi.this, profilegrid.class);

                        //액티비티 시작!
                        startActivity(intent2);
                    }
                }
        );



    }

    @Override
    protected void onStart() {
        super.onStart();
        final PostItem postItem = new PostItem();
        listItem = new ArrayList<>();
        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        loaderlayout.setVisibility(View.VISIBLE);

//        final CollectionReference cafeRef = mStore.collection("cafe");
//        final DocumentReference cafeIdRef = cafeRef.document(cafeId);
//        final CollectionReference postRef = cafeIdRef.collection("post");
        Log.e("온스타트","");

        mStore.collection("post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Log.e(",", ""+snapshot.getId());
                                Log.e("", snapshot.getId() + " => " + snapshot.getData().toString());

                                DocumentReference postRef = mStore.document("post/"+snapshot.getId());
                                final   CollectionReference likesRef = postRef.collection("memos");
                            //    CollectionReference posR = mStore.collection("post");
                            //    DocumentReference docR = posR.document(snapshot.getId());
                             //   CollectionReference memoR = docR.collection("memos");
                             // CollectionReference memmo = mStore.collection("post/"+snapshot.getId()+"/memos");
                              Log.e("경로",""+"post/"+snapshot.getId()+"/memos");
                                     likesRef
                                        .whereEqualTo("username",mAuth.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    listItem.clear();
                                                    for (QueryDocumentSnapshot snap : task2.getResult()) {
                                                        Log.e(",,",""+snap.getId());
                                                        Map<String, Object> shot = snap.getData();
                                                        String postId = String.valueOf(shot.get("postId"));
                                                        Log.e("postt","ps"+ postId);
                                                        mStore.collection("post")
                                                                .whereEqualTo("postid",postId)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                        if (task2.isSuccessful()) {

                                                                            for (QueryDocumentSnapshot snap : task2.getResult()) {
                                                                                Log.e(",,",""+snap.getId());
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

                                                                                PostItem data = new PostItem(documentId,username ,userImgUrl,postImgUrl,contents,camera, camerafilter,postId,userlike);
                                                                                listItem.add(data);
                                                                            }
                                                                            adapter = new PostAdapter(downgaesi.this, listItem);

                                                                            mPostRecyclerView.setLayoutManager(new LinearLayoutManager(downgaesi.this, LinearLayoutManager.VERTICAL, false));
                                                                            mPostRecyclerView.setAdapter(adapter);
                                                                        } else {
                                                                            Log.e("","실패2");
                                                                        }
                                                                        loaderlayout.setVisibility(View.GONE);
                                                                    }
                                                                });

                                                    }

                                                } else {
                                                    Log.e("","실패2");
                                                }
                                                loaderlayout.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        } else {
                                Log.e("","실패");
                        }
                    }
                });







//        mStore.collection("memos")
//                .whereEqualTo("username", mAuth.getUid())
//                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
//                .get()
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    private int likescount;
//
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        if (queryDocumentSnapshots != null) {
//                            listItem.clear();
//                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
//                                Map<String, Object> shot = snap.getData();
//                                String postId = String.valueOf(shot.get(FirebaseID.postid));
//                                String documentId = String.valueOf(shot.get(FirebaseID.documentId));
//                                final String username = String.valueOf(shot.get(FirebaseID.neckname));
//                                String userImgUrl = String.valueOf((shot.get(FirebaseID.photoUrl)));
//                                String postImgUrl = String.valueOf((shot.get(FirebaseID.gaesiUrl)));
//                                String camera = String.valueOf(shot.get(FirebaseID.camera));
//                                String camerafilter = String.valueOf(shot.get(FirebaseID.camerafilter));
//                                String contents = String.valueOf(shot.get(FirebaseID.contents));
//                                DocumentReference postRef =  snap.getReference();
////                            final CollectionReference likesRef = postRef.collection("likes");
////
////                            likesRef.get()
////                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                        @Override
////                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                                QuerySnapshot likesResult = task.getResult();
////
////                                               likescount  = likesResult.size();
////
////                                              postItem.setPostLikeCount(likescount);
////                                             likesRef.whereEqualTo("username", mAuth.getUid())
////                                                     .get()
////                                                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                                         @Override
////                                                         public void onComplete(@NonNull Task<QuerySnapshot> task2) {
////                                                            if(task2.getResult().size()>0){
////                                                   DocumentSnapshot likeDocument  =  task2.getResult().getDocuments().get(0);
////                                                          postItem.setLikeId(likeDocument.getId());
////                                                          postItem.setUserLike(true);
////                                                                Log.e("라이크아이디 ",likeDocument.getId());
////
////                                                                Log.e("좋아요 ", String.valueOf(postItem.isUserLike()));
////                                                            }else{
////
////                                                                postItem.setUserLike(false);
////
////                                                                Log.e("싫어요 ", String.valueOf(postItem.isUserLike()));
////                                                                       //좋아요를 안눌렀다
////                                                            }
////
////                                                         }
////                                                     });
////                                        }
////                                    });
//
//
//                                PostItem data = new PostItem(documentId,username ,userImgUrl,postImgUrl,contents,camera, camerafilter,postId);
//                                listItem.add(data);
//                            }
//                            adapter = new PostAdapter(downgaesi.this, listItem);
//
//                            mPostRecyclerView.setLayoutManager(new LinearLayoutManager(downgaesi.this, LinearLayoutManager.VERTICAL, false));
//                            mPostRecyclerView.setAdapter(adapter);
//                        }
//                    }
//                });
//        loaderlayout.setVisibility(View.GONE);
//
//
//    }

    }

}