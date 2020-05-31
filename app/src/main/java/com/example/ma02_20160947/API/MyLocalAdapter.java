package com.example.ma02_20160947.API;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ma02_20160947.R;
import com.example.ma02_20160947.model.LocalDto;

import java.util.ArrayList;

public class MyLocalAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<LocalDto> list;
    private ViewHolder viewHolder = null;

    public MyLocalAdapter(Context context, int resource, ArrayList<LocalDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LocalDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(layout, parent, false);
            viewHolder.tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            viewHolder.tvAddress = (TextView)view.findViewById(R.id.tvAddress);
            viewHolder.tvPhone = (TextView) view.findViewById(R.id.tvPhone);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        LocalDto dto = list.get(position);
        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvAddress.setText(dto.getAddress());
        viewHolder.tvPhone.setText(dto.getTelephone());

        return view;
    }


    public void setList(ArrayList<LocalDto> list) {
        this.list = list;
    }

    public void clear() {
        this.list = new ArrayList<LocalDto>();
    }



    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvPhone = null;
        public TextView tvAddress = null;
    }
}
