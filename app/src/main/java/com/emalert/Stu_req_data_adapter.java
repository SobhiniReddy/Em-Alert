package com.emalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sobhini on 3/6/2017.
 */
public class Stu_req_data_adapter extends BaseAdapter {
    Context context;

    ArrayList<Stu_req_data> datalist;

    public Stu_req_data_adapter(Context context, ArrayList<Stu_req_data> list) {

        this.context = context;
        datalist = list;
    }

    @Override
    public int getCount() {

        return datalist.size();
    }

    @Override
    public Object getItem(int position) {

        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        Stu_req_data datafetcher = datalist.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview, null);

        }

        TextView msg = (TextView) convertView.findViewById(R.id.dataMsg);
        msg.setText(datafetcher.getMsg());
        TextView user = (TextView) convertView.findViewById(R.id.dataUser);
        user.setText(datafetcher.getUser());

        return convertView;
    }
}
