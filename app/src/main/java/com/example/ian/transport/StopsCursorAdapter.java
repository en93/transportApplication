package com.example.ian.transport;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Ian on 22/08/2015.
 */
public class StopsCursorAdapter extends CursorAdapter {
    public StopsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.stop_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvStopAddress;
        TextView tvStopID;

        String stopNumber = "Stop: " + cursor.getString(cursor.getColumnIndex(DBOpenHelper.STOP_ID));
        String stopAddress = cursor.getString(cursor.getColumnIndex(DBOpenHelper.STOP_ADDRESS));

        tvStopID = (TextView) view.findViewById(R.id.tvStopNum);
        tvStopAddress = (TextView) view.findViewById(R.id.tvStop);

        tvStopID.setText(stopNumber);
        tvStopAddress.setText(stopAddress);



    }
}
