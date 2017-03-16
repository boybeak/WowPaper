package com.nulldreams.wowpaper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nulldreams.wowpaper.manager.ApiManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiManager.getInstance(this);
    }
}
