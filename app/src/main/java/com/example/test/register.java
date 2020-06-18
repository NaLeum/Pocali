package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register extends BasicActivity implements View.OnClickListener {

    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
//private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText  mEmail, mPassword, mPasswordCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        mEmail = findViewById(R.id.sign_email);
        mPassword = findViewById(R.id.sign_password);
        mPasswordCheck = findViewById(R.id.sign_passwordcheck);

        findViewById(R.id.btn_register).setOnClickListener(this);

//        Button button = findViewById(R.id.registerButton);
//        button.setOnClickListener(
//                new Button.OnClickListener(){
//                    public void onClick(View v) {
//                        //SubActivity로 가는 인텐트를 생성
//                        Toast.makeText(register.this,"회원가입이 되었습니다!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(register.this, login.class);
//
//                        //액티비티 시작!
//                        startActivity(intent);
//                    }
//                }
//        );

    }

    @Override
    public void onClick(View v) {
        if (mEmail.length() > 0 && mPassword.length() > 0 && mPasswordCheck.length() > 0) {

             String CheckPassword = mPassword.getText().toString();
             String CheckPasswordCheck = mPasswordCheck.getText().toString();
            if (CheckPassword.equals(CheckPasswordCheck)) {
                mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mAuth.getCurrentUser();
                                   /* if (user != null) {
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put(FirebaseID.documentId, user.getUid());

                                        userMap.put(FirebaseID.email, mEmail.getText().toString());
                                        userMap.put(FirebaseID.passward, mPassword.getText().toString());
                                        userMap.put(FirebaseID.passwardcheck, mPasswordCheck.getText().toString());
                                        mStore.collection(FirebaseID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                                        finish();
                                    }*/
                                    Toast.makeText(register.this, "회원가입에 성공하였습니다.",
                                            Toast.LENGTH_SHORT).show();
                                   finish();
                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(register.this, "Sgin Up Error",
                                            Toast.LENGTH_SHORT).show();

                                }

                                /* ... */
                            }
                        });
            } else {
                Toast.makeText(register.this, "비밀번호가 일치하지 않습니다.",
                        Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(register.this, "이메일 또는 비밀번호를 입력해주세요.",
                    Toast.LENGTH_SHORT).show();

        }
    }
}
