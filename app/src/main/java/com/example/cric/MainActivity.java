package com.example.cric;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<String> matchNameList,idList;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matchNameList = new ArrayList<>();
        idList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);

        new GetMatchDetails().execute();
    }
    private class GetMatchDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.cricapi.com/v1/players?apikey=acdcba6a-5e79-4d7d-8efc-650c785096e2&offset=0";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray matches = jsonObj.getJSONArray("data");

                    // looping through All Contacts
                    for (int i = 0; i < matches.length(); i++) {
                        JSONObject c = matches.getJSONObject(i);
                        String id = c.getString("unique_id");
                        String title = c.getString("title");
//                        if(c.getBoolean("matchStarted")==false)
//                            continue;

                        // tmp hash map for single match
                        HashMap<String, String> match = new HashMap<>();

                        // adding each child node to HashMap key => value
                        match.put("id", id);
                        match.put("title", title);
                        matchNameList.add(title);
                        idList.add(id);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mAdapter = new RecyclerAdapter(matchNameList,idList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}