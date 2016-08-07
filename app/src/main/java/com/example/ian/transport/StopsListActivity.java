package com.example.ian.transport;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class StopsListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String STOP_NUMBER = "StopNumber";
    public static final String STOP_ADDRESS = "StopAddress";
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_list);
        cursorAdapter = new StopsCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    private void testInsertStop(String stop, String address, String desc) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.STOP_ID, stop);
        values.put(DBOpenHelper.STOP_ADDRESS, address);
        values.put(DBOpenHelper.STOP_DESCRIPTION, desc);
        Uri noteUri = getContentResolver().insert(StopsProvider.CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    public void onResume(){
        super.onResume();
        restartLoader();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
           case R.id.action_delete_all:
                clearAllData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearAllData() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(StopsProvider.CONTENT_URI, null, null);
                            restartLoader();
                            Toast.makeText(StopsListActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, StopsProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void startTimeTable(View view) {
        String stopNumber;
        String stopAddress;
        TextView tvId = (TextView) view.findViewById(R.id.tvStopNum);
        TextView tvAd = (TextView) view.findViewById(R.id.tvStop);
        stopNumber = tvId.getText().toString();
        stopAddress = tvAd.getText().toString();
        Intent intent = new Intent(this, BusStopActivity.class);
        intent.putExtra(STOP_NUMBER, stopNumber);
        intent.putExtra(STOP_ADDRESS, stopAddress);
        startActivity(intent);
    }

    public void createNewStop(View view) {
        Intent intent = new Intent(this, TempNewStop.class);
        startActivity(intent);
    }
}
