package com.example.usama.serviceslab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView tvPercentage;
    EditText etThreadNum;
    Button btnService, btnStop;
    public int percent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPercentage = findViewById(R.id.tvPercentage);
        etThreadNum = findViewById(R.id.etThreadNum);
        btnService = findViewById(R.id.btnService);
        btnStop = findViewById(R.id.btnStop);

        final Intent intent = new Intent(this, MyService.class);
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent.putExtra("num", etThreadNum.getText().toString());
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyEvent event) {
        Integer i = event.getMessage();
        tvPercentage.setText(i.toString().trim()+"%");
    }
}