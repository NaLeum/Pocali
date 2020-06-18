package com.example.test;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.test.data.model.Chatmodel;
import com.example.test.data.model.NotificationModel;
import com.example.test.data.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends BasicActivity{
    private ImageView kakao,kakaa;
    private String uid;
    private String chatroomuid,pushTokens;
    private String documentid;
    private Button button,emobutton;
    private EditText editText;
    private RecyclerView recyclerView;
   private String userprofileImageUrl,useruid, useruserName;
  private UserModel userModel;
  private LinearLayout list_emoticon;
private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
private DatabaseReference databaseReference;
private ValueEventListener valueEventListener;
int peoplecount = 0 ;
int buttoncount =0;
    int buttoncount2 =0;
    int comentsize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        uid = FirebaseAuth.getInstance().getUid();
        documentid = getIntent().getStringExtra("documentid");
        button = findViewById(R.id.btn_messagesend);
        list_emoticon = findViewById(R.id.list_emoticon);

        kakao = findViewById(R.id.iv_kakao);
        Glide.with(MessageActivity.this)
                .load(R.drawable.kakao).override(500)
                .into(kakao);
        kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttoncount++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(buttoncount==1){

                        }else if (buttoncount==2){
                            Chatmodel chatmodel = new Chatmodel();
                            chatmodel.users.put(uid,true);
                            chatmodel.users.put(documentid,true);


                            if(chatroomuid == null) {
                                button.setEnabled(false);
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        checkChatrooms();
                                    }
                                });
                            }else{
                                Chatmodel.Comment comment = new Chatmodel.Comment();
                                comment.uid = uid;
                                comment.message = "(kakao)";
                                comment.timestamp = ServerValue.TIMESTAMP;
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendGcm();
                                        editText.setText("");
                                        list_emoticon.setVisibility(View.GONE);
                                    }
                                });
                            }


                        }
                        buttoncount=0;
                    }
                },500);

            }
        });

        kakaa = findViewById(R.id.iv_kakaa);
        Glide.with(MessageActivity.this)
                .load(R.drawable.kakao2).override(500)
                .into(kakaa);
        kakaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttoncount2++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(buttoncount2==1){

                        }else if (buttoncount2==2){
                            Chatmodel chatmodel = new Chatmodel();
                            chatmodel.users.put(uid,true);
                            chatmodel.users.put(documentid,true);


                            if(chatroomuid == null) {
                                button.setEnabled(false);
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        checkChatrooms();
                                    }
                                });
                            }else{
                                Chatmodel.Comment comment = new Chatmodel.Comment();
                                comment.uid = uid;
                                comment.message = "(kakaa)";
                                comment.timestamp = ServerValue.TIMESTAMP;
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendGcm();
                                        editText.setText("");
                                        list_emoticon.setVisibility(View.GONE);
                                    }
                                });
                            }


                        }
                        buttoncount2=0;
                    }
                },500);

            }
        });


        emobutton = findViewById(R.id.btn_emoticon);
        emobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list_emoticon.getVisibility()==View.GONE){
                    list_emoticon.setVisibility(View.VISIBLE);
                    recyclerView.scrollToPosition(comentsize);

                }else{
                    list_emoticon.setVisibility(View.GONE);
                }
            }
        });
        editText = findViewById(R.id.et_message);
        recyclerView = findViewById(R.id.rv_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatmodel chatmodel = new Chatmodel();
                chatmodel.users.put(uid,true);
                chatmodel.users.put(documentid,true);


                if(chatroomuid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatrooms();
                        }
                    });
                }else{
                    Chatmodel.Comment comment = new Chatmodel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendGcm();
                            editText.setText("");
                        }
                    });
                }
            }
        });
        checkChatrooms();
    }

    void sendGcm(){
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to=pushTokens;
        notificationModel.notification.title= "27일의 테스트";
        if(editText.getText().toString().equals("")){
            notificationModel.notification.text = "이모티콘을 보냈습니다.";
        }else{
            if(editText.getText().toString().contains("(kakao)")){

                String s2 = editText.getText().toString().replace("(kakao)", "");

                if (s2.equals("")) {

                    notificationModel.notification.text = "이모티콘을 보냈습니다.";

                } else {
                    notificationModel.notification.text =s2;

                }

            }else if(editText.getText().toString().contains("(kakaa)")){

                String s2 = editText.getText().toString().replace("(kakaa)", "");

                if (s2.equals("")) {

                    notificationModel.notification.text = "이모티콘을 보냈습니다.";

                } else {
                    notificationModel.notification.text =s2;

                }

            }else{
                notificationModel.notification.text = editText.getText().toString();
            }

        }

        notificationModel.data.title= useruserName;
        notificationModel.data.text = editText.getText().toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));
        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AAAAf-DOtcc:APA91bHGpVU4F2AIep25CHGMM2a68vghpQCt7_rOK6KRLYDbqb0M66pG3agasQqErplBhtKPPuAOkEL-yS8TI2TnA8dyGnC0umxgLuC5JYGMhXVW6F05EgVhtftUS8ejmCZ8lZHbEFui")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });


    }

    void checkChatrooms(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Chatmodel chatmodel = item.getValue(Chatmodel.class);
                    if(chatmodel.users.containsKey(documentid)){
                        chatroomuid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


       List<Chatmodel.Comment> comments;

        public RecyclerViewAdapter() {
            documentid = getIntent().getStringExtra("documentid");
            comments = new ArrayList<>();
            FirebaseFirestore mStore = FirebaseFirestore.getInstance();
            mStore.collection(FirebaseID.user).document(documentid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                               userprofileImageUrl = (String) task.getResult().getData().get(FirebaseID.photoUrl);
                               useruid = (String)task.getResult().getData().get(FirebaseID.documentId);
                                useruserName = (String)task.getResult().getData().get(FirebaseID.neckname);
                                pushTokens=(String)task.getResult().getData().get("pushToken");
                                getMessageList();
                            }
                        }
                    });




        }
        void getMessageList(){
            databaseReference =  FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments");
          valueEventListener =  databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        Chatmodel.Comment comment_orgin = item.getValue(Chatmodel.Comment.class);
                        Chatmodel.Comment comment_modify = item.getValue(Chatmodel.Comment.class);
                        comment_modify.readUsers.put(uid , true);
                        readUsersMap.put(key,comment_modify);
                        comments.add(comment_orgin);
                    }
                    if(comments.size() == 0){return;}
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)){
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size()-1);
                                comentsize=comments.size()-1;
                            }
                        });

                    }else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size()-1);
                        comentsize=comments.size()-1;

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);




            return new MessageViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = (MessageViewHolder)holder;
           Log.e("",""+comments.get(position).message) ;

            //내가보낸메세지
            if(comments.get(position).uid.equals(uid)){

                if(comments.get(position).message.contains("(kakao)")){
                messageViewHolder.imageView_emoticon.setVisibility(View.VISIBLE);
                    String s2 = comments.get(position).message.replace("(kakao)", "");

                    if(s2.equals("")){
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.rightchatbubble);
                        messageViewHolder.textView_message.setVisibility(View.GONE);
                    }else{
                        messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.rightchatbubble);
                        messageViewHolder.textView_message.setText(s2);

                    }

                }else if(comments.get(position).message.contains("(kakaa)")){
                    messageViewHolder.imageView_emoticon.setVisibility(View.VISIBLE);
                    String s2 = comments.get(position).message.replace("(kakaa)", "");

                    if(s2.equals("")){
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao2).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.rightchatbubble);
                        messageViewHolder.textView_message.setVisibility(View.GONE);
                    }else{
                        messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao2).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.rightchatbubble);
                        messageViewHolder.textView_message.setText(s2);

                    }


                }else {
                    messageViewHolder.imageView_emoticon.setVisibility(View.GONE);
                    messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                }

                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightchatbubble);
                messageViewHolder.linearLayout_vs.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textView_message.setTextSize(15);
                setreadcounter(position,messageViewHolder.textView_readcounter_left);
            }else{
            //상대방이보낸메세지

                if(comments.get(position).message.contains("(kakao)")){
                    messageViewHolder.imageView_emoticon.setVisibility(View.VISIBLE);
                    String s2 = comments.get(position).message.replace("(kakao)", "");

                    if(s2.equals("")){
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.leftchatbubble);
                        messageViewHolder.textView_message.setVisibility(View.GONE);
                    }else{
                        messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.leftchatbubble);
                        messageViewHolder.textView_message.setText(s2);

                    }

                }else if(comments.get(position).message.contains("(kakaa)")){
                    messageViewHolder.imageView_emoticon.setVisibility(View.VISIBLE);
                    String s2 = comments.get(position).message.replace("(kakaa)", "");

                    if(s2.equals("")){
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao2).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.leftchatbubble);
                        messageViewHolder.textView_message.setVisibility(View.GONE);
                    }else{
                        messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                        Glide.with(MessageActivity.this)
                                .load(R.drawable.kakao2).override(500)
                                .into(messageViewHolder.imageView_emoticon);
                        messageViewHolder.imageView_emoticon.setBackgroundResource(R.drawable.leftchatbubble);
                        messageViewHolder.textView_message.setText(s2);

                    }


                }else {
                    messageViewHolder.imageView_emoticon.setVisibility(View.GONE);
                    messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                }
                Glide.with(holder.itemView.getContext())
                        .load(userprofileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(useruserName);
                messageViewHolder.linearLayout_vs.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftchatbubble);

                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setreadcounter(position,messageViewHolder.textView_readcounter_right);
            }
            long unixtime = (long) comments.get(position).timestamp;
            Date date =new Date(unixtime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String times = simpleDateFormat.format(date);
            messageViewHolder.time.setText(times);

        }

        void setreadcounter(final int position, final TextView textView){
            if(peoplecount==0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatroomuid).child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peoplecount =users.size();
                        int count = peoplecount - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                int count = peoplecount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }

            }
        }


        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name, time;
            public ImageView imageView_profile,imageView_emoticon;
            public LinearLayout linearLayout_vs;
            public LinearLayout linearLayout_main;
            public TextView textView_readcounter_left,textView_readcounter_right;


            public MessageViewHolder(View view) {
                super(view);
                imageView_emoticon=view.findViewById(R.id.iv_emoticon);
                textView_message =view.findViewById(R.id.tv_messageitem);
                textView_name=view.findViewById(R.id.tv_messageitem_tv_name);
                imageView_profile = view.findViewById(R.id.tv_messageitem_image_profile);
                linearLayout_vs=view.findViewById(R.id.tv_messageitem_liner_vs);
                linearLayout_main = view.findViewById(R.id.messageitem_liner_main);
                time = view.findViewById(R.id.tv_messageitem_time);
                textView_readcounter_left=view.findViewById(R.id.tv_messageitem_readCounter_left);
                textView_readcounter_right=view.findViewById(R.id.tv_messageitem_readCounter_right);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseReference.removeEventListener(valueEventListener);
    }
}
