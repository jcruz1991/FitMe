package com.fitme.fitme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitme.fitme.R;
import com.fitme.fitme.model.Workout;

import java.util.List;

public class ListWorkOutAdapter extends BaseAdapter {

    private Context mContext;
    private List<Workout> myWList;
    public ListWorkOutAdapter(Context mContext, List<Workout> myWList) {
        this.mContext = mContext;
        this.myWList = myWList;
    }

    @Override
    public int getCount() {
        return myWList.size();
    }

    @Override
    public Object getItem(int position) {
        return myWList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myWList.get(position).getId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.workout_item,null);
        TextView tvExName = (TextView)v.findViewById(R.id.tvExName);
        TextView tvExDesc = (TextView)v.findViewById(R.id.tvExDesc);
        tvExName.setText(myWList.get(position).getE_name());
        tvExDesc.setText(myWList.get(position).getE_desc());
        return v;
    }
}
