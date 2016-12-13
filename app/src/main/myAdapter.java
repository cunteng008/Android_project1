package com.cunteng008.daygram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Created by CMJ on 2016/9/19.
 */
public class myAdapter extends BaseAdapter {

    private Context mcontext;
    private List< Object> mdata = new ArrayList<Object>();
    private int []mtype;

    public myAdapter(Context context, ArrayList<Object> listItem,int []type) {
        this.mcontext = context;
        mdata.addAll(listItem);
        this.mtype=type;
    }
    @Override
    public int getCount() {
        return mdata.size();
    }
    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1;

        if(convertView==null){
            holder1 = new ViewHolder1();
            convertView = View.inflate(mcontext, R.layout.lv_layout, null);
            holder1.ItemImage = (ImageView) convertView.findViewById(R.id.ItemImage);
            holder1.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            holder1.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
            convertView.setTag(holder1);
        }
        else{
            holder1 = (ViewHolder1) convertView.getTag();
        }

        Content content= (Content) mdata.get(position);
        holder1.ItemImage.setImageResource(content.getItemImageId());
        holder1.ItemTitle.setText(content.getItemTitle());
        holder1.ItemText.setText(content.getItemText());

        return convertView;
    }

    /**
     * item B 的Viewholder
     */
    private static class ViewHolder1 {
        TextView ItemTitle;
        TextView ItemText;
        ImageView ItemImage;
    }
}