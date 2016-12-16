package com.cunteng008.daygram.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cunteng008.daygram.constant.Constant;
import com.cunteng008.daygram.R;
import com.cunteng008.daygram.model.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by CMJ on 2016/9/19.
 */
public class myAdapter extends BaseAdapter {

    //Pane类的type标志
    private static final int TYPE_Pane = 1;
    //Drop类的type标志
    private static final int TYPE_BlackDrop = 0;
    private static final int TYPE_RedDrop = 2 ;
    private static final int TYPE_ViewAll = 3 ;

    private Context mcontext;
    private List<Data> mMonthData = new ArrayList<Data>();
    private boolean mIsViewAll;

    public myAdapter(Context context, ArrayList<Data> listItems,boolean isViewAll) {
        mcontext = context;
            mMonthData.addAll(listItems);
        this.mIsViewAll = isViewAll;
    }

    public void setMonthData(ArrayList<Data> listItems){
        mMonthData = listItems;
    }
    public void setIsViewAll(boolean isViewAll){
        mIsViewAll = isViewAll;
    }
    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if(mIsViewAll){
            result = TYPE_ViewAll;
        }
        else if (mMonthData.get(position).getContent().length()>0) {
            result = TYPE_Pane;
        }
        else if(mMonthData.get(position).getContent().length()==0){
            if(mMonthData.get(position).getWeek() == 0)
            result = TYPE_RedDrop;
            else
                result = TYPE_BlackDrop;
        }
        return result;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }
    @Override
    public int getCount() {
        return mMonthData.size();
    }
    @Override
    public Object getItem(int position) {
        return mMonthData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        //根据position获得View的type
        int type = getItemViewType(position);
        if(convertView==null){
            holder1 = new ViewHolder1();
            holder2 = new ViewHolder2();
            switch (type){
                case TYPE_Pane:
                    convertView = View.inflate(mcontext, R.layout.pane, null);
                holder1.Week = (TextView) convertView.findViewById(R.id.Week);
                holder1.Date = (TextView) convertView.findViewById(R.id.Date);
                holder1.Content = (TextView) convertView.findViewById(R.id.Content);
                convertView.setTag(R.id.tag_pane,holder1);
                    break;
                case TYPE_BlackDrop:
                    convertView = View.inflate(mcontext, R.layout.black_drop, null);
                    break;
                case  TYPE_RedDrop:
                    convertView = View.inflate(mcontext, R.layout.red_drop, null);
                    break;
                case TYPE_ViewAll:
                    convertView = View.inflate(mcontext,R.layout.view_all,null);
                    holder2.viewAll = (TextView) convertView.findViewById(R.id.view_all_textView);
                    convertView.setTag(R.id.tag_view_all,holder2);
                    break;
            }
        }
        else{
            switch (type) {
                case TYPE_Pane:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.tag_pane);
                    break;
                case TYPE_ViewAll:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.tag_view_all);
                    break;
            }
        }
        switch (type)
        {
            case TYPE_Pane:
                Data data = (Data) mMonthData.get(position);
                holder1.Week.setText(Constant.SHORT_WEEK[data.getWeek()]);
                holder1.Date.setText(""+data.getDate());
                holder1.Content.setText(data.getContent());
                break;
            case TYPE_ViewAll:
                Data data2 = (Data) mMonthData.get(position);

                    int whatWeek = data2.getWeek();
                    int whatYear = data2.getYear();
                    int date = data2.getDate();
                    int whatMonth = data2.getMonth();

                    String str = date+" "+Constant.WEEK[whatWeek]+ " / "+data2.getContent()+"\n";

                    SpannableStringBuilder style=new SpannableStringBuilder(str);
                    if(whatWeek == 0){
                        //找到需要改变内容的起点和终点
                        int fstart=str.indexOf(Constant.WEEK[whatWeek]);
                        int fend=fstart+Constant.WEEK[whatWeek].length();

                        style.setSpan(new ForegroundColorSpan(Color.RED),fstart,fend,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    holder2.viewAll.setText(style);
                break;
        }
        return convertView;
    }

    /**
     * item B 的Viewholder
     */
    private static class ViewHolder1 {
        TextView Week;
        TextView Date;
        TextView Content;
    }
    private  static class  ViewHolder2{
        TextView viewAll;
    }
}