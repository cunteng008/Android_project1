package com.cunteng008.daygram.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cunteng008.daygram.constant.Constant;
import com.cunteng008.daygram.model.Data;
import com.cunteng008.daygram.model.Message;
import com.cunteng008.daygram.R;
import com.cunteng008.daygram.adapter.myAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;


public class daygramActivity extends AppCompatActivity {
    public  final static String SER_KEY = "com.cunteng008.daygram.myser";
    public final static String VIEW_ALLL_DIARY = "com.cunteng008.daygram.view_all_diary";

    private final   static String SUFFIX_FILE_NAME = "daygram_data_save.txt";
    private String FILE_NAME;

    private ListView mlv;
    private myAdapter mmyAdapter;

    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;
    private ImageView mAddImageView;
    private ImageView mViewImageView;
    private ImageView mSettingImageView;
    private int mPresentYear;
    private int mPresentMonth;
    private int mlvPosition = 0;

    //设置开关
    private boolean mViewImageViewSwitch = false;

    //一个月的日记数据链表
    private ArrayList<Data> mDataList = new ArrayList<Data>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_daygram);

        Calendar c = Calendar.getInstance();  //日历
        mPresentYear =  c.get(Calendar.YEAR);
        mPresentMonth = c.get(Calendar.MONTH)+1;  //一月对应0，十二月对应11
        mMonthSpinner = (Spinner) findViewById(R.id.month_spinner);
        mMonthSpinner.setSelection(mPresentMonth-1,true);
        mMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Log.i("mMonthSpinner 示例",result);
                mPresentMonth = i+1;
                Toast.makeText(daygramActivity.this, Constant.MONTH[i],Toast.LENGTH_SHORT).show();
                upDateData(mPresentYear,mPresentMonth);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){

            }
        });
        mYearSpinner =(Spinner) findViewById(R.id.year_spinner);
        String[]  years_data= new String[6];
        // 生成最近6年的年份数值
        for (int j = 0; j < 6; j++) {
            years_data[j]=("" + (c.get(Calendar.YEAR) -j ));
        }
        // 适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(daygramActivity.this,
                android.R.layout.simple_spinner_item, years_data);
        // 设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器与选择列表框关联
        mYearSpinner.setAdapter(adapter);
        mYearSpinner.setSelection(0,true);
        mYearSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(daygramActivity.this, result,Toast.LENGTH_SHORT).show();

                mPresentYear =Integer.valueOf(result).intValue() ;
                upDateData(mPresentYear,mPresentMonth);
                Log.i("mYearSpinner ",result);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){

            }
        });

        //初始化数据
        init(mPresentYear,mPresentMonth);

        mAddImageView =(ImageView) findViewById(R.id.add_imageView);
        mAddImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addNew();
            }
        });

        //下面两个函数针对listview item的滚动和点击事件
        mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // i 表示第几个item响应
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(daygramActivity.this,mDataList.get(i).getTime(),Toast.LENGTH_SHORT).show();
                    SerializeMethod(i);
            }
        });
        mlv.setSelection(mlvPosition);
        mlv.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 滚动状态改变时调用
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 不滚动时保存当前滚动到的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mlvPosition = mlv.getFirstVisiblePosition();
                }
            }
            // 滚动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        //listView长按事件
        mlv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                final Data data = mDataList.get(position);
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(daygramActivity.this);
                builder.setMessage("你确定要删除"+data.getTime()+"的日记");
                builder.setTitle("提示");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.setContent("");
                        mDataList.set(position,data);
                        data.setContentSize(data.getContent().length());
                        bindAdapter();
                        Toast.makeText(getBaseContext(), "已删除", Toast.LENGTH_SHORT).show();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return false;
            }
        });


    mViewImageView = (ImageView) findViewById(R.id.view_imageView);
        mViewImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!mViewImageViewSwitch){
                    viewDiary();
                    mViewImageViewSwitch = true;
                }
                else {
                    upDateData(mPresentYear,mPresentMonth);
                    mViewImageViewSwitch = false;
                }
            }
        });

        mSettingImageView = (ImageView) findViewById(R.id.setting_imageView);
        mSettingImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mIntent = new Intent(daygramActivity.this,settingActivity.class);
                startActivityForResult(mIntent,2);  //本次操作代号为2
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode!= Activity.RESULT_OK)  //请求失败
            return;

        if(requestCode == 1){    //对应启动的代号1
            if(data==null){
                return;
            }
            Message msg = (Message) data.getSerializableExtra(iputTextActivity.IPUT_TEXT_RETURN_CONTENT);  //没问题
            if(msg == null)
                return;
            if(!mViewImageViewSwitch){
                updateData(msg);
            }
            else {
                viewDiaryUpdate(msg);
            }
        }
    }

    protected void init(int y,int m){
        int days;
        Calendar c = Calendar.getInstance();
        c.set(y,m,1);
        days = getDayNumber(y,m);

        FILE_NAME = y +""+ m + SUFFIX_FILE_NAME ;

        if(getObject(FILE_NAME)== null ){
            for(int i=0;i<days;i++)
            {
                int date=i+1;
                Date d1 = new Date(y-1900,m-1,date);
                Data data = new Data();
                data.setDate(date);
                data.setWeek(getWeek(d1));
                data.setYear(y);
                data.setMonth(m);
                data.setContent("");
                data.setContentSize(data.getContent().length());
                mDataList.add(data);
            }
        }
        else {
            mDataList = (ArrayList<Data>) getObject(FILE_NAME);
        }
        bindAdapter();
        mlv.setBackgroundColor(888888888);
    }
    protected void upDateData(int y,int m){
        saveObject(mDataList,FILE_NAME);
        int days;
        Calendar c = Calendar.getInstance();
        c.set(y,m,1);
        days = getDayNumber(y,m);

        mDataList = new ArrayList<Data>();
        FILE_NAME = y +""+ m + SUFFIX_FILE_NAME ;

        if(getObject(FILE_NAME)== null ){
            for(int i=0;i<days;i++)
            {
                int date=i+1;
                Date d1 = new Date(y-1900,m-1,date);
                Data data = new Data();
                data.setDate(date);
                data.setWeek(getWeek(d1));
                data.setYear(y);
                data.setMonth(m);
                data.setContent("");
                data.setContentSize(data.getContent().length());
                mDataList.add(i,data);
            }
        }
        else {
            mDataList = (ArrayList<Data>) getObject(FILE_NAME);
        }
        bindAdapter();
    }
    protected void updateData(Message msg) {
        Data data = mDataList.get(msg.getPos());
        data.setContent(msg.getContent());
        data.setContentSize( msg.getContent().length());
        mDataList.set(msg.getPos(),data);
        mmyAdapter.notifyDataSetChanged();
    }

    protected void SerializeMethod(int pos){
        Message msg = new Message();
        int date = pos+1;
        msg.setContent(mDataList.get(pos).getContent());
        msg.setPos(pos);
        msg.setYear(mDataList.get(pos).getYear());
        msg.setWeek(mDataList.get(pos).getWeek());
        msg.setMonth(mDataList.get(pos).getMonth());
        Intent mIntent = new Intent(daygramActivity.this,iputTextActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SER_KEY,msg);
        mIntent.putExtras(mBundle);

        startActivityForResult(mIntent,1);  //本次操作代号为1
    }
    protected void bindAdapter(){
        mmyAdapter = new myAdapter(daygramActivity.this,mDataList,false);
        mlv = (ListView) findViewById(R.id.lv);  /*定义一个动态数组*/
        mlv.setAdapter(mmyAdapter);  //为ListView绑定适配器
    }

    protected void addNew(){
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DAY_OF_MONTH);
        int pos = date - 1;

        Message msg = new Message();
        Data data = mDataList.get(pos);
        msg.setContent( data.getContent());
        msg.setPos(date-1);
        msg.setYear(mDataList.get(pos).getYear());
        msg.setWeek(mDataList.get(pos).getWeek());
        msg.setMonth(mDataList.get(pos).getMonth());

        Intent mIntent = new Intent(daygramActivity.this,iputTextActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SER_KEY,msg);
        mIntent.putExtras(mBundle);
        startActivityForResult(mIntent,1);
    }

    private void saveObject(Object o,String name){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    private Object getObject(String name){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }

    private int getDayNumber(int year, int month) {
        int days[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if (2 == month && 0 == (year % 4) && (0 != (year % 100) || 0 == (year % 400))) {
            days[1] = 29;
        }
        return (days[month - 1]);
    }

    private int getWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return week_index;
    }

    private void viewDiary(){
        mmyAdapter = new myAdapter(daygramActivity.this,mDataList,true);
        mlv.setAdapter(mmyAdapter);  //为ListView绑定适配器
    }
    private void viewDiaryUpdate(Message msg){
        Data data = mDataList.get(msg.getPos());
        data.setContent(msg.getContent());
        data.setContentSize( msg.getContent().length());
        mDataList.set(msg.getPos(),data);
        mmyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveObject(mDataList,FILE_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveObject(mDataList,FILE_NAME);
    }
}

