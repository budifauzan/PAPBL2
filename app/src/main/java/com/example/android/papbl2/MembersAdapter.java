package com.example.android.papbl2;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MembersAdapter extends ArrayAdapter<Members> {

    public MembersAdapter(Activity context, ArrayList<Members> members) {
        super(context, 0, members);
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //To inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_members_list_item, parent,
                    false);
        }
        //To determine the index
        Members currentMember = getItem(position);

        TextView birthName = listItemView.findViewById(R.id.list_item_members_real_name);
        birthName.setText(currentMember.getNamaMember());

        TextView stageName = listItemView.findViewById(R.id.list_item_members_stage_name);
        stageName.setText(currentMember.getNamaPanggung());

        //Return each view that have been determined
        return listItemView;
    }
}