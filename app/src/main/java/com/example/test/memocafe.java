package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.data.model.CafeItem;
import com.example.test.data.model.PostItem;
import com.example.test.recyclerview.CafeviewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class memocafe extends BasicActivity {
    private RecyclerView mcafeview;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private ArrayList<CafeItem> arrayList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CafeviewAdapter cafeviewAdapter;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memocafe);
        mcafeview = findViewById(R.id.rv_memocafelist);

        arrayList = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
        final PostItem postItem = new PostItem();

        final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
        loaderlayout.setVisibility(View.VISIBLE);

//        final CollectionReference cafeRef = mStore.collection("cafe");
//        final DocumentReference cafeIdRef = cafeRef.document(cafeId);
//        final CollectionReference postRef = cafeIdRef.collection("post");
        Log.e("온스타트","");

        mStore.collection("cafe")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Log.e(",", ""+snapshot.getId());
                                Log.e("", snapshot.getId() + " => " + snapshot.getData().toString());

                                DocumentReference postRef = mStore.document("cafe/"+snapshot.getId());
                                final CollectionReference likesRef = postRef.collection("find");
                                //    CollectionReference posR = mStore.collection("post");
                                //    DocumentReference docR = posR.document(snapshot.getId());
                                //   CollectionReference memoR = docR.collection("memos");
                                // CollectionReference memmo = mStore.collection("post/"+snapshot.getId()+"/memos");
                                Log.e("경로",""+"cafe/"+snapshot.getId()+"/find");
                                likesRef
                                        .whereEqualTo("username",mAuth.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    arrayList.clear();
                                                    for (QueryDocumentSnapshot snap : task2.getResult()) {
                                                        Log.e(",,",""+snap.getId());
                                                        Map<String, Object> shot = snap.getData();
                                                        String cafeId = String.valueOf(shot.get("cafeId"));
                                                        Log.e("postt","ps"+ cafeId);
                                                        mStore.collection("cafe")
                                                                .whereEqualTo("cafeId",cafeId)
                                                                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)

                                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                        if (queryDocumentSnapshots != null) {

                                                                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                                                                Map<String, Object> shot = snap.getData();
                                                                                String cafename = String.valueOf(shot.get("cafename"));
                                                                                String cafelocation = String.valueOf(shot.get("cafelocation"));
                                                                                String cafeimg = String.valueOf((shot.get("cafeimage")));
                                                                                String cafeId = String.valueOf((shot.get("cafeId")));
                                                                                CafeItem data = new CafeItem(cafeimg,cafename,cafelocation,cafeId);
                                                                                arrayList.add(data);
                                                                                Log.e("",""+cafename+cafelocation+cafeimg+cafeId);
                                                                            }
                                                                            cafeviewAdapter=new CafeviewAdapter(memocafe.this , arrayList);
                                                                            linearLayoutManager= new LinearLayoutManager(memocafe.this, LinearLayoutManager.VERTICAL, false);
                                                                            mcafeview.setLayoutManager(linearLayoutManager);
                                                                            mcafeview.setAdapter(cafeviewAdapter);                        }
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






    }
}
