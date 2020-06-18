package com.example.test.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.model.ComentItem;
import com.example.test.personprofile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ComentAdapter extends RecyclerView.Adapter<ComentAdapter.ComentHolder> {

    private ArrayList<ComentItem> arrayList;
    private Context context;
private FirebaseFirestore mStore =FirebaseFirestore.getInstance();
private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public ComentAdapter(ArrayList<ComentItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ComentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coment_item,parent,false);

        ComentAdapter.ComentHolder comentHolder = new ComentAdapter.ComentHolder(view);

        return comentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComentHolder holder, int position) {

        final ComentItem item = arrayList.get(position);

        holder.usernames.setText(item.getUsername());
        holder.coments.setText(item.getComent());
        if(mAuth.getUid().equals(item.getUserid())){
            holder.comentdelete.setVisibility(View.VISIBLE);
        }else {
            holder.comentdelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
         return (null != arrayList ? arrayList.size():0);
    }

    public class ComentHolder extends RecyclerView.ViewHolder {
        protected TextView usernames, coments;
        protected ImageView comentdelete;
        public ComentHolder(@NonNull View itemView) {
            super(itemView);
            this.comentdelete = itemView.findViewById(R.id.iv_delete_coment);
            this.usernames = itemView.findViewById(R.id.tv_username);
            this.coments = itemView.findViewById(R.id.tv_coment);
            usernames.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, personprofile.class);
                    intent.putExtra("documentid",arrayList.get(position).getUserid());
                    context.startActivity(intent);
                }
            });
            comentdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  final   int position = getAdapterPosition();

                    mStore.collection("coment").document(arrayList.get(position).getComentid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    arrayList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, arrayList.size());

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            });

        }
    }
}
