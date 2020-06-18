package com.example.test.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.coment;
import com.example.test.data.model.PostItem;
import com.example.test.editgaesi;
import com.example.test.personprofile;
import com.example.test.profilegrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.test.FirebaseID.documentId;

public class PostAdapter extends RecyclerView.Adapter<PostviewHolder> {

    private   FirebaseFirestore mStore ;
    private  FirebaseAuth mAuth;
    private FirebaseStorage mStorge;
    private Context mContext;
    private String mdocumentId;
    private ArrayList<PostItem> postItems;
    SharedPreferences postdata;
    Gson gson;
    String contact_Post;

    public  PostAdapter(Context context, ArrayList<PostItem> listItem){
        mContext = context;
        postItems=listItem;
        mStore = FirebaseFirestore.getInstance();
        mStorge=FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mdocumentId = documentId;
    }


    @NonNull
    @Override
    public PostviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View baseView = LayoutInflater.from(parent.getContext()).inflate( R.layout.gaesi,parent,false);

        PostviewHolder postviewHolder = new PostviewHolder(baseView, this);




        return postviewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull final PostviewHolder holder, int position) {
        final PostItem item = postItems.get(position);
        postdata = mContext.getSharedPreferences("shared",MODE_PRIVATE);



        gson = new GsonBuilder().create();
        contact_Post = postdata.getString(item.getPostid(),"");


        if(!contact_Post.equals("")) {
            PostItem ppostItem = gson.fromJson(contact_Post, PostItem.class);
            if(ppostItem.getDocumentId().equals(mAuth.getUid())){
                holder.cbLike.setChecked(ppostItem.isUserLike());
                holder.cbFind.setChecked(ppostItem.isUserFind());


            }else{
                DocumentReference postRef = mStore.document("post/"+item.getPostid());
                final   CollectionReference likesRef = postRef.collection("likes");
                Log.e("firestore", String.valueOf(item.isUserLike()));

                DocumentReference like = likesRef.document(mAuth.getUid());
                likesRef.whereEqualTo("username", mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if(task2.getResult().size()>0){

                                    holder.cbLike.setChecked(true);
                                }else{
//좋아요 안눌린 상태
                                    holder.cbLike.setChecked(false);
                                }

                            }
                        });
                final   CollectionReference memosRef = postRef.collection("memos");
                Log.e("firestore", String.valueOf(item.isUserLike()));
                memosRef.whereEqualTo("username", mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if(task2.getResult().size()>0){

                                    holder.cbFind.setChecked(true);
                                }else{
//좋아요 안눌린 상태
                                    holder.cbFind.setChecked(false);
                                }

                            }
                        });


            }

        }
        else{
            DocumentReference postRef = mStore.document("post/"+item.getPostid());
            final   CollectionReference likesRef = postRef.collection("likes");
            Log.e("firestore", String.valueOf(item.isUserLike()));

            DocumentReference like = likesRef.document(mAuth.getUid());
            likesRef.whereEqualTo("username", mAuth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            if(task2.getResult().size()>0){

                                holder.cbLike.setChecked(true);
                            }else{
//좋아요 안눌린 상태
                                holder.cbLike.setChecked(false);
                            }

                        }
                    });
            final   CollectionReference memosRef = postRef.collection("memos");
            Log.e("firestore", String.valueOf(item.isUserLike()));
            memosRef.whereEqualTo("username", mAuth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            if(task2.getResult().size()>0){

                                holder.cbFind.setChecked(true);
                            }else{
//좋아요 안눌린 상태
                                holder.cbFind.setChecked(false);
                            }

                        }
                    });


        }





        mStore.collection("post").document(item.getPostid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult() != null){
                            if (mAuth.getUid().equals(item.getDocumentId())){
                                holder.btnsetting.setVisibility(View.VISIBLE);
                            }else{
                                holder.btnsetting.setVisibility(View.GONE);
                            }

                        }
                    }
                });


        Glide.with(holder.itemView.getContext())
                .load(item.getUserImgUrl()).centerCrop()
                .into(holder.ivUploader);
        Glide.with(holder.itemView.getContext())
                .load(item.getpostImgUrl())
                .into(holder.ivImg);

        holder.tvUserName.setText(item.getUsername());
        holder.tvPostText.setText(item.getPostText());
        holder.tvLikeCount.setText(item.getLikecount());

//        DocumentReference postRef = mStore.document("post/"+item.getPostid());
//        final   CollectionReference likesRef = postRef.collection("likes");
//
//        likesRef.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        QuerySnapshot likesResult = task.getResult();
//
//                    final int likecount =likesResult.size();
//                        String to = Integer.toString(likecount);
//
//
//
//                 Log.e("사이즈",""+likesResult.size());
//                        holder.tvLikeCount.setText(to);
//
//                    }
//                });





        holder.tvCameraFilter.setText((item.getCamerafilter()));
        holder.tvCameraModel.setText((item.getCameraModel()));


    }

    @Override
    public int getItemCount() {
        return (null != postItems ? postItems.size():0);
    }
    public void onmemoCliked(final int position){
        final PostItem item = postItems.get(position);
        final  boolean[] findcheck = new boolean[1];
        final  boolean[] likecheck = new boolean[1];
        final FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        DocumentReference postRef = mStore.document("post/"+item.getPostid());
        final   CollectionReference likesRef = postRef.collection("memos");
        Log.e("firestore", String.valueOf(item.isUserLike()));

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

                             //       Toast.makeText(mContext, "저장 취소", Toast.LENGTH_SHORT).show();

                                    findcheck[0] = false;
                                    DocumentReference postsRef = mStore.document("post/"+item.getPostid());
                                    final   CollectionReference likessRef = postsRef.collection("likes");
                                    Log.e("firestore", String.valueOf(item.isUserLike()));

                                    DocumentReference like = likesRef.document(mAuth.getUid());
                                    likessRef.whereEqualTo("username", mAuth.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                    if(task2.getResult().size()>0){
                                                        likecheck[0] = true;
                                                        contact_Post = "";

                                                        PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                        contact_Post = gson.toJson(postItem, PostItem.class);
                                                        SharedPreferences.Editor editor = postdata.edit();
                                                        editor.putString(item.getPostid(),contact_Post);
                                                        editor.commit();

                                                    }else{
                                                        likecheck[0] = false;
                                                        contact_Post = "";

                                                        PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                        contact_Post = gson.toJson(postItem, PostItem.class);
                                                        SharedPreferences.Editor editor = postdata.edit();
                                                        editor.putString(item.getPostid(),contact_Post);
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
                            likeMap.put("postId", item.getPostid());
                            likesRef.document(mAuth.getUid()).set(likeMap, SetOptions.merge());
                        //    Toast.makeText(mContext, "저장됨", Toast.LENGTH_SHORT).show();

                            findcheck[0] = true;
                            DocumentReference postsRef = mStore.document("post/"+item.getPostid());
                            final   CollectionReference likessRef = postsRef.collection("likes");
                            Log.e("firestore", String.valueOf(item.isUserLike()));

                            DocumentReference like = likesRef.document(mAuth.getUid());
                            likessRef.whereEqualTo("username", mAuth.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                            if(task2.getResult().size()>0){
                                                likecheck[0] = true;
                                                contact_Post = "";

                                                PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                contact_Post = gson.toJson(postItem, PostItem.class);
                                                SharedPreferences.Editor editor = postdata.edit();
                                                editor.putString(item.getPostid(),contact_Post);
                                                editor.commit();

                                            }else{
                                                likecheck[0] = false;
                                                contact_Post = "";

                                                PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                contact_Post = gson.toJson(postItem, PostItem.class);
                                                SharedPreferences.Editor editor = postdata.edit();
                                                editor.putString(item.getPostid(),contact_Post);
                                                editor.commit();
                                            }

                                        }
                                    });



                        }

                    }
                });

    }

    public void onlikClicked(int position) {
       final PostItem item = postItems.get(position);
        final  boolean[] findcheck = new boolean[1];
        final  boolean[] likecheck = new boolean[1];
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();

        final DocumentReference postRef = mStore.document("post/"+item.getPostid());
      final   CollectionReference likesRef = postRef.collection("likes");
      final   CollectionReference likessRef = postRef.collection("memos");
        Log.e("firestore", String.valueOf(item.isUserLike()));

       DocumentReference like = likesRef.document(mAuth.getUid());
        likesRef.whereEqualTo("username", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                        if(task2.getResult().size()>0){
                            //좋아요 취소가 여기서 이미 이루어진다.

                            DocumentReference userlikeRef = likesRef.document(mAuth.getUid());
                            userlikeRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("firestore", " removed");
                                 //   Toast.makeText(mContext, "좋아요 취소", Toast.LENGTH_SHORT).show();
                                    likecheck[0]=false;
                                    likessRef.whereEqualTo("username", mAuth.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                    if(task2.getResult().size()>0){
                                                        findcheck[0] = true;
                                                        contact_Post = "";

                                                        PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                        contact_Post = gson.toJson(postItem, PostItem.class);
                                                        SharedPreferences.Editor editor = postdata.edit();
                                                        editor.putString(item.getPostid(),contact_Post);
                                                        editor.commit();

                                                    }else{
                                                        findcheck[0] = false;
                                                        contact_Post = "";

                                                        PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                        contact_Post = gson.toJson(postItem, PostItem.class);
                                                        SharedPreferences.Editor editor = postdata.edit();
                                                        editor.putString(item.getPostid(),contact_Post);
                                                        editor.commit();
                                                    }

                                                }
                                            });
                                }
                            });

                            postRef.update("userlike", FieldValue.increment(-1));



                        }else{

                            Map<String,Object> likeMap = new HashMap<>();
                            likeMap.put("username" , mAuth.getUid());
                            likeMap.put("created at", new Date());
                            likesRef.document(mAuth.getUid()).set(likeMap, SetOptions.merge());
                     //       Toast.makeText(mContext, "좋아요", Toast.LENGTH_SHORT).show();

                            likecheck[0]=true;
                            likessRef.whereEqualTo("username", mAuth.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                            if(task2.getResult().size()>0){
                                                findcheck[0] = true;
                                                contact_Post = "";

                                                PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                contact_Post = gson.toJson(postItem, PostItem.class);
                                                SharedPreferences.Editor editor = postdata.edit();
                                                editor.putString(item.getPostid(),contact_Post);
                                                editor.commit();

                                            }else{
                                                findcheck[0] = false;
                                                contact_Post = "";

                                                PostItem postItem = new PostItem(likecheck[0],findcheck[0],mAuth.getUid());
                                                contact_Post = gson.toJson(postItem, PostItem.class);
                                                SharedPreferences.Editor editor = postdata.edit();
                                                editor.putString(item.getPostid(),contact_Post);
                                                editor.commit();
                                            }

                                        }
                                    });
                            postRef.update("userlike", FieldValue.increment(1));
                        }

                    }
                });









    }
    @Override
    public void onBindViewHolder(@NonNull final PostviewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        PostItem item = postItems.get(position);



    }
    public   void showPopup(View v , final int position) {
        final PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        Intent intent = new Intent(mContext, editgaesi.class);
                        intent.putExtra("id",postItems.get(position).getPostid());

                        mContext.startActivity(intent);
                        notifyDataSetChanged();

                        return true;
                    case R.id.delete:

                        StorageReference storageRef = mStorge.getReference();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        StorageReference desertRef = storageRef.child("posts/" + user.getUid() + "/"+postItems.get(position).getPostid()+".jpg");

                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("파일삭제", "posts/" + user.getUid() + "/"+postItems.get(position).getPostid()+".jpg");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                        Log.e("로그", "삭제: " + postItems.get(position));
                        Log.e("로그", "삭제: " + postItems.get(position).getPostid());
                       mStore.collection("post").document(postItems.get(position).getPostid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        postItems.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, postItems.size());


                                        Toast.makeText(mContext, "삭제되었습니다.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(mContext, "삭제에 실패하였습니다.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.navi_draw, popup.getMenu());
        popup.show();
    }


    public void gotoprofile(int position) {
        final PostItem item = postItems.get(position);
        if(mAuth.getUid().equals(item.getDocumentId())){
            Intent intent3 = new Intent(mContext, profilegrid.class);

            mContext.startActivity(intent3);

        }else {
            Intent intent3 = new Intent(mContext, personprofile.class);
            intent3.putExtra("documentid", item.getDocumentId());
            mContext.startActivity(intent3);
        }


    }

    public void gotocoment(int position) {
        final PostItem item = postItems.get(position);
        Intent intent3 = new Intent(mContext, coment.class);
        intent3.putExtra("postid", item.getPostid());
        intent3.putExtra("camera",item.getCameraModel());
        intent3.putExtra("camerafilter",item.getCamerafilter());
        intent3.putExtra("context",item.getPostText());
        mContext.startActivity(intent3);

    }
    public void gotoapp(String packagename) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getApplicationInfo(packagename,PackageManager.GET_META_DATA);
            Intent intent = pm.getLaunchIntentForPackage(packagename);
            mContext.startActivity(intent);

        }catch(PackageManager.NameNotFoundException e){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packagename));
            mContext.startActivity(intent);

        }

    }
}
