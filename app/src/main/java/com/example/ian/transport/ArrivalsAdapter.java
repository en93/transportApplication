package com.example.ian.transport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ian on 1/09/2015.
 */
public class ArrivalsAdapter extends ArrayAdapter<BusArrival> {

    ArrayList<BusArrival> arrivals;

    public ArrivalsAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.arrivals = (ArrayList<BusArrival>) objects;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.arrival_list_item, null);
        }
        BusArrival busArrival = arrivals.get(position);
        TextView route = (TextView) v.findViewById(R.id.routeNum);
        TextView time = (TextView) v.findViewById(R.id.arrivalTime);
        route.setText(busArrival.getRouteNum());
        time.setText(busArrival.getTime());
        return v;
    }
}
