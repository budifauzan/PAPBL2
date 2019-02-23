package com.example.android.papbl2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WebFragment extends Fragment {
    View rootView;
    ExchangeRatesAdapter adapter;
    ListView listView;
    EditText searchText;
    Button search;
    ArrayList<ExchangeRates> rates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_web, container, false);
        search = rootView.findViewById(R.id.web_search_button);
        searchText = rootView.findViewById(R.id.web_search_edit_text);

        rates = new ArrayList<>();

        rates.add(new ExchangeRates("USD", 0.0000712251));
        rates.add(new ExchangeRates("EUR", 0.0000627976));
        rates.add(new ExchangeRates("HKD", 0.0005590621));

        adapter = new ExchangeRatesAdapter(getActivity(), rates);

        //Specify the list view
        listView = rootView.findViewById(R.id.web_list_view);

        //Attaching the adapter to the list view
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView arrow = view.findViewById(R.id.web_list_item_arrow);
                final TextView rate = view.findViewById(R.id.web_list_item_rate);
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

        ArrayList<ExchangeRates> resultRates = new ArrayList<>();
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < rates.size(); i++) {
                    if (rates.get(i).getCurrency().startsWith(searchText.getText().toString().toUpperCase())) {
                        Toast.makeText(rootView.getContext(), rates.get(i).getCurrency(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return rootView;
    }

}

