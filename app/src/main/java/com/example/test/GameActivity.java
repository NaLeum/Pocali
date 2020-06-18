package com.example.test;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Random;

public class GameActivity extends BasicActivity{
    View[] include = new View[2];
    TextView now_number,dc,dc2;
    RelativeLayout[] button = new RelativeLayout[25];
    TextView[] button_text = new TextView[25];
    RelativeLayout ok_button;
    Thread t;
    boolean thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        all_view();
        include[1].setVisibility(View.GONE);

        match_number_int = 1;
        for(int reset_button_number = 0; reset_button_number <= 24; reset_button_number++) {
            button_number_01[reset_button_number] = 0;
            button_number_02[reset_button_number] = 0;
            button_text[reset_button_number].setVisibility(View.VISIBLE);
        }
        game();
        all_button_click();
        Thread t = new Thread(new Runnable() { public void run() {
            //첫 시작한 현재시간
            final long start = System.currentTimeMillis();
            //시간포맷팅을 위한 포맷설정
            final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
            while (thread) {
                runOnUiThread(new Runnable()
            { public void run() {
                //쓰레드가 돌때마다 계속 현재시간 갱신
                long end = System.currentTimeMillis();
                //진행된시간을 계산하여 포맷에 맞게
                //TextView에 뿌리기
                dc.setText(sdf.format(end - start).substring(0, 8)); } });
            //0.01초마다 Thread돌리기
                SystemClock.sleep(10); } } });
        thread = true;
        t.start();


    }
    int number_group1, number_group2;
    private void game() {
        now_number.setText(Integer.toString(match_number_int));
        for(number_group1 = 1; number_group1 <= 2; number_group1++) {
            for(number_group2 = 1; number_group2 <= 25; number_group2++) {
                random();
            }
        }
        for(int print_button_num = 0; print_button_num <= 24; print_button_num++) {
            button_text[print_button_num].setText(Integer.toString(button_number_01[print_button_num]));
        }
    }
    int match_number_int = 1;
    int random_button_number_int;
    int[] button_number_01 = new int[25];
    int[] button_number_02 = new int[25];
    private void random() {
        Random random = new Random();
        random_button_number_int = random.nextInt(25);
        if(number_group1 == 1 && button_number_01[(random_button_number_int)] == 0) {
            button_number_01[(random_button_number_int)] = number_group2;
        }else if(number_group1 == 1 && number_group2 <= 25){
            random();
        }
        if(number_group1 == 2 && button_number_02[(random_button_number_int)] == 0) {
            button_number_02[(random_button_number_int)] = (number_group2+25);
        }else if(number_group1 == 2 && number_group2 <= 25){
            random();
        }
    }
    private void all_view() {
        include[1] = findViewById(R.id.gameresult);
        dc = findViewById(R.id.tv_time);
        dc2 = findViewById(R.id.tv_time2);
        now_number = (TextView)findViewById(R.id.now_number);
        button[0] = (RelativeLayout)findViewById(R.id.button_1);
        button[1] = (RelativeLayout)findViewById(R.id.button_2);
        button[2] = (RelativeLayout)findViewById(R.id.button_3);
        button[3] = (RelativeLayout)findViewById(R.id.button_4);
        button[4] = (RelativeLayout)findViewById(R.id.button_5);
        button[5] = (RelativeLayout)findViewById(R.id.button_6);
        button[6] = (RelativeLayout)findViewById(R.id.button_7);
        button[7] = (RelativeLayout)findViewById(R.id.button_8);
        button[8] = (RelativeLayout)findViewById(R.id.button_9);
        button[9] = (RelativeLayout)findViewById(R.id.button_10);
        button[10] = (RelativeLayout)findViewById(R.id.button_11);
        button[11] = (RelativeLayout)findViewById(R.id.button_12);
        button[12] = (RelativeLayout)findViewById(R.id.button_13);
        button[13] = (RelativeLayout)findViewById(R.id.button_14);
        button[14] = (RelativeLayout)findViewById(R.id.button_15);
        button[15] = (RelativeLayout)findViewById(R.id.button_16);
        button[16] = (RelativeLayout)findViewById(R.id.button_17);
        button[17] = (RelativeLayout)findViewById(R.id.button_18);
        button[18] = (RelativeLayout)findViewById(R.id.button_19);
        button[19] = (RelativeLayout)findViewById(R.id.button_20);
        button[20] = (RelativeLayout)findViewById(R.id.button_21);
        button[21] = (RelativeLayout)findViewById(R.id.button_22);
        button[22] = (RelativeLayout)findViewById(R.id.button_23);
        button[23] = (RelativeLayout)findViewById(R.id.button_24);
        button[24] = (RelativeLayout)findViewById(R.id.button_25);
        button_text[0] = (TextView)findViewById(R.id.button_text_1);
        button_text[1] = (TextView)findViewById(R.id.button_text_2);
        button_text[2] = (TextView)findViewById(R.id.button_text_3);
        button_text[3] = (TextView)findViewById(R.id.button_text_4);
        button_text[4] = (TextView)findViewById(R.id.button_text_5);
        button_text[5] = (TextView)findViewById(R.id.button_text_6);
        button_text[6] = (TextView)findViewById(R.id.button_text_7);
        button_text[7] = (TextView)findViewById(R.id.button_text_8);
        button_text[8] = (TextView)findViewById(R.id.button_text_9);
        button_text[9] = (TextView)findViewById(R.id.button_text_10);
        button_text[10] = (TextView)findViewById(R.id.button_text_11);
        button_text[11] = (TextView)findViewById(R.id.button_text_12);
        button_text[12] = (TextView)findViewById(R.id.button_text_13);
        button_text[13] = (TextView)findViewById(R.id.button_text_14);
        button_text[14] = (TextView)findViewById(R.id.button_text_15);
        button_text[15] = (TextView)findViewById(R.id.button_text_16);
        button_text[16] = (TextView)findViewById(R.id.button_text_17);
        button_text[17] = (TextView)findViewById(R.id.button_text_18);
        button_text[18] = (TextView)findViewById(R.id.button_text_19);
        button_text[19] = (TextView)findViewById(R.id.button_text_20);
        button_text[20] = (TextView)findViewById(R.id.button_text_21);
        button_text[21] = (TextView)findViewById(R.id.button_text_22);
        button_text[22] = (TextView)findViewById(R.id.button_text_23);
        button_text[23] = (TextView)findViewById(R.id.button_text_24);
        button_text[24] = (TextView)findViewById(R.id.button_text_25);

    }
    int button_num;
    private void all_button_click() {
        for (int i = 0; i < 25; i++) {
            final int j = i;
            button[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(match_number_int == 50) {
                        match_number_int=50;
                        thread = false;
                        include[1].setVisibility(View.VISIBLE);
                    CharSequence time = dc.getText();
                    dc2.setText(time);


                    }
                    int number = j;
                    if(match_number_int == button_number_01[number]) {
                        match_number_int += 1;
                        button_text[number].setText(Integer.toString(button_number_02[number]));
                    }
                    if(match_number_int == button_number_02[number]) {
                        match_number_int += 1;
                        button_text[number].setVisibility(View.GONE);
                    }
                    now_number.setText(Integer.toString(match_number_int));
                }
            });
        }

    }
}
