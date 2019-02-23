package com.example.android.papbl2;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class WebFragment extends Fragment {
    View rootView;
    ExchangeRatesAdapter adapter;
    ListView listView;
    ArrayList<ExchangeRates> rates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_web, container, false);

        rates = new ArrayList<>();

        rates.add(new ExchangeRates("USD", 0.0000712251));
        rates.add(new ExchangeRates("EUR", 0.0000627976));
        rates.add(new ExchangeRates("HKD", 0.0005590621));

        adapter = new ExchangeRatesAdapter(getActivity(), rates);

        //Specify the list view
        listView = rootView.findViewById(R.id.web_list_view);

        Button view = rootView.findViewById(R.id.web_view_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Attaching the adapter to the list view
                listView.setAdapter(adapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flip(view, position);
            }
        });

        return rootView;
    }

    private void flip(View view, final int position) {
        ImageView flip = view.findViewById(R.id.web_list_item_flip);
        final TextView arrow = view.findViewById(R.id.web_list_item_arrow);
        final TextView rate = view.findViewById(R.id.web_list_item_rate);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double exchangeRate = rates.get(position).getRate();
                if (arrow.getText().toString().equalsIgnoreCase("->")) {
                    arrow.setText("<-");
                    exchangeRate = 1 / exchangeRate;
                    rate.setText(String.format("%.2f", exchangeRate));
                } else {
                    arrow.setText("->");
                    rate.setText(String.format("%.10f", exchangeRate));
                }
            }
        });
    }
}

