package com.example.test.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.cafepage;
import com.example.test.data.model.CafeItem;
import com.example.test.googlemap2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CafeviewAdapter extends RecyclerView.Adapter<CafeviewAdapter.CafeviewHolder> implements Filterable {
    SharedPreferences cafedata;
    Gson gson;
    String contact_Cafe;



    private ArrayList<CafeItem> arrayList;
    private ArrayList<CafeItem> arrayListFull;
private FirebaseAuth mAuth = FirebaseAuth.getInstance();
final private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private Context context;

    public CafeviewAdapter(Context context, ArrayList<CafeItem> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        arrayListFull = new ArrayList<>(arrayList);
    }



    @NonNull
    @Override
    public CafeviewAdapter.CafeviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cafedata,parent,false);

        CafeviewHolder cafeviewHolder = new CafeviewHolder(view);


        return cafeviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CafeviewAdapter.CafeviewHolder holder, int position) {
        final CafeItem item = arrayList.get(position);

        cafedata = context.getSharedPreferences("shared",MODE_PRIVATE);

        Log.e("쉐어드","쉐어드"+cafedata);
        Log.e("쉐어드2","22"+cafedata.getString(item.getCafeId(),""));

        gson = new GsonBuilder().create();
        contact_Cafe = cafedata.getString(item.getCafeId(),"");

        if(!contact_Cafe.equals("")) {
            CafeItem cafeItem = gson.fromJson(contact_Cafe, CafeItem.class);
                if(cafeItem.getUserid()!=null&&cafeItem.getUserid().equals(mAuth.getUid())){
                    Glide.with(holder.btn_cafeimage.getContext())
                            .load(cafeItem.getCafeimg())
                            .into((holder.btn_cafeimage));

                    holder.tv_cafename.setText(cafeItem.getCafename());
                    holder.tv_cafelocation.setText(cafeItem.getCafelocation());
                    holder.cb_cafe.setChecked(cafeItem.isIscafelike());
                    Log.e("쉐어드", "유저" + contact_Cafe);

                }else{
                    Glide.with(holder.btn_cafeimage.getContext())
                            .load(arrayList.get(position).getCafeimg())
                            .into(holder.btn_cafeimage);
                    holder.tv_cafename.setText(arrayList.get(position).getCafename());
                    holder.tv_cafelocation.setText(arrayList.get(position).getCafelocation());


                    DocumentReference postRef = mStore.document("cafe/" + item.getCafeId());
                    final CollectionReference likesRef = postRef.collection("find");
                    Log.e("firestore", String.valueOf(item.isIscafelike()));
                    DocumentReference like = likesRef.document(mAuth.getUid());
                    likesRef.whereEqualTo("username", mAuth.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                    if (task2.getResult().size() > 0) {

                                        holder.cb_cafe.setChecked(true);
                                    } else {
//좋아요 안눌린 상태
                                        holder.cb_cafe.setChecked(false);
                                    }

                                }
                            });

                }

        }else {


            Glide.with(holder.btn_cafeimage.getContext())
                    .load(arrayList.get(position).getCafeimg())
                    .into(holder.btn_cafeimage);
            holder.tv_cafename.setText(arrayList.get(position).getCafename());
            holder.tv_cafelocation.setText(arrayList.get(position).getCafelocation());


            DocumentReference postRef = mStore.document("cafe/" + item.getCafeId());
            final CollectionReference likesRef = postRef.collection("find");
            Log.e("firestore", String.valueOf(item.isIscafelike()));
            DocumentReference like = likesRef.document(mAuth.getUid());
            likesRef.whereEqualTo("username", mAuth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            if (task2.getResult().size() > 0) {

                                holder.cb_cafe.setChecked(true);
                            } else {
//좋아요 안눌린 상태
                                holder.cb_cafe.setChecked(false);
                            }

                        }
                    });


        }

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size():0);
    }

    @Override
    public Filter getFilter() {
        return cafefilter;
    }
    private Filter cafefilter =new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CafeItem> filteredList = new ArrayList<>();

            if(constraint == null|| constraint.length()==0){
                     filteredList.addAll(arrayListFull);
            }else{
                String filterpattern = constraint.toString().toLowerCase().trim();

                for(CafeItem item : arrayListFull){
                    if(item.getCafename().toLowerCase().contains(filterpattern)||item.getCafelocation().toLowerCase().contains(filterpattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results =new FilterResults();
            results.values =filteredList;
            return results;
         }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class CafeviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView btn_cafeimage;
        protected TextView tv_cafename;
        protected TextView tv_cafelocation;
        protected CheckBox cb_cafe;

        public CafeviewHolder(@NonNull View itemView) {
            super(itemView);
            this.btn_cafeimage = itemView.findViewById(R.id.btn_cafeimage);
            this.tv_cafename = itemView.findViewById(R.id.tv_cafename);
            this.tv_cafelocation = itemView.findViewById(R.id.tv_cafelocation);
            this.cb_cafe= itemView.findViewById(R.id.cb_cafe);
            btn_cafeimage.setOnClickListener(this);
            tv_cafelocation.setOnClickListener(this);
            cb_cafe.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            switch (v.getId()){
                case  R.id.btn_cafeimage:

                        Intent intent = new Intent(context, cafepage.class);
                        intent.putExtra("cafeId",arrayList.get(position).getCafeId());
                        Log.e("",""+arrayList.get(position).getCafeId());
                        context.startActivity(intent);


                    break;

                case R.id.tv_cafelocation:

                        Intent intent1 = new Intent(context, googlemap2.class);
                        intent1.putExtra("cafelocation",arrayList.get(position).getCafelocation());
                        intent1.putExtra("cafename",arrayList.get(position).getCafename());
                        context.startActivity(intent1);


                    break;

                case R.id.cb_cafe:
                    final CafeItem item = arrayList.get(position);


                    final FirebaseFirestore mStore = FirebaseFirestore.getInstance();

                    DocumentReference postRef = mStore.document("cafe/"+item.getCafeId());
                    final CollectionReference likesRef = postRef.collection("find");
                    Log.e("firestore", String.valueOf(item.isIscafelike()));

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

                                              //  Toast.makeText(context, "즐겨찾기 취소", Toast.LENGTH_SHORT).show();


                                                mStore.collection("cafe").document(item.getCafeId())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.getResult() != null){
                                                                    String cafename=(String)task.getResult().getData().get("cafename");
                                                                    String cafelocation=(String)task.getResult().getData().get("cafelocation");
                                                                    String cafeimage=(String)task.getResult().getData().get("cafeimage");
                                                                    final boolean[] cafecheck = new boolean[1];
                                                                    cafecheck[0] = false;

                                                                    contact_Cafe = "";

                                                                    CafeItem cafeItem = new CafeItem(mAuth.getUid(),cafeimage,cafename,cafelocation, item.getCafeId(), cafecheck[0]);
                                                                    contact_Cafe = gson.toJson(cafeItem,CafeItem.class);
                                                                    SharedPreferences.Editor editor = cafedata.edit();
                                                                    editor.putString(item.getCafeId(),contact_Cafe);
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
                                        likeMap.put("cafeId",item.getCafeId());
                                        likesRef.document(mAuth.getUid()).set(likeMap, SetOptions.merge());
                              //          Toast.makeText(context, "즐겨찾기 등록", Toast.LENGTH_SHORT).show();

                                        mStore.collection("cafe").document(item.getCafeId())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.getResult() != null){
                                                            String cafename=(String)task.getResult().getData().get("cafename");
                                                            String cafelocation=(String)task.getResult().getData().get("cafelocation");
                                                            String cafeimage=(String)task.getResult().getData().get("cafeimage");
                                                            final boolean[] cafecheck = new boolean[1];
                                                            cafecheck[0] = true;

                                                            contact_Cafe = "";

                                                            CafeItem cafeItem = new CafeItem(mAuth.getUid(),cafeimage,cafename,cafelocation, item.getCafeId(), cafecheck[0]);
                                                            contact_Cafe = gson.toJson(cafeItem,CafeItem.class);
                                                            SharedPreferences.Editor editor = cafedata.edit();
                                                            editor.putString(item.getCafeId(),contact_Cafe);
                                                            editor.commit();


                                                        }
                                                    }
                                                });
                                    }

                                }
                            });



                    break;
                default:
            }
        }
    }
}
