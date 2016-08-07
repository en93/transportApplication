package com.example.ian.transport;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TempNewStop extends AppCompatActivity {

    private String stopNum;

    private String STOP_ID = "stop_id";
    private String STOP_DESC = "stop_name";

    private ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_new_stop);

        final EditText et = (EditText) findViewById(R.id.newStopText);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_GO){
                    stopNum = et.getText().toString();

                    bar = (ProgressBar) findViewById(R.id.progressBarAdd);
                    bar.setVisibility(View.VISIBLE);
                    makeNewStop stop = new makeNewStop();
                    stop.execute();
                    handled = true;
                }
                return handled;
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temp_new_stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertStop(String stop, String address, String desc) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.STOP_ID, stop);
        values.put(DBOpenHelper.STOP_ADDRESS, address);
        values.put(DBOpenHelper.STOP_DESCRIPTION, desc);
        getContentResolver().insert(StopsProvider.CONTENT_URI, values);

    }

    public void addSampleData(View view) {
        insertStop("7012", "Opposite 6 Greenmount Drive", "null");
        insertStop("7239", "International Airport Arrivals", "null");
        insertStop("5906", "45 Marellen Drive", "null");
        insertStop("5908", "Old Lake Rd By Woodall Park", "null");
        Toast t = Toast.makeText(this, "Sample data was added", Toast.LENGTH_LONG);
        t.show();
        finish();

    }

    private class makeNewStop extends AsyncTask<Object , Void,JSONObject> {

        int responseCode = -1;

        @Override
        protected JSONObject doInBackground(Object... params) {


            JSONObject jsonResponse = null;

            try{
                URL APIUrl = new URL("https://api.at.govt.nz/v1/gtfs/stops/stopId/"+stopNum+"?api_key=e662c62b-5c16-41f2-86e3-031993be4a4b"); //TODO further break into modular constants
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
//                  Log.d("JSONRes", responseData);
                    jsonResponse = new JSONObject(responseData);
                }else{
                    Log.i(BusStopActivity.class.getSimpleName(), "Unsuccessful HTTP response Code: " + responseCode);
                }
            }catch (Exception e){
                //TODO handle this case properly
                Toast.makeText(TempNewStop.this,
                        "An error was encountered adding the stop",
                        Toast.LENGTH_LONG).show();
                finish();

            }finally {
                return jsonResponse;
            }
        }

        protected void onPostExecute(JSONObject result){
            try {
                addStop(result.getJSONArray("response"));
            }catch (Exception e){

            }
        }

    }
    private void addStop(JSONArray jsonArray){
        try {
            ContentValues values = new ContentValues();
            JSONObject jObject = jsonArray.getJSONObject(0);
            values.put(DBOpenHelper.STOP_ID, jObject.getString(STOP_ID));
            values.put(DBOpenHelper.STOP_ADDRESS, jObject.getString(STOP_DESC));
            getContentResolver().insert(StopsProvider.CONTENT_URI, values);
            bar.setVisibility(View.GONE);
            Toast t = Toast.makeText(this, "Stop " + stopNum + " has been added", Toast.LENGTH_LONG);
            t.show();
            finish();
        }catch (Exception e){ //todo change so the problem is diagnosed and recovered from
            Toast.makeText(TempNewStop.this,
                    "An error was encountered adding the stop",
                    Toast.LENGTH_LONG).show();
            finish();
        }


    }
    
}
