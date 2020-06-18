package com.example.test.recyclerview;


import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.model.PostItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;

public class PostviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public CheckBox cbLike, cbFind;
    public ImageView ivImg, ivComent, ivUploader, btnsetting;
    public TextView tvLikeCount, tvUserName, tvPostText,tvCameraModel,tvCameraFilter;
    private PostAdapter mAdapter;
    private FirebaseAuth mAuth;
    SharedPreferences postdata;
    Gson gson;
    String contact_Post;

    private ArrayList<PostItem> postItems;
    public PostviewHolder(@NonNull View itemView, PostAdapter postAdapter) {
        super(itemView);
        mAdapter=postAdapter;

        ivImg=itemView.findViewById(R.id.iv_img);
        cbLike=itemView.findViewById(R.id.cb_like);
        ivComent=itemView.findViewById(R.id.iv_coment);
        ivUploader=itemView.findViewById(R.id.Iv_uploader);
        cbFind=itemView.findViewById(R.id.cb_find);
        btnsetting=itemView.findViewById(R.id.btn_setting);

        tvLikeCount=itemView.findViewById(R.id.tv_like_Count);
        tvUserName=itemView.findViewById(R.id.tv_username);
        tvPostText=itemView.findViewById(R.id.tv_PostText);
        tvCameraModel=itemView.findViewById(R.id.tv_camera_model);
        tvCameraFilter =itemView.findViewById(R.id.tv_camera_filter);

        cbLike.setOnClickListener(this);
        ivComent.setOnClickListener(this);
        cbFind.setOnClickListener(this);
        btnsetting.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        tvCameraModel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int position = getAdapterPosition();


        switch (v.getId()){
            case  R.id.cb_like:
                    if(cbLike.isChecked()){
                        String i  =tvLikeCount.getText().toString();
                        int i2 = Integer.parseInt(i);
                        i2+=1;
                        String to = Integer.toString(i2);
                        tvLikeCount.setText(to);

                    }else{
                        String i  =tvLikeCount.getText().toString();
                        int i2 = Integer.parseInt(i);
                        i2-=1;
                        String to = Integer.toString(i2);
                        tvLikeCount.setText(to);
                    }
                mAdapter.onlikClicked(position);
               // mAdapter.notifyItemChanged(position,position);
                break;
            case R.id.cb_find:

                mAdapter.onmemoCliked(position);
              //  mAdapter.onlikClicked(position);
                 break;

            case R.id.iv_coment:
                mAdapter.gotocoment(position);
                break;
            case R.id.btn_setting:

                mAdapter.showPopup(v,position);

                break;
            case R.id.tv_username:
                mAdapter.gotoprofile(position);
                break;

            case R.id.tv_camera_model:
                String camo=tvCameraModel.getText().toString();
                if(camo.equals("cymera")||camo.equals("Cymera")||camo.equals("싸이메라")){
                    mAdapter.gotoapp("com.cyworld.camera");
                }else if(camo.equals("foodie")||camo.equals("푸디")||camo.equals("Foodie")){
                    mAdapter.gotoapp("com.linecorp.foodcam.android");
                 }
                break;

            default:
        }

        }





    }

