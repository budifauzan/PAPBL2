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

public class ExchangeRatesAdapter extends ArrayAdapter<ExchangeRates> {

    public ExchangeRatesAdapter(Activity context, ArrayList<ExchangeRates> rates) {
        super(context, 0, rates);
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //To inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_web_list_item, parent,
                    false);
        }
        //To determine the index
        ExchangeRates currentRate = getItem(position);

        TextView currency = listItemView.findViewById(R.id.web_list_item_currency);
        currency.setText(currentRate.getCurrency());

        TextView date = listItemView.findViewById(R.id.web_list_date);
        date.setText(currentRate.getDate());

        TextView base = listItemView.findViewById(R.id.web_list_item_base);
        base.setText(currentRate.getBase());

        TextView arrow = listItemView.findViewById(R.id.web_list_item_arrow);
        arrow.setText(currentRate.getExchangeArrow());

        TextView rate = listItemView.findViewById(R.id.web_list_item_rate);;
        rate.setText(String.format ("%.10f", currentRate.getRate()));

        //Return each view that have been determined
        return listItemView;
    }

}
