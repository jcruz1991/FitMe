package com.fitme.fitme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitme.fitme.R;
import com.fitme.fitme.model.UserLocation;

import java.util.List;

public class ListUserAdapter extends BaseAdapter {
    private Context context;
    private List<UserLocation> userLocations;

    public ListUserAdapter(Context context, List<UserLocation> userLocations) {
        this.context = context;
        this.userLocations = userLocations;
    }

    @Override
    public int getCount() {
        return userLocations.size();
    }

    @Override
    public Object getItem(int position) {
        return userLocations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userLocations.get(position).getId();
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, R.layout.location_item_list, null);
        TextView email = (TextView) v.findViewById(R.id.email);
        TextView city = (TextView) v.findViewById(R.id.city);
        email.setText(userLocations.get(position).getEmail());
        city.setText("City: " + userLocations.get(position).getCity());
        return v;
    }
}
