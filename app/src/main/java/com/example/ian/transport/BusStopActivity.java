package com.example.ian.transport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class BusStopActivity extends AppCompatActivity {

    protected ProgressBar progressBar;

    protected JSONObject schedule;
    private String stopId;
    private final String API_KEY = "";

    private final String ARRIVAL_TIME = "arrival_time";
    private final String ROUTE_ID = "stop_sequence";

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);

        if(API_KEY == "" || API_KEY.isEmpty()) {
            Toast.makeText(BusStopActivity.this,
                    "Please enter a AT transport API key in your code",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        Intent intent = getIntent();
        stopId = intent.getStringExtra(StopsListActivity.STOP_NUMBER);
        stopId = stopId.substring(6);
        String stopAd = intent.getStringExtra(StopsListActivity.STOP_ADDRESS);
        TextView stopHeaderAddress = (TextView) findViewById(R.id.stopHeaderAddress);
        TextView stopHeaderId = (TextView) findViewById(R.id.stopHeaderId);
        stopHeaderAddress.setText(stopAd);
        stopHeaderId.setText("Stop: " + stopId);

        lv = (ListView) findViewById(R.id.listArrivals);

        progressBar = (ProgressBar) findViewById(R.id.pbar);
        if(isNetworkAvailable()){
            GetSchedule times = new GetSchedule();
            times.execute();
        }else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.delete_stop:
                deleteAndGoBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAndGoBack() {
        deleteStop(stopId);
        finish();
    }

    private void deleteStop(String stopId) {
        getContentResolver().delete(StopsProvider.CONTENT_URI, DBOpenHelper.STOP_ID+"="+stopId, null);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void deleteAndGoBack(View view) {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            deleteStop(stopId);

                            Toast.makeText(BusStopActivity.this,
                                    getString(R.string.stop_deleted),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private class GetSchedule extends AsyncTask<Object , Void,JSONObject>{

        int responseCode = -1;

        @Override
        protected JSONObject doInBackground(Object... params) {
            Log.d("testing", "testing");
            JSONObject jsonResponse = null;
            try{                                        //TODO This method takes too much time, find a faster implementation
                URL APIUrl = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/"+stopId+"?api_key="+API_KEY);
                HttpURLConnection connection = (HttpURLConnection) APIUrl.openConnection();
                connection.connect();
                responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int nextCharacter; // read() returns an int, we cast it to char later
                    String responseData = "";
                    while (true) { // Infinite loop, can only be stopped by a "break" statement
                        nextCharacter = reader.read(); // read() without parameters returns one character
                        if (nextCharacter == -1) // A return value of -1 means that we reached the end
                            break;
                        responseData += (char) nextCharacter; // The += operator appends the character to the end of the string
                    }
                    jsonResponse = new JSONObject(responseData);
                }else{
                    Log.i(BusStopActivity.class.getSimpleName(), "Unsuccessful HTTP response Code: " + responseCode);
                }
            }catch (Exception e){
                logException(e);
            }finally {
                return jsonResponse;
            }
        }

        protected void onPostExecute(JSONObject result){
            schedule = result;
            receivedResponce();
        }
    }

    private void receivedResponce() {
        progressBar.setVisibility(View.GONE);
        if(schedule == null){
            //TODO show an icon to express this situation
        }else{
            try{
                JSONArray jArray = schedule.getJSONArray("response");
                JSONObject arrival;
                Date currentDate = new Date();
                ArrayList<BusArrival> busArray = new ArrayList<BusArrival>();
                BusArrival bus;
                int successCounter = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    arrival = jArray.getJSONObject(i);
                    String time = arrival.getString(ARRIVAL_TIME);
                    String route = arrival.getString(ROUTE_ID);
                    bus = new BusArrival(route, time);
                    if(currentDate.compareTo(bus.getDate())>0){
                        continue;
                    }
                    busArray.add(bus);
                    successCounter++;
                    if (successCounter>14){
                        break;
                    }
                }
                ArrivalsAdapter adapter = new ArrivalsAdapter(this, R.layout.arrival_list_item, busArray);
                lv.setAdapter(adapter);



            }catch (JSONException e){
                Log.d(BusStopActivity.class.getSimpleName(), e.getStackTrace().toString());
            }
        }

    }

    private void logException(Exception e) {
        Log.e(BusStopActivity.class.getSimpleName(),e.getStackTrace().toString());
    }
}

/***************************************************************************
Code store for old code:

received response():

                /*JSONArray jArray = schedule.getJSONArray("response");
                ArrayList<HashMap<String, String>> busArrivals =
                        new ArrayList<HashMap<String, String>>();
                JSONObject arrival;
                for (int i = 0; i < jArray.length(); i++) {
                    arrival = jArray.getJSONObject(i);
                    String time = arrival.getString(ARRIVAL_TIME);
//                    time = Html.fromHtml(time).toString();
                    String route = arrival.getString(ROUTE_ID);
//                    route = Html.fromHtml(route).toString();

                    HashMap<String, String> arrivalInstance = new HashMap<String, String>();
                    arrivalInstance.put(ROUTE_ID, route);
                    arrivalInstance.put(ARRIVAL_TIME, time);

                    busArrivals.add(arrivalInstance);
                }
                String[] keys = {ROUTE_ID, ARRIVAL_TIME};
                int[] ids = {android.R.id.text1, android.R.id.text2};
                SimpleAdapter adapter = new SimpleAdapter(this, busArrivals,
                        R.layout.arrival_list_item, keys, ids);
                lv.setAdapter(adapter);*/

                 /*if(jArray.length()==0){
                    TextView tv = (TextView) findViewById(R.id.textViewNoServices);
                    tv.setVisibility(View.VISIBLE);
                }
     */

