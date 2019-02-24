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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.HashMap;

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
    double mxn = 0, aud = 0, hkd = 0, ron = 0, hrk = 0, chf = 0, cad = 0, usd = 0, jpy = 0, brl = 0,
            searchResult = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_web, container, false);
        search = rootView.findViewById(R.id.web_search_button);
        searchText = rootView.findViewById(R.id.web_search_edit_text);

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            final String localResult = result;
            httpConn.disconnect();
            progressDialog.dismiss();
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rates.clear();
                    search.setText("Search");
                    if (searchText.getText().toString().equalsIgnoreCase("")) {
                        try {
                            JSONObject jsonObj = new JSONObject(localResult);
                            // Getting JSON Array node
                            JSONObject rates = jsonObj.getJSONObject("rates");
                            mxn = Double.valueOf(rates.getString("MXN"));
                            aud = Double.valueOf(rates.getString("AUD"));
                            hkd = Double.valueOf(rates.getString("HKD"));
                            ron = Double.valueOf(rates.getString("RON"));
                            hrk = Double.valueOf(rates.getString("HRK"));
                            chf = Double.valueOf(rates.getString("CHF"));
                            cad = Double.valueOf(rates.getString("CAD"));
                            usd = Double.valueOf(rates.getString("USD"));
                            jpy = Double.valueOf(rates.getString("JPY"));
                            brl = Double.valueOf(rates.getString("BRL"));
                            date = jsonObj.getString("date");
                            base = jsonObj.getString("base");

                        } catch (final JSONException e) {
                            Toast.makeText(rootView.getContext(), "Json parsing error: "
                                    + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        rates.add(new ExchangeRates("MXN", date, base, mxn));
                        rates.add(new ExchangeRates("AUD", date, base, aud));
                        rates.add(new ExchangeRates("HKD", date, base, hkd));
                        rates.add(new ExchangeRates("RON", date, base, ron));
                        rates.add(new ExchangeRates("HRK", date, base, hrk));
                        rates.add(new ExchangeRates("CHF", date, base, chf));
                        rates.add(new ExchangeRates("CAD", date, base, cad));
                        rates.add(new ExchangeRates("USD", date, base, usd));
                        rates.add(new ExchangeRates("JPY", date, base, jpy));
                        rates.add(new ExchangeRates("BRL", date, base, brl));
                    } else {
                        try {
                            JSONObject jsonObj = new JSONObject(localResult);
                            // Getting JSON Array node
                            JSONObject rates = jsonObj.getJSONObject("rates");
                            searchResult = Double.valueOf(rates.getString(searchText.getText()
                                    .toString().toUpperCase()));
                            date = jsonObj.getString("date");
                            base = jsonObj.getString("base");

                        } catch (final JSONException e) {
                            Toast.makeText(rootView.getContext(), "Json parsing error: "
                                    + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        rates.add(new ExchangeRates(searchText.getText().toString().toUpperCase()
                                , date, base, searchResult));
                    }
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
                            if (arrow.getText().toString().equalsIgnoreCase("→")) {
                                arrow.setText("←");
                                exchangeRate = 1 / exchangeRate;
                                rate.setText(String.format("%.2f", exchangeRate));
                            } else {
                                arrow.setText("→");
                                rate.setText(String.format("%.10f", exchangeRate));
                            }
                        }
                    });
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


    }

}

