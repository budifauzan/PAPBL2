package com.example.android.papbl2;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebFragment extends Fragment {
    View rootView;
    ExchangeRatesAdapter adapter;
    ListView listView;
    EditText searchText;
    Button search;
    ArrayList<ExchangeRates> rates;
    ProgressDialog progressDialog;
    String webContent, date, base;
    HttpURLConnection httpConn;
    int dataLength = 0;
    String currency[] = {"MXN", "AUD", "HKD", "RON", "HRK", "CHF", "IDR", "CAD", "USD", "JPY", "BRL"
            , "PHP", "CZK", "NOK", "INR", "PLN", "ISK", "MYR", "ZAR", "ILS", "GBP", "SGD", "HUF"
            , "EUR", "CNY", "TRY", "SEK", "RUB", "NZD", "KRW", "THB", "BGN", "DKK"};
    double exchangeRate[] = new double[33];
    double searchResult = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_web, container, false);
        search = rootView.findViewById(R.id.web_search_button);
        searchText = rootView.findViewById(R.id.web_search_edit_text);
        //Specify the list view
        listView = rootView.findViewById(R.id.web_list_view);

        rates = new ArrayList<>();

        downloadWebContent("https://api.exchangeratesapi.io/latest?base=IDR");

        return rootView;
    }

    private void downloadWebContent(String urlStr) {

        ConnectivityManager connMgr = (ConnectivityManager) rootView.getContext()
                .getSystemService(rootView.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Check for network connections
        if (networkInfo != null && networkInfo.isConnected()) {

            // Create background thread to connect and get data
            new DownloadWebTask().execute(urlStr);
        } else {
            Toast.makeText(rootView.getContext(), "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadWebTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(rootView.getContext());
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            InputStream in;

            try {
                StringBuilder sb = new StringBuilder();
                in = openHttpConnection(url);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String nextLine;
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                    webContent = sb.toString();
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", "doInBackground: Input stream process");
            }

            return webContent;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            final String localResult = result;
            httpConn.disconnect();
            progressDialog.dismiss();
            showAllData(result);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rates.clear();
                    if (searchText.getText().toString().equalsIgnoreCase("")) {
                        showAllData(result);
                    } else {
                        try {
                            JSONObject jsonObj = new JSONObject(localResult);
                            // Getting JSON Array node
                            JSONObject ratesObj = jsonObj.getJSONObject("rates");
                            for (int i = 0; i < currency.length; i++) {
                                if (currency[i].toLowerCase().contains(searchText.getText().toString())) {
                                    searchResult = Double.valueOf(ratesObj.getString(currency[i]));
                                    date = jsonObj.getString("date");
                                    base = jsonObj.getString("base");
                                    rates.add(new ExchangeRates(currency[i], date, base, searchResult));
                                }
                            }

                        } catch (final JSONException e) {
                            Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            listView.setAdapter(null);
                            return;
                        }
                    }
                    adapter = new ExchangeRatesAdapter(getActivity(), rates);

                    //Attaching the adapter to the list view
                    listView.setAdapter(adapter);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (rates.get(position).getExchangeArrow().equalsIgnoreCase("→")) {
                        rates.get(position).setExchangeArrow("←");
                    } else {
                        rates.get(position).setExchangeArrow("→");
                    }
                    rates.get(position).setRate(1 / rates.get(position).getRate());
                    listView.invalidateViews();
                }
            });

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        private InputStream openHttpConnection(String urlStr) {
            InputStream in = null;
            int resCode;

            try {
                URL url = new URL(urlStr);

                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setReadTimeout(10000 /* milliseconds */);
                httpConn.setConnectTimeout(15000 /* milliseconds */);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();

                } else {
                    Log.e("HTTP", "openHttpConnection: Host not reached");
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("Error", "openHttpConnection: URL not correct", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", "openHttpConnection: host not reach", e);
            }
            return in;
        }

        private void showAllData(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                // Getting JSON Array node
                JSONObject rates = jsonObj.getJSONObject("rates");
                for (int i = 0; i < rates.length(); i++) {
                    exchangeRate[i] = Double.valueOf(rates.getString(currency[i]));
                }
                dataLength = rates.length();
                date = jsonObj.getString("date");
                base = jsonObj.getString("base");

            } catch (final JSONException e) {
                Toast.makeText(rootView.getContext(), "Json parsing error: "
                        + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            for (int i = 0; i < dataLength; i++) {
                rates.add(new ExchangeRates(currency[i], date, base, exchangeRate[i]));
            }
            adapter = new ExchangeRatesAdapter(getActivity(), rates);

            //Attaching the adapter to the list view
            listView.setAdapter(adapter);
        }

    }

}

