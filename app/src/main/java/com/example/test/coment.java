package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.data.model.ComentItem;
import com.example.test.recyclerview.ComentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class coment extends BasicActivity {
    private ArrayList<ComentItem> arrayList;
    //  private PostAdapter adapter;
    // private RecyclerView mPostRecyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private RecyclerView mComentRecyclerView;
    private String neckname, photoUrl;
    private ComentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postcoment);
        mComentRecyclerView = findViewById(R.id.rv_coment);
        final String postid = getIntent().getStringExtra("postid");
        final String camera = getIntent().getStringExtra("camera");
        final String camerafilter = getIntent().getStringExtra("camerafilter");
        final String context = getIntent().getStringExtra("context");


        TextView tv_camera_model = findViewById(R.id.tv_camera_model);
        tv_camera_model.setText(camera);
        TextView tv_camera_filter = findViewById(R.id.tv_camera_filter);
        tv_camera_filter.setText(camerafilter);
        TextView tv_PostText = findViewById(R.id.tv_PostText);
        tv_PostText.setText(context);

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
        Button button = findViewById(R.id.btn_sc_ca);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent1 = new Intent(coment.this, cafesearch.class);

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

                        Intent intent2 = new Intent(coment.this, profilegrid.class);

                        //액티비티 시작!
                        startActivity(intent2);
                    }
                }
        );
        Button button2 = findViewById(R.id.btn_comentfinish);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final   String comentids = mStore.collection("coment").document().getId();
                final EditText editText = findViewById(R.id.et_coment);
                 String  coments =editText.getText().toString();
                if(coments != null){
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.postid, postid);
                    data.put(FirebaseID.neckname,neckname);
                    data.put(FirebaseID.documentId,mAuth.getUid());
                    data.put("coment", coments);
                    data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());
                    data.put("comentid", comentids);


                    mStore.collection("coment").document(comentids)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("댓글","성공");
                                    editText.setText(null);
                                    onResume();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });




                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        final ComentItem comentItem = new ComentItem();
        arrayList = new ArrayList<>();
        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        loaderlayout.setVisibility(View.VISIBLE);
        final String postid = getIntent().getStringExtra("postid");
//        final CollectionReference cafeRef = mStore.collection("cafe");
//        final DocumentReference cafeIdRef = cafeRef.document(cafeId);
//        final CollectionReference postRef = cafeIdRef.collection("post");
//
//        postRef
        arrayList.clear();
        mStore.collection("coment")
                .whereEqualTo("postid", postid)
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
                                String coment = String.valueOf((shot.get("coment")));
                                String comentid = String.valueOf((shot.get("comentid")));

                                ComentItem data = new ComentItem(username, coment, documentId, postId,comentid);
                                arrayList.add(data);
                            }
                            adapter = new ComentAdapter(arrayList, coment.this);

                            mComentRecyclerView.setLayoutManager(new LinearLayoutManager(coment.this, LinearLayoutManager.VERTICAL, false));
                            mComentRecyclerView.setAdapter(adapter);
                        }
                    }
                });
        loaderlayout.setVisibility(View.GONE);


    }


}
