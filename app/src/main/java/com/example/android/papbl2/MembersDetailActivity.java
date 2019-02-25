package com.example.android.papbl2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MembersDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_detail);
        TextView namaGroup = findViewById(R.id.members_detail_group);
        TextView namaMember = findViewById(R.id.members_detail_birth_name);
        TextView namaPanggung = findViewById(R.id.members_detail_stage_name);
        TextView tanggalLahir = findViewById(R.id.members_detail_birth_date);

        Intent intent = getIntent();
        String namaGroupS = intent.getStringExtra("namaGroup");
        String namaMemberS = intent.getStringExtra("namaMember");
        String namaPanggungS = intent.getStringExtra("namaPanggung");
        String tanggalLahirS = intent.getStringExtra("tanggalLahir");

        namaGroup.setText(namaGroupS);
        namaMember.setText(namaMemberS);
        namaPanggung.setText(namaPanggungS);
        tanggalLahir.setText(tanggalLahirS);


    }
}
