package com.example.test.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.cafepage;
import com.example.test.data.model.PostItem;

import java.util.ArrayList;

public class MypageAdapter extends RecyclerView.Adapter<MypageAdapter.MypageHolder> {



    private ArrayList<PostItem> arrayList;
    private Context context;

    public MypageAdapter(ArrayList<PostItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public MypageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery,parent,false);
        MypageHolder mypageHolder = new MypageHolder(view);

        return mypageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MypageHolder holder, int position) {

        Glide.with(holder.iv_postiamge.getContext())
                .load(arrayList.get(position).getpostImgUrl()).centerCrop().override(500)
                .into(holder.iv_postiamge);
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size():0);
    }

    public class MypageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView iv_postiamge;

        public MypageHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_postiamge = itemView.findViewById(R.id.Iv_gallery);
            iv_postiamge.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context, cafepage.class);
            intent.putExtra("cafeId",arrayList.get(position).getCafeId());
            intent.putExtra("movepostid",arrayList.get(position).getPostid());
            Log.e("",""+arrayList.get(position).getCafeId());
            context.startActivity(intent);
        }
    }
}
