package com.example.android.papbl2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GroupsDetailActivity extends AppCompatActivity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_detail);
        Button memberList = findViewById(R.id.groups_detail_member_list_button);
        TextView namaGroup = findViewById(R.id.groups_detail_name);
        TextView agensiGroup = findViewById(R.id.groups_detail_agency);
        TextView tanggalDebut = findViewById(R.id.groups_detail_debut_date);
        TextView jumlahMember = findViewById(R.id.groups_detail_members);

        Intent intent = getIntent();
        final String namaGroupS = intent.getStringExtra("namaGroup");
        String agensiGroupS = intent.getStringExtra("agensiGroup");
        String tanggalDebutS = intent.getStringExtra("tanggalDebut");
        String jumlahMemberS = intent.getStringExtra("jumlahMember");
        id = intent.getStringExtra("id");

        namaGroup.setText(namaGroupS);
        agensiGroup.setText(agensiGroupS);
        tanggalDebut.setText(tanggalDebutS);
        jumlahMember.setText(jumlahMemberS);

        memberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsDetailActivity.this, MembersActivity.class);
                intent.putExtra("idGroup", id);
                intent.putExtra("namaGroup", namaGroupS);
                startActivity(intent);
            }
        });
    }
}
