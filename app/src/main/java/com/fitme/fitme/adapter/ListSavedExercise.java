package com.fitme.fitme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitme.fitme.R;
import com.fitme.fitme.model.Workout;

import java.util.List;

public class ListSavedExercise extends BaseAdapter {
    private Context mContext;
    private List<Workout> mProductList;


    public ListSavedExercise(Context mContext, List<Workout> mProductList) {
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

        View v = View.inflate(mContext, R.layout.item_listview,null);
        TextView tvex_name = (TextView)v.findViewById(R.id.tvex_name);
        TextView tBody_type = (TextView)v.findViewById(R.id.tBody_type);
        TextView tExc_type = (TextView)v.findViewById(R.id.textView);
        tBody_type.setText(mProductList.get(position).getW_name());
        return v;
    }
}
