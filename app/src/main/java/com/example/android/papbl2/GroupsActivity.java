package com.example.android.papbl2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GroupsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new GroupsFragment())
                .commit();
    }
}
