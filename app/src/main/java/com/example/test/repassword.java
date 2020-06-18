package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class repassword extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repassword);

        Button button = findViewById(R.id.psfsButton);
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성
                        Toast.makeText(repassword.this,"비밀번호가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(repassword.this, login.class);

                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );
    }
}
