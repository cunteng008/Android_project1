package com.cunteng008.daygram.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cunteng008.daygram.constant.Constant;
import com.cunteng008.daygram.model.Data;
import com.cunteng008.daygram.model.Message;
import com.cunteng008.daygram.R;
import com.cunteng008.daygram.adapter.myAdapter;
import com.cunteng008.daygram.model.Lock;
import com.cunteng008.daygram.widget.MyListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;


public class daygramActivity extends AppCompatActivity {
    //常量
    public  final static String SER_KEY = "com.cunteng008.daygram.myser";
    private final static String SUFFIX_FILE_NAME = "daygram_data_save.txt";
    private final static String PASSWORD_FILE_NAME = "com.cunteng008.daygram.psd";

    //变化文件名
    private String FILE_NAME;  //用来放每个月的日记的文件名

    //显示日记列表
    private MyListView mlv;
    private myAdapter mmyAdapter;
    private int mSelectYear;  //选择的年月
    private int mSelectMonth;
    private int mPresentYear;  //当前年月
    private int mPresentMonth;
    public static int mDateOfToday;
    private int mlvPosition = 0;
    private ArrayList<Data> mMonthData = new ArrayList<Data>(); //一个月的日记数据链表
    private ArrayList<Data> mTempleData = new ArrayList<Data>(); //临时放置日记

    //布局控件
    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;
    private ImageView mAddImageView;
    private ImageView mViewImageView;
    private ImageView mSettingImageView;

    //设置查看日记开关
    private boolean mViewImageViewSwitch = false;

    //密码
    public static Lock mMyLock = new Lock(false,"1234");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_daygram);
        SysApplication.getInstance().addActivity(this);  // 将该activity添加到list中去。

        if(getObject(PASSWORD_FILE_NAME) != null){
            mMyLock = (Lock) getObject(PASSWORD_FILE_NAME);
            if(mMyLock.isLock()){
                Intent intent = new Intent(daygramActivity.this,inputPasswordActivity.class);
                intent.putExtra("input_password", Constant.LONG_IN+"");
                startActivity(intent);
            }
        }

        Calendar c = Calendar.getInstance();  //日历

        mPresentYear = mSelectYear =  c.get(Calendar.YEAR);
        mPresentMonth = mSelectMonth = c.get(Calendar.MONTH)+1;  //一月对应0，十二月对应11
        mMonthSpinner = (Spinner) findViewById(R.id.month_spinner);
        mMonthSpinner.setSelection(mSelectMonth -1,true);
        mMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Log.i("mMonthSpinner 示例",result);
                mSelectMonth = i+1;
                Toast.makeText(daygramActivity.this, Constant.MONTH[i],Toast.LENGTH_SHORT).show();
                upDateData(mSelectYear, mSelectMonth);
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

                mSelectYear =Integer.valueOf(result).intValue() ;
                upDateData(mSelectYear, mSelectMonth);
                Log.i("mYearSpinner ",result);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){

            }
        });

        //初始化数据
        init(mSelectYear, mSelectMonth);

        mAddImageView =(ImageView) findViewById(R.id.add_imageView);
        mAddImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mViewImageViewSwitch){
                    Toast.makeText(daygramActivity.this,"阅览未结束，不能创建新日记",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPresentMonth != mSelectMonth || mPresentYear!=mSelectYear){
                    mSelectMonth = mPresentMonth;
                    mSelectYear = mPresentYear;
                    mYearSpinner.setSelection(0,true);
                    mMonthSpinner.setSelection(mSelectMonth -1,true);
                    upDateData(mSelectYear, mSelectMonth);
                }
                addNew();
            }
        });

        //利用刷新的方法来显示listview下拉超出时利用headview显示时间
        mlv.setOnrefreshListener(new MyListView.OnrefreshListener() {
            @Override
            public void refresh() {
                new AsyncTask<Void,Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        return null;
                    }
                    protected void onPostExecute(Void result) {
                        mmyAdapter.notifyDataSetChanged();
                        mlv.onRefreshComplete();
                    };
                }.execute();
            }
        });
        //下面两个函数针对listview item的滚动和点击事件
        mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // i 表示第几个item响应
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mViewImageViewSwitch){
                    int index = mTempleData.get(i-1).getDate()-1;
                    Toast.makeText(daygramActivity.this, mMonthData.get(index).getTime(),Toast.LENGTH_SHORT).show();
                    SerializeMethod(i-1);
                }else {
                    Toast.makeText(daygramActivity.this, mMonthData.get(i-1).getTime(),Toast.LENGTH_SHORT).show();
                    SerializeMethod(i-1);
                }
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
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(daygramActivity.this);
                builder.setMessage("你确定要删除"+ mMonthData.get(position-1).getTime()+"的日记");
                builder.setTitle("提示");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMonthData.get(position-1).setContent("");
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
                Log.d("viewall","阅览");
                if(!mViewImageViewSwitch){
                    Toast.makeText(daygramActivity.this,"阅览日记",Toast.LENGTH_SHORT).show();
                    mTempleData.clear();
                    for(Data data: mMonthData){
                        if(data.getContent().length()>0){
                            mTempleData.add(data);
                        }
                    }
                    mmyAdapter.setIsViewAll(true);
                    mmyAdapter.setMonthData(mTempleData);
                    mmyAdapter.notifyDataSetChanged();
                    mViewImageViewSwitch = true;
                } else {
                    Toast.makeText(daygramActivity.this,"关闭阅览",Toast.LENGTH_SHORT).show();
                    mmyAdapter.setIsViewAll(false);
                    mmyAdapter.setMonthData(mMonthData);
                    mmyAdapter.notifyDataSetChanged();
                    mViewImageViewSwitch = false;
                }
            }
        });

        mSettingImageView = (ImageView) findViewById(R.id.setting_imageView);
        mSettingImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(daygramActivity.this, "设置", Toast.LENGTH_SHORT).show();
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
            if(mViewImageViewSwitch){
                mTempleData.get(msg.getPos()).setContent(msg.getContent());
                int index = mTempleData.get(msg.getPos()).getDate()-1;
                mMonthData.get(index).setContent(msg.getContent());
                if(msg.getContent().length()==0){
                    mTempleData.remove(msg.getPos());
                }
                mmyAdapter.notifyDataSetChanged();
            } else {
                mMonthData.get(msg.getPos()).setContent(msg.getContent());
                mmyAdapter.notifyDataSetChanged();
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
                mMonthData.add(data);
            }
        } else {
            mMonthData = (ArrayList<Data>) getObject(FILE_NAME);
        }
        bindAdapter();
    }

    //加载另一个月的新数据
    protected void upDateData(int y,int m){
        saveObject(mMonthData,FILE_NAME);
        int days;
        Calendar c = Calendar.getInstance();
        c.set(y,m,1);
        days = getDayNumber(y,m);

        mMonthData = new ArrayList<Data>();
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
                mMonthData.add(i,data);
            }
        }
        else {
            mMonthData = (ArrayList<Data>) getObject(FILE_NAME);
        }
        if(mViewImageViewSwitch){
            mTempleData.clear();
            for(Data data: mMonthData){
                if(data.getContent().length()>0){
                    mTempleData.add(data);
                }
            }
            mmyAdapter.notifyDataSetChanged();
        }else{
            //数据改变，需要重新设置数据
            mmyAdapter.setMonthData(mMonthData);
            mmyAdapter.notifyDataSetChanged();
        }
    }
    /*
    protected void updateData(Message msg) {
        Data data = mMonthData.get(msg.getPos());
        data.setContent(msg.getContent());
        mMonthData.set(msg.getPos(),data);
        mmyAdapter.notifyDataSetChanged();
    } */

    protected void SerializeMethod(int pos){
        Message msg = new Message();
        int date;
        if(mViewImageViewSwitch){
            date = mTempleData.get(pos).getDate();
        }else {
            date  = pos+1;
        }
        msg.setPos(pos);  //pos为listview项的实际点击位置
        pos = date -1;  //相对mMonthData的位置
        msg.setContent(mMonthData.get(pos).getContent());
        msg.setYear(mMonthData.get(pos).getYear());
        msg.setWeek(mMonthData.get(pos).getWeek());
        msg.setMonth(mMonthData.get(pos).getMonth());
        msg.setDate(mMonthData.get(pos).getDate());
        Intent mIntent = new Intent(daygramActivity.this,iputTextActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SER_KEY,msg);
        mIntent.putExtras(mBundle);

        startActivityForResult(mIntent,1);  //本次操作代号为1
    }
    protected void bindAdapter(){
        mmyAdapter = new myAdapter(daygramActivity.this, mMonthData,false);
        mlv = (MyListView) findViewById(R.id.lv);  /*定义一个动态数组*/
        mlv.setAdapter(mmyAdapter);  //为ListView绑定适配器
    }

    protected void addNew(){
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DAY_OF_MONTH);
        int pos = date - 1;
        Message msg = new Message();
        Data data = mMonthData.get(pos);
        msg.setContent( data.getContent());
        msg.setPos(date-1);
        msg.setYear(mMonthData.get(pos).getYear());
        msg.setWeek(mMonthData.get(pos).getWeek());
        msg.setMonth(mMonthData.get(pos).getMonth());
        msg.setDate(date);

        Intent mIntent = new Intent(daygramActivity.this,iputTextActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SER_KEY,msg);
        mIntent.putExtras(mBundle);
        startActivityForResult(mIntent,1);
    }

    //存取文件
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

    //根据年月得到该月的天数
    private int getDayNumber(int year, int month) {
        int days[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if (2 == month && 0 == (year % 4) && (0 != (year % 100) || 0 == (year % 400))) {
            days[1] = 29;
        }
        return (days[month - 1]);
    }

    //根据日期得到该日在一个周的第几天
    private int getWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return week_index;
    }


    private void viewDiaryUpdate(Message msg){
        Data data = mMonthData.get(msg.getPos());
        data.setContent(msg.getContent());
        mMonthData.set(msg.getPos(),data);
        mmyAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveObject(mMonthData,FILE_NAME);
        saveObject(mMyLock,PASSWORD_FILE_NAME);
    }
}

