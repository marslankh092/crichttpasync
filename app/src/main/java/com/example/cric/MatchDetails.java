package com.example.cric;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MatchDetails extends AppCompatActivity {

    private TextView Status,type,Date,t1,t2,score,scorelabel,desc;
    private String id,text,matchName;
    private String TAG = MatchDetails.class.getSimpleName();
    // tmp hash map for single match
    HashMap<String, String> match = new HashMap<>();
    private Boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_details);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        matchName = intent.getStringExtra("matchName");
        this.setTitle(matchName);

        Status =(TextView) findViewById(R.id.status);
        type =(TextView) findViewById(R.id.type);
        Date =(TextView) findViewById(R.id.date);
        t1 =(TextView) findViewById(R.id.team1);
        t2 =(TextView) findViewById(R.id.team2);
        score =(TextView) findViewById(R.id.score);
        scorelabel =(TextView) findViewById(R.id.scorelabel);
        desc =(TextView) findViewById(R.id.description);

        new DisplayMatchDetails().execute();
    }

    private class DisplayMatchDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MatchDetails.this,"Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://cricapi.com/api/cricketScore?unique_id="+id+"&apikey=DVueR4fUxqMczbT9vWLlVn57H9o1";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);
                    String req = c.getString("innings-requirement");
                    String date = c.getString("dateTimeGMT");
                    String type = c.getString("type");
                    String team2 = c.getString("team-2");
                    String team1 = c.getString("team-1");
                    String score="";
                    start =c.getBoolean("matchStarted");

                    // adding each child node to HashMap key => value
                    match.put("req", req);
                    match.put("date", date);
                    match.put("team1", team1);
                    match.put("team2", team2);
                    match.put("type", type);
                    if(start)
                        score = c.getString("score");
                    match.put("score", score+"\n");

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
            Status.setText(match.get("req"));
            type.setText(match.get("type"));
            Date.setText(match.get("date"));
            t1.setText(match.get("team1"));
            t2.setText(match.get("team2"));
            if(start) {
                score.setText(match.get("score"));
                scorelabel.setText("score");
            }
            else {
                score.setText("");
                scorelabel.setText("");
            }
            desc.setText(matchName);
        }
    }
}
