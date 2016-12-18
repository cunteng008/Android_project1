package com.cunteng008.daygram.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cunteng008.daygram.constant.Constant;
import com.cunteng008.daygram.model.Data;
import com.cunteng008.daygram.model.Message;
import com.cunteng008.daygram.R;
import com.cunteng008.daygram.adapter.myAdapter;
import com.cunteng008.daygram.model.SettingInfo;
import com.cunteng008.daygram.widget.MyListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import static com.cunteng008.daygram.constant.Constant.MONTH;


public class daygramActivity extends AppCompatActivity {
    /*
    * 有关常量
    * */
    public  final static String SER_KEY = "com.cunteng008.daygram.myser";
    private final static String SUFFIX_FILE_NAME = "daygram_data_save.txt";
    private final static String PASSWORD_FILE_NAME = "com.cunteng008.daygram.psd";


    /*
    *  日记
    * */
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
    private String FILE_NAME;  //用来放每个月的日记的文件名


    /*
    * 布局控件
    * */
    //底部控件:功能选项
    private LinearLayout mFunctionSelection;
    private TextView mTextMonth;
    private TextView mTextYear;
    private ImageView mAddImageView;
    private ImageView mViewImageView;
    private ImageView mSettingImageView;
    private RelativeLayout mRelativeLayoutAdd;
    //底部控件:月选项
    private HorizontalScrollView mScrollViewMonth;
    private RadioGroup mRadioGroupMonth;
    private RadioButton mRadioButtonJAN;
    private RadioButton mRadioButtonFEB;
    private RadioButton mRadioButtonMAR;
    private RadioButton mRadioButtonAPR;
    private RadioButton mRadioButtonMAY;
    private RadioButton mRadioButtonJUN;
    private RadioButton mRadioButtonJUL;
    private RadioButton mRadioButtonAUG;
    private RadioButton mRadioButtonSEP;
    private RadioButton mRadioButtonOCT;
    private RadioButton mRadioButtonNOV;
    private RadioButton mRadioButtonDEC;
    //底部控件:年选项
    private LinearLayout mLinearLayoutYear;
    private TextView mTextYear1;
    private TextView mTextYear2;
    private TextView mTextYear3;
    private TextView mTextYear4;
    private TextView mTextYear5;
    private TextView mTextYear6;


    //设置查看日记开关
    private boolean mViewImageViewSwitch = false;
    //设置信息
    public static SettingInfo mMySettingInfo = new SettingInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_daygram);
        SysApplication.getInstance().addActivity(this);  // 将该activity添加到list中去。

        /*
        * 控件资源
        * */
        //底部控件:功能选项
        mFunctionSelection = (LinearLayout) findViewById(R.id.function_selection);
        mTextMonth = (TextView) findViewById(R.id.text_month);
        mTextYear =(TextView) findViewById(R.id.text_year);
        //mAddImageView =(ImageView) findViewById(R.id.add_imageView);
        mRelativeLayoutAdd = (RelativeLayout) findViewById(R.id.relativeLayout_add);
        mViewImageView = (ImageView) findViewById(R.id.view_imageView);
        mSettingImageView = (ImageView) findViewById(R.id.setting_imageView);
        mSettingImageView = (ImageView)findViewById(R.id.setting_imageView);
        //底部控件:月选项
        mScrollViewMonth = (HorizontalScrollView ) findViewById(R.id.scrollView_month);
        mRadioGroupMonth = (RadioGroup) findViewById(R.id.radioGroup_month);
        mRadioButtonJAN = (RadioButton) findViewById(R.id.radioButton_JAN);
        mRadioButtonFEB = (RadioButton) findViewById(R.id.radioButton_FEB);
        mRadioButtonMAR = (RadioButton) findViewById(R.id.radioButton_MAR);
        mRadioButtonAPR = (RadioButton) findViewById(R.id.radioButton_APR);
        mRadioButtonMAY = (RadioButton) findViewById(R.id.radioButton_MAY);
        mRadioButtonJUN = (RadioButton) findViewById(R.id.radioButton_JUN);
        mRadioButtonJUL = (RadioButton) findViewById(R.id.radioButton_JUL);
        mRadioButtonAUG = (RadioButton) findViewById(R.id.radioButton_AUG);
        mRadioButtonSEP = (RadioButton) findViewById(R.id.radioButton_SEP);
        mRadioButtonOCT = (RadioButton) findViewById(R.id.radioButton_OCT);
        mRadioButtonNOV = (RadioButton) findViewById(R.id.radioButton_NOV);
        mRadioButtonDEC = (RadioButton) findViewById(R.id.radioButton_DEC);
        //底部控件:年选项
        mLinearLayoutYear = (LinearLayout) findViewById(R.id.linearLayout_year);
        mTextYear1 = (TextView) findViewById(R.id.text_year1);
        mTextYear2 = (TextView) findViewById(R.id.text_year2);
        mTextYear3 = (TextView) findViewById(R.id.text_year3);
        mTextYear4 = (TextView) findViewById(R.id.text_year4);
        mTextYear5 = (TextView) findViewById(R.id.text_year5);
        mTextYear6 = (TextView) findViewById(R.id.text_year6);


        /*
        * 检测是否加锁，则需输入密码
        * */
        if(getObject(PASSWORD_FILE_NAME) != null){
            mMySettingInfo = (SettingInfo) getObject(PASSWORD_FILE_NAME);
            if(mMySettingInfo.isLock()){
                Intent intent = new Intent(daygramActivity.this,inputPasswordActivity.class);
                intent.putExtra("input_password", Constant.LONG_IN+"");
                startActivity(intent);
            }
        }

        /**
         * 月选项和月按钮
         */
        Calendar c = Calendar.getInstance();  //日历
        mPresentYear = mSelectYear =  c.get(Calendar.YEAR);
        mPresentMonth = mSelectMonth = c.get(Calendar.MONTH)+1;  //一月对应0，十二月对应11
        selectRadioGroupMonth(mSelectMonth);
        mRadioGroupMonth.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
             if(checkedId == mRadioButtonJAN.getId()){
                 mSelectMonth = 1;
             }else if(checkedId == mRadioButtonFEB.getId()){
                 mSelectMonth = 2;
             }else if(checkedId == mRadioButtonMAR.getId()){
                 mSelectMonth = 3;
             }else if(checkedId == mRadioButtonAPR.getId()){
                 mSelectMonth = 4;
             }else if(checkedId == mRadioButtonMAY.getId()){
                 mSelectMonth = 5;
             }else if(checkedId == mRadioButtonJUN.getId()){
                 mSelectMonth = 6;
             }else if(checkedId == mRadioButtonJAN.getId()){
                 mSelectMonth = 7;
             }else if(checkedId == mRadioButtonAUG.getId()){
                 mSelectMonth = 8;
             }else if(checkedId == mRadioButtonSEP.getId()){
                 mSelectMonth = 9;
             }else if(checkedId == mRadioButtonOCT.getId()){
                 mSelectMonth = 10;
             }else if(checkedId == mRadioButtonNOV.getId()){
                 mSelectMonth = 11;
             }else if(checkedId == mRadioButtonDEC.getId()){
                 mSelectMonth = 12;
             }
            mScrollViewMonth.setVisibility(View.GONE);
            mFunctionSelection.setVisibility(View.VISIBLE);
            mTextMonth.setText(MONTH[mSelectMonth-1]);
            upDateData(mSelectYear, mSelectMonth);
            }
        });
        mTextMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               mScrollViewMonth.setVisibility(View.VISIBLE);
               mFunctionSelection.setVisibility(View.GONE);
            }
        });
        /*
        mTextMonth.setSelection(mSelectMonth -1,true);
        mTextMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Log.i("mTextMonth 示例",result);
                mSelectMonth = i+1;
                Toast.makeText(daygramActivity.this, Constant.MONTH[i],Toast.LENGTH_SHORT).show();
                upDateData(mSelectYear, mSelectMonth);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){

            }
        }); */


        /*
        * 年选项
        * */
        mTextYear6.setText((mPresentYear - 0)+"");
        mTextYear6.setTextColor(getResources().getColor(R.color.select_year));
        mTextYear5.setText((mPresentYear - 1)+"");
        mTextYear4.setText((mPresentYear - 2)+"");
        mTextYear3.setText((mPresentYear - 3)+"");
        mTextYear2.setText((mPresentYear - 4)+"");
        mTextYear1.setText((mPresentYear - 5)+"");
        mTextYear1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectYear(mPresentYear - 5,5);
                mLinearLayoutYear.setVisibility(View.GONE);
                mFunctionSelection.setVisibility(View.VISIBLE);
            }
        });
        mTextYear2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectYear(mPresentYear - 4,4);
                mLinearLayoutYear.setVisibility(View.GONE);
                mFunctionSelection.setVisibility(View.VISIBLE);
            }
        });
        mTextYear3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectYear(mPresentYear - 3,3);
                mLinearLayoutYear.setVisibility(View.GONE);
                mFunctionSelection.setVisibility(View.VISIBLE);
            }
        });
        mTextYear4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectYear(mPresentYear - 2,2);
                mLinearLayoutYear.setVisibility(View.GONE);
                mFunctionSelection.setVisibility(View.VISIBLE);
            }
        });
        mTextYear5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectYear(mPresentYear - 1,1);
                mLinearLayoutYear.setVisibility(View.GONE);
                mFunctionSelection.setVisibility(View.VISIBLE);
            }
        });
        mTextYear6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectYear(mPresentYear - 0,0);
                mLinearLayoutYear.setVisibility(View.GONE);
                mFunctionSelection.setVisibility(View.VISIBLE);
            }
        });
        mTextYear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mLinearLayoutYear.setVisibility(View.VISIBLE);
                mFunctionSelection.setVisibility(View.GONE);
            }
        });

        /*
         // 生成最近6年的年份数值
        int[] mYearsData = new int[6];  //年选项数据数据
        for (int j = 0; j < 6; j++) {
            mYearsData[j]=(c.get(Calendar.YEAR) -j );
        // 适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(daygramActivity.this,
                android.R.layout.simple_spinner_item, mYearsData);
        // 设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器与选择列表框关联
        mTextYear.setAdapter(adapter);
        mTextYear.setSelection(0,true);
        mTextYear.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(daygramActivity.this, result,Toast.LENGTH_SHORT).show();

                mSelectYear =Integer.valueOf(result).intValue() ;
                upDateData(mSelectYear, mSelectMonth);
                Log.i("mTextYear ",result);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){

            }
        }); */


        /*
        *  初始化数据
        * */
        init(mSelectYear, mSelectMonth);


        /*
        * 创建或编辑当天日记
        * */
        mRelativeLayoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewImageViewSwitch){
                    Toast.makeText(daygramActivity.this,"阅览未结束，不能创建新日记",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPresentMonth != mSelectMonth || mPresentYear!= mSelectYear){
                    mSelectMonth = mPresentMonth;
                    mSelectYear = mPresentYear;
                    mTextYear.setText("2016");
                    selectRadioGroupMonth(mPresentMonth);
                    upDateData(mSelectYear, mSelectMonth);
                }
                addNew();
            }
        });
        /*
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
                    mTextYear.setText("2016");
                    selectRadioGroupMonth(mPresentMonth);
                    upDateData(mSelectYear, mSelectMonth);
                }
                addNew();
            }
        }); */


        /*
        * 日记listview设置
        * */
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


        /*
        * 阅览日记
        * */
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


        /*
        * 设置
        * */
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

    /*
    * 初始化数据
    * */
    //初始化日记
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
   //初始化月份选项
   private void selectRadioGroupMonth(int month){
        switch (month){
            case 1: mRadioButtonJAN.setChecked(true);
                break;
            case 2: mRadioButtonFEB.setChecked(true);
                break;
            case 3: mRadioButtonMAR.setChecked(true);
                break;
            case 4: mRadioButtonAPR.setChecked(true);
                break;
            case 5: mRadioButtonMAY.setChecked(true);
                break;
            case 6: mRadioButtonJUN.setChecked(true);
                break;
            case 7: mRadioButtonJUL.setChecked(true);
                break;
            case 8: mRadioButtonAUG.setChecked(true);
                break;
            case 9: mRadioButtonSEP.setChecked(true);
                break;
            case 10: mRadioButtonOCT.setChecked(true);
                break;
            case 11: mRadioButtonNOV.setChecked(true);
                break;
            case 12: mRadioButtonDEC.setChecked(true);
                break;
        }
       mTextMonth.setText(MONTH[month-1]+"");
   }
    private void selectYear(int year,int index){
        notSelectYear(mPresentYear - mSelectYear);  //先让之前选的失效
        mTextYear.setText(""+year);  //改变功能选项的月内容
        mSelectYear = year;
        upDateData(mSelectYear,mSelectMonth);
        switch (index){
            case 0:
                mTextYear6.setTextColor(getResources().getColor(R.color.select_year));
                break;
            case 1:
                mTextYear5.setTextColor(getResources().getColor(R.color.select_year));
                break;
            case 2:
                mTextYear4.setTextColor(getResources().getColor(R.color.select_year));
                break;
            case 3:
                mTextYear3.setTextColor(getResources().getColor(R.color.select_year));
                break;
            case 4:
                mTextYear2.setTextColor(getResources().getColor(R.color.select_year));
                break;
            case 5:
                mTextYear1.setTextColor(getResources().getColor(R.color.select_year));
                break;
        }
    }
    private void notSelectYear(int index){
        switch (index){
            case 0:
                mTextYear6.setTextColor(getResources().getColor(R.color.not_select_year));
                break;
            case 1:
                mTextYear5.setTextColor(getResources().getColor(R.color.not_select_year));
                break;
            case 2:
                mTextYear4.setTextColor(getResources().getColor(R.color.not_select_year));
                break;
            case 3:
                mTextYear3.setTextColor(getResources().getColor(R.color.not_select_year));
                break;
            case 4:
                mTextYear2.setTextColor(getResources().getColor(R.color.not_select_year));
                break;
            case 5:
                mTextYear1.setTextColor(getResources().getColor(R.color.not_select_year));
                break;
        }
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
        saveObject(mMySettingInfo,PASSWORD_FILE_NAME);
    }
}

