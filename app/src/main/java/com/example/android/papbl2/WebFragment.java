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
    String webContent;
    HttpURLConnection httpConn;

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

        ArrayList<ExchangeRates> resultRates = new ArrayList<>();

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                for (int i = 0; i < rates.size(); i++) {
//                    if (rates.get(i).getCurrency().startsWith(searchText.getText().toString().toUpperCase())) {
//                        Toast.makeText(rootView.getContext(), rates.get(i).getCurrency(), Toast.LENGTH_SHORT).show();
//                    }
//                }
                downloadWebContent("https://api.exchangeratesapi.io/latest?base=IDR");
            }
        });


        return rootView;
    }

    private void downloadWebContent(String urlStr) {

        ConnectivityManager connMgr = (ConnectivityManager) rootView.getContext().getSystemService(rootView.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Check for network connections

        if (networkInfo != null && networkInfo.isConnected()) {
            // Create background thread to connect and get data

            new DownloadWebTask().execute(urlStr);
        } else {
            Toast.makeText(rootView.getContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(rootView.getContext(), result, Toast.LENGTH_SHORT).show();
            httpConn.disconnect();
            progressDialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        private InputStream openHttpConnection(String urlStr) {
            InputStream in = null;
            int resCode = -1;

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

