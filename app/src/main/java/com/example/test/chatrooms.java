package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.test.data.model.Chatmodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class chatrooms extends BasicActivity {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatrooms);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = findViewById(R.id.rv_chatrooms);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(chatrooms.this));
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        private List<Chatmodel> chatmodel = new ArrayList<>();
        private String uid;
        private ArrayList<String> documentUsers = new ArrayList<>();

        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatmodel.clear();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        chatmodel.add(item.getValue(Chatmodel.class));

                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatrooms,parent,false);


            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;

            String documentuid=null;
            for(String user: chatmodel.get(position).users.keySet()){
                if(!user.equals(uid)){
                    documentuid = user;
                    documentUsers.add(documentuid);
                }
            }
             FirebaseFirestore mStore = FirebaseFirestore.getInstance();
            final String Documentuid = documentuid;
            mStore.collection(FirebaseID.user).document(documentuid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){

                                String userName=(String)task.getResult().getData().get(FirebaseID.neckname);
                                String profileImageUrl=(String)task.getResult().getData().get(FirebaseID.photoUrl);
                                String documentuid= (String)task.getResult().getData().get(FirebaseID.documentId);


                                Glide.with(customViewHolder.imageView.getContext())
                                        .load(profileImageUrl)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(customViewHolder.imageView);
                                customViewHolder.textView_title.setText(userName);

                            }
                        }
                    });

            Map<String,Chatmodel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatmodel.get(position).comments);
            String lastmessagekey = (String) commentMap.keySet().toArray()[0];


            if(chatmodel.get(position).comments.get(lastmessagekey).message.contains("(kakao)")) {
                String s2 = chatmodel.get(position).comments.get(lastmessagekey).message.replace("(kakao)", "");

                if (s2.equals("")) {

                    customViewHolder.textView_lastmessage.setText("이모티콘을 보냈습니다.");

                } else {
                    customViewHolder.textView_lastmessage.setText(s2);

                }

            }else if(chatmodel.get(position).comments.get(lastmessagekey).message.contains("(kakaa)")){

                String s2 = chatmodel.get(position).comments.get(lastmessagekey).message.replace("(kakaa)", "");

                if (s2.equals("")) {

                    customViewHolder.textView_lastmessage.setText("이모티콘을 보냈습니다.");

                } else {
                    customViewHolder.textView_lastmessage.setText(s2);

                }


            }else{
                customViewHolder.textView_lastmessage.setText(chatmodel.get(position).comments.get(lastmessagekey).message);
            }






            customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),MessageActivity.class);
                    intent.putExtra("documentid",documentUsers.get(position));
                    startActivity(intent);
                }
            });
simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
long unixTime = (long) chatmodel.get(position).comments.get(lastmessagekey).timestamp;
Date date = new Date(unixTime);
customViewHolder.textView_time.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return chatmodel.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView_title, textView_lastmessage,textView_time;


            public CustomViewHolder(View view) {
                super(view);

                imageView = view.findViewById(R.id.chatitems_imageview);
                textView_title=view.findViewById(R.id.chatitems_textview_title);
                textView_lastmessage=view.findViewById(R.id.chatitems_textview_lastMessage);
                textView_time = view.findViewById(R.id.chatitems_textview_time);


            }
        }
    }
}
