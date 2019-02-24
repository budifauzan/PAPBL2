package com.example.android.papbl2;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class GroupsFragment extends Fragment {
    View rootView;
    GroupsAdapter adapter;
    ListView listView;
    EditText searchText;
    Button search;
    ArrayList<Groups> groups;
    ProgressDialog progressDialog;
    String webContent;
    HttpURLConnection httpConn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_groups, container, false);
        search = rootView.findViewById(R.id.groups_search_button);
        searchText = rootView.findViewById(R.id.groups_search_edit_text);
        //Specify the list view
        listView = rootView.findViewById(R.id.groups_list_view);

        groups = new ArrayList<>();

        downloadWebContent("http://192.168.43.139/PAPBL/PAPBL2/web_service.php");

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
                    groups.clear();
                    search.setText("Search");
                    if (searchText.getText().toString().equalsIgnoreCase("")) {
                        try {
                            JSONObject jsonObj = new JSONObject(localResult);
                            JSONArray groupsArr = jsonObj.getJSONArray("Groups");
                            for (int i = 0; i < groupsArr.length(); i++) {
                                JSONObject g = groupsArr.getJSONObject(i);
                                int idGroup = Integer.valueOf(g.getString("IdGroup"));
                                String namaGroup = g.getString("NamaGroup");
                                String agensi = g.getString("Agensi");
                                String tanggalDebut = g.getString("TanggalDebut");
                                String jumlahMember = g.getString("JumlahMember");
                                groups.add(new Groups(idGroup, namaGroup, agensi, tanggalDebut, jumlahMember));
                            }

                        } catch (final JSONException e) {
                            Toast.makeText(rootView.getContext(), "Json parsing error: "
                                    + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            JSONObject jsonObj = new JSONObject(localResult);
                            JSONArray groupsArr = jsonObj.getJSONArray("Groups");
                            for (int i = 0; i < groupsArr.length(); i++) {
                                JSONObject g = groupsArr.getJSONObject(i);
                                if (searchText.getText().toString().equalsIgnoreCase(g.getString("NamaGroup"))) {
                                    int idGroup = Integer.valueOf(g.getString("IdGroup"));
                                    String namaGroup = g.getString("NamaGroup");
                                    String agensi = g.getString("Agensi");
                                    String tanggalDebut = g.getString("TanggalDebut");
                                    String jumlahMember = g.getString("JumlahMember");
                                    groups.add(new Groups(idGroup, namaGroup, agensi, tanggalDebut, jumlahMember));
                                    break;
                                }
                            }
                            if (groups.isEmpty()) {
                                Toast.makeText(rootView.getContext(), "No value for "
                                        + searchText.getText().toString(), Toast.LENGTH_LONG).show();
                            }

                        } catch (final JSONException e) {
                            Toast.makeText(rootView.getContext(), "Json parsing error: "
                                    + e.getMessage(), Toast.LENGTH_LONG).show();
                            listView.setAdapter(null);
                            return;
                        }
                    }
                    adapter = new GroupsAdapter(getActivity(), groups);

                    //Attaching the adapter to the list view
                    listView.setAdapter(adapter);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(rootView.getContext(), GroupsDetailActivity.class);
                    intent.putExtra("id", groups.get(position).getId());
                    intent.putExtra("namaGroup", groups.get(position).getNamaGroup());
                    intent.putExtra("agensiGroup", groups.get(position).getAgensi());
                    intent.putExtra("tanggalDebut", groups.get(position).getTanggalDebut());
                    intent.putExtra("jumlahMember", groups.get(position).getJumlahMember());
                    rootView.getContext().startActivity(intent);
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
