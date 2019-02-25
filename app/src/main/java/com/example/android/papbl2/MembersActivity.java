package com.example.android.papbl2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class MembersActivity extends AppCompatActivity {
    MembersAdapter adapter;
    ListView listView;
    EditText searchText;
    Button search;
    ArrayList<Members> members;
    ProgressDialog progressDialog;
    String webContent;
    HttpURLConnection httpConn;
    String id, namaGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        search = findViewById(R.id.members_search_button);
        searchText = findViewById(R.id.members_search_edit_text);
        //Specify the list view
        listView = findViewById(R.id.members_list_view);

        Intent intent = getIntent();
        id = intent.getStringExtra("idGroup");
        namaGroup = intent.getStringExtra("namaGroup");

        members = new ArrayList<>();

        downloadWebContent("http://"+new IPAddress().getIpAddress()+"/PAPBL/PAPBL2/web_service.php");

    }

    private void downloadWebContent(String urlStr) {

        ConnectivityManager connMgr = (ConnectivityManager) MembersActivity.this
                .getSystemService(MembersActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Check for network connections
        if (networkInfo != null && networkInfo.isConnected()) {

            // Create background thread to connect and get data
            new DownloadWebTask().execute(urlStr);
        } else {
            Toast.makeText(MembersActivity.this, "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadWebTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MembersActivity.this);
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
                    members.clear();
                    if (searchText.getText().toString().equalsIgnoreCase("")) {
                        showAllData(result);
                    } else {
                        try {
                            JSONObject jsonObj = new JSONObject(localResult);
                            JSONArray membersArr = jsonObj.getJSONArray("Members");
                            for (int i = 0; i < membersArr.length(); i++) {
                                JSONObject g = membersArr.getJSONObject(i);
                                if (g.getString("IdGroup").equalsIgnoreCase(id)
                                        && g.getString("NamaPanggung").toLowerCase().contains(searchText.getText().toString())) {
                                    int idMember = Integer.valueOf(g.getString("IdMember"));
                                    int idGroup = Integer.valueOf(g.getString("IdGroup"));
                                    String namaMember = g.getString("NamaMember");
                                    String namaPanggung = g.getString("NamaPanggung");
                                    String tanggalLahir = g.getString("TanggalLahir");
                                    members.add(new Members(idMember, idGroup, namaMember, namaPanggung, tanggalLahir));
                                }
                            }
                            if (members.isEmpty()) {
                                Toast.makeText(MembersActivity.this, "No value for "
                                        + searchText.getText().toString(), Toast.LENGTH_LONG).show();
                            }

                        } catch (final JSONException e) {
                            Toast.makeText(MembersActivity.this, "Json parsing error: "
                                    + e.getMessage(), Toast.LENGTH_LONG).show();
                            listView.setAdapter(null);
                            return;
                        }
                    }
                    adapter = new MembersAdapter(MembersActivity.this, members);

                    //Attaching the adapter to the list view
                    listView.setAdapter(adapter);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MembersActivity.this, MembersDetailActivity.class);
                    intent.putExtra("namaGroup", namaGroup);
                    intent.putExtra("namaMember", members.get(position).getNamaMember());
                    intent.putExtra("namaPanggung", members.get(position).getNamaPanggung());
                    intent.putExtra("tanggalLahir", members.get(position).getTanggalLahir());
                    MembersActivity.this.startActivity(intent);
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
                JSONArray membersArr = jsonObj.getJSONArray("Members");
                for (int i = 0; i < membersArr.length(); i++) {
                    JSONObject g = membersArr.getJSONObject(i);
                    if (g.getString("IdGroup").equalsIgnoreCase(id)) {
                        int idMember = Integer.valueOf(g.getString("IdMember"));
                        int idGroup = Integer.valueOf(g.getString("IdGroup"));
                        String namaMember = g.getString("NamaMember");
                        String namaPanggung = g.getString("NamaPanggung");
                        String tanggalLahir = g.getString("TanggalLahir");
                        members.add(new Members(idMember, idGroup, namaMember, namaPanggung, tanggalLahir));
                    }
                }

            } catch (final JSONException e) {
                Toast.makeText(MembersActivity.this, "Json parsing error: "
                        + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            adapter = new MembersAdapter(MembersActivity.this, members);

            //Attaching the adapter to the list view
            listView.setAdapter(adapter);
        }


    }
}
