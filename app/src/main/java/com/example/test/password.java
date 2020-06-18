package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class password extends BasicActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText mEmail, mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        mEmail = findViewById(R.id.et_ps_email);
        Button button = findViewById(R.id.btn_send );
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {

                        if(mEmail.length()>0){
                            final RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
                            loaderlayout.setVisibility(View.VISIBLE);
                            mAuth.sendPasswordResetEmail(mEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                loaderlayout.setVisibility(View.GONE);
                                                Toast.makeText(password.this,"이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(password.this, login.class);
//
                        //                        액티비티 시작!
                                                             startActivity(intent);
                                            }

                                        }
                                    });


                        }else{
                            Toast.makeText(password.this,"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }

//                        //SubActivity로 가는 인텐트를 생성
//                        Toast.makeText(password.this,"확인 되었습니다!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(password.this, repassword.class);
//
//                        //액티비티 시작!
//                        startActivity(intent);
                    }
                }
        );
    }
}
