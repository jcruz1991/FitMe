package com.fitme.fitme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitme.fitme.R;
import com.fitme.fitme.model.UserLocation;

import java.util.List;

public class ListShowUserActivity extends BaseAdapter{

    private Context mContext;
    private List<UserLocation> mProductList;
    public ListShowUserActivity(Context mContext, List<UserLocation> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mProductList.get(position).getId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.user_item,null);
        TextView tvUser = (TextView)v.findViewById(R.id.tvUserName);
        TextView tvLocation = (TextView)v.findViewById(R.id.tvLocation);
        TextView tvWorkout = (TextView)v.findViewById(R.id.tvWorkout);
        tvUser.setText(mProductList.get(position).getName());
        tvLocation.setText(mProductList.get(position).getCity());
        tvWorkout.setText(mProductList.get(position).getUser_workout());
        return v;
    }
}
