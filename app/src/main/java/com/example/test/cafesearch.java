package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.data.model.CafeItem;
import com.example.test.recyclerview.CafeviewAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class cafesearch extends BasicActivity {
   private RecyclerView mcafeview;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
   private ArrayList<CafeItem> arrayList;
   private CafeviewAdapter cafeviewAdapter;
   private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafesearch);

        mcafeview = findViewById(R.id.rv_cafelist);
        linearLayoutManager= new LinearLayoutManager(this);

        arrayList = new ArrayList<>();
        SearchView searchView = findViewById(R.id.editSearch);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cafeviewAdapter.getFilter().filter(newText);
                return false;
            }
        });












        Button button = findViewById(R.id.btn_cafeadd);
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                       // CafeItem cafeItem = new CafeItem();
                        Intent intent = new Intent(cafesearch.this, editcafe.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        final CollectionReference cafeRef = mStore.collection("cafe");
        mStore.collection("cafe")
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            arrayList.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String cafename = String.valueOf(shot.get("cafename"));
                                String cafelocation = String.valueOf(shot.get("cafelocation"));
                                String cafeimg = String.valueOf((shot.get("cafeimage")));
                                String cafeId = String.valueOf((shot.get("cafeId")));
                                CafeItem data = new CafeItem(cafeimg,cafename,cafelocation,cafeId);
                                arrayList.add(data);
                            }
                            cafeviewAdapter=new CafeviewAdapter(cafesearch.this , arrayList);
                            linearLayoutManager= new LinearLayoutManager(cafesearch.this, LinearLayoutManager.VERTICAL, false);
                            mcafeview.setLayoutManager(linearLayoutManager);
                            mcafeview.setAdapter(cafeviewAdapter);                        }
                    }
                });

    }
}
