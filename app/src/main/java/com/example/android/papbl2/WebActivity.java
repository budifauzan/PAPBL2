package com.example.android.papbl2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WebActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_fragment);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new WebFragment())
                .commit();
    }
}
