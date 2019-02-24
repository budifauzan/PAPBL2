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

public class GroupsAdapter extends ArrayAdapter<Groups> {

    public GroupsAdapter(Activity context, ArrayList<Groups> groups) {
        super(context, 0, groups);
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //To inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_groups_list_item, parent,
                    false);
        }
        //To determine the index
        Groups currentGroup = getItem(position);

        TextView groupName = listItemView.findViewById(R.id.list_item_groups_name);
        groupName.setText(currentGroup.getNamaGroup());

        TextView groupAgency = listItemView.findViewById(R.id.list_item_groups_agency);
        groupAgency.setText(currentGroup.getAgensi());

        //Return each view that have been determined
        return listItemView;
    }
}