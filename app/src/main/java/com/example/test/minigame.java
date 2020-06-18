package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class minigame extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minigame);

        TextView t_start = findViewById(R.id.tv_start);
        TextView t_end = findViewById(R.id.tv_end);
    }

    public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_start:{

                    Intent intent = new Intent(this,GameActivity.class);
                    startActivity(intent);

                }
                break;
                case R.id.tv_end:{
                finish();
                }
                break;





            }



    }
}
