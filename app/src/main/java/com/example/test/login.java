package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class login extends BasicActivity  {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText mEmail, mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Log.e("재훈","onCreate");

        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);



//        findViewById(R.id.registerButton).setOnClickListener(this);
//        findViewById(R.id.LoginButton).setOnClickListener(this);
        Button button = findViewById(R.id.LoginButton);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (mEmail.length() > 0 && mPassword.length() > 0) {
                          final   RelativeLayout loaderlayout = findViewById(R.id.loaderlayout);
                            loaderlayout.setVisibility(View.VISIBLE);
                            mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                                    .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            loaderlayout.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    Toast.makeText(login.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(login.this, first.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        //액티비티 시작!
                                                    startActivity(intent);
                                                }
                                            } else {


                                                Toast.makeText(login.this, "Login Error",
                                                        Toast.LENGTH_SHORT).show();

                                            }

                                            // ...
                                        }
                                    });
                            //SubActivity로 가는 인텐트를 생성
//                        Toast.makeText(login.this,"로그인 되었습니다!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(login.this, first.class);
//
//                        //액티비티 시작!
//                        startActivity(intent);

                        }else{

                            Toast.makeText(login.this, "이메일과 비밀번호를 입력해주세요.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );


        Button button1 = findViewById(R.id.PasswardFindButton);
        button1.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent = new Intent(login.this, password.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );

        Button button2 = findViewById(R.id.SigninButton);
        button2.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성

                        Intent intent = new Intent(login.this, register.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );

    }
    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user != null){
//            Toast.makeText(this,"자동 로그인"+user.getUid(), Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(login.this, first.class);
////
////                        //액티비티 시작!
//                     startActivity(intent);
//
//        }

        Log.e("재훈","onStart");
    }
   @Override
    protected void onResume() {
        super.onResume();

        Log.e("재훈","onResume");
    }
    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e("재훈","onRestart");
    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.e("재훈","onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();

        Log.e("재훈","onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("재훈","onDestroy");
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }



  /*  @Override
    public void onClick(View v) {


        Intent intent = new Intent(login.this, first.class);
        startActivity(intent);

       /* Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        startActivity(intent);
    }*/
}
