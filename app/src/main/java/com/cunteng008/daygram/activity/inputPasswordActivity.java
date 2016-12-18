package com.cunteng008.daygram.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cunteng008.daygram.R;

import static com.cunteng008.daygram.constant.Constant.LOCK_OFF;
import static com.cunteng008.daygram.constant.Constant.LOCK_ON;
import static com.cunteng008.daygram.constant.Constant.LONG_IN;
import static com.cunteng008.daygram.constant.Constant.RESET_LOCK;

public class inputPasswordActivity extends AppCompatActivity {
    //输入密码
    private EditText mEditPassword;
    private String mInputPassword="";

    //显示的4位密码(圆点)
    private TextView mPassword1;
    private TextView mPassword2;
    private TextView mPassword3;
    private TextView mPassword4;

    //提示
    private RelativeLayout mInputHint;
    private RelativeLayout mErrorHint;
    private TextView mIputHintText;
    private TextView mErrorHintText;

    //登陆还是重置密码
    private Intent mIntent;
    private int mType;
    private String mNewPsd ;  //重置密码需要前后一致
    private int mNewPsdIputTimes = -1;  //新密码输入次数

    //private AlarmReceiver mAlarmreceiver ;
    //private IntentFilter mAlarmFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_input_password);
        mEditPassword = (EditText) findViewById(R.id.password_edit);
        mPassword1 = (TextView) findViewById(R.id.password1);
        mPassword2 = (TextView) findViewById(R.id.password2);
        mPassword3 = (TextView) findViewById(R.id.password3);
        mPassword4 = (TextView) findViewById(R.id.password4);
        mInputHint = (RelativeLayout) findViewById(R.id.iput_hint);
        mErrorHint = (RelativeLayout) findViewById(R.id.error_hint);
        mIputHintText = (TextView) findViewById(R.id.input_psd_text_view);
        mErrorHintText = (TextView) findViewById(R.id.error_input_text_view);

        //信息的索引
        mIntent = getIntent();
        mType =  Integer.parseInt(mIntent.getStringExtra("input_password"));

        //监听editText的内容，当有内容输入时，则将一个原点描黑，删除原点换另一种背景
        mEditPassword.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                if(mInputPassword.length() < s.length()){
                    setInputPassword_circles(s.length());
                }else if(mInputPassword.length()>s.length()){
                    setUnInputPassword_circles(mInputPassword.length());
                }else {
                    return;
                }
                mInputPassword = mEditPassword.getText().toString();
                Log.d("afterTextChanged","输入了"+s);
                if(mInputPassword.length()==4){
                    if(mNewPsdIputTimes==-1){  //没新密码输入
                        if(mInputPassword.equals(daygramActivity.mMySettingInfo.getPassword())){
                            if (mType == LONG_IN){
                                finish();
                            }else if(mType == LOCK_OFF){
                                daygramActivity.mMySettingInfo.setLock(false);
                                Intent mIntent = new Intent(inputPasswordActivity.this,settingActivity.class);
                                mIntent.putExtra("input_password", LOCK_OFF+"");
                                startActivity(mIntent);
                                finish();
                            }else if(mType == LOCK_ON){
                                daygramActivity.mMySettingInfo.setLock(true);
                                Intent mIntent = new Intent(inputPasswordActivity.this,settingActivity.class);
                                mIntent.putExtra("input_password", LOCK_OFF+"");
                                startActivity(mIntent);
                                finish();
                            }
                            else if(mType == RESET_LOCK && mNewPsdIputTimes == -1){
                                mNewPsdIputTimes ++;
                                mIputHintText.setText(R.string.input_new_psd);
                                InputShowTimer.start();
                            }
                        }else {
                            mInputHint.setVisibility(View.GONE);
                            mErrorHint.setVisibility(View.VISIBLE);
                            mPassword1.setBackgroundResource(R.drawable.password_circle_error);
                            mPassword2.setBackgroundResource(R.drawable.password_circle_error);
                            mPassword3.setBackgroundResource(R.drawable.password_circle_error);
                            mPassword4.setBackgroundResource(R.drawable.password_circle_error);
                            ErrorHintShowTimer.start();
                        }
                    }else if(mNewPsdIputTimes == 0 ){  //新密码输入一次
                        mNewPsdIputTimes++;
                        mNewPsd = mInputPassword;
                        mIputHintText.setText(R.string.input_new_psd_again); //确认新密码提示
                        InputShowTimer.start();
                    }else if(mNewPsdIputTimes == 1  ){  //新密码输入二次
                        if(mNewPsd.equals(mInputPassword)){
                            daygramActivity.mMySettingInfo.setPassword(mNewPsd);
                            Intent mIntent = new Intent(inputPasswordActivity.this,settingActivity.class);
                            mIntent.putExtra("input_password", LOCK_OFF+"");
                            startActivity(mIntent);
                            finish();
                        }else {
                            mInputHint.setVisibility(View.GONE);  //将提示输入的信息撤销
                            mErrorHint.setVisibility(View.VISIBLE);
                            mErrorHintText.setText(R.string.psd_diff);  //两次输入不一致
                            mPassword1.setBackgroundResource(R.drawable.password_circle_error);
                            mPassword2.setBackgroundResource(R.drawable.password_circle_error);
                            mPassword3.setBackgroundResource(R.drawable.password_circle_error);
                            mPassword4.setBackgroundResource(R.drawable.password_circle_error);
                            ErrorHintShowTimer.start();
                        }
                    }
                }
            }
            /* 原有的文本s中，从start开始的count个字符将会被一个新的长度为after的文本替换，
             * 注意这里是将被替换，还没有被替换。
             * */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged","输入前"+count+"的count长度将成为"+after);
            }
            /*说在原有的文本s中，从start开始的count个字符替换长度为before的旧文本，
            * 注意这里没有将要之类的字眼，也就是说一句执行了替换动作。
            * */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged","输入了"+s);
            }
        });

    }

    /*
    *CountDownTimer ErrorHintShowTimer = new CountDownTimer(10000, 1000)中，第一个参数表示总时间，
    * 第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后10秒之后会回
    * 调onFinish方法。
    * */
    private CountDownTimer ErrorHintShowTimer = new CountDownTimer(800,800) {
        @Override
        public void onTick(long millisUntilFinished) {
        }
        @Override
        public void onFinish() {
            ErrorHintShowTimer.cancel();
            mErrorHint.setVisibility(View.GONE);
            mInputHint.setVisibility(View.VISIBLE);
            mEditPassword.setText("");
            mInputPassword = "";
            for(int i=1;i<=4;i++){
                setUnInputPassword_circles(i);
            }
        }
    };

    private CountDownTimer InputShowTimer = new CountDownTimer(800,800) {
        @Override
        public void onTick(long millisUntilFinished) {
        }
        @Override
        public void onFinish() {
            InputShowTimer.cancel();
            mEditPassword.setText("");
            mInputPassword = "";
            for(int i=1;i<=4;i++){
                setUnInputPassword_circles(i);
            }
        }
    };

    /*
    class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(mAlarmreceiver);
            mErrorHint.setVisibility(View.GONE);
            mInputHint.setVisibility(View.VISIBLE);
            mPassword1.setBackgroundResource(R.drawable.password_circle_default);
            mPassword2.setBackgroundResource(R.drawable.password_circle_default);
            mPassword3.setBackgroundResource(R.drawable.password_circle_default);
            mPassword4.setBackgroundResource(R.drawable.password_circle_default);
            mEditPassword.setText("");
        }
    }  */

    void setInputPassword_circles(int i){
        switch (i){
            case 1:
                mPassword1.setBackgroundResource(R.drawable.password_circle_input);
                break;
            case 2:
                mPassword2.setBackgroundResource(R.drawable.password_circle_input);
                break;
            case 3:
                mPassword3.setBackgroundResource(R.drawable.password_circle_input);
                break;
            case 4:
                mPassword4.setBackgroundResource(R.drawable.password_circle_input);
                break;
        }
    }
    void setUnInputPassword_circles(int i){
        switch (i){
            case 1:
                mPassword1.setBackgroundResource(R.drawable.password_circle_default);
                break;
            case 2:
                mPassword2.setBackgroundResource(R.drawable.password_circle_default);
                break;
            case 3:
                mPassword3.setBackgroundResource(R.drawable.password_circle_default);
                break;
            case 4:
                mPassword4.setBackgroundResource(R.drawable.password_circle_default);
                break;
        }
    }
    // 捕获返回键
    @Override
    public void onBackPressed() {
        if(mType == LONG_IN){
            //关闭整个程序
            SysApplication.getInstance().exit();
        }else if(mType == LOCK_ON || mType == LOCK_OFF){
            Intent mIntent = new Intent(inputPasswordActivity.this,settingActivity.class);
            mIntent.putExtra("input_password", LOCK_OFF+"");
            startActivity(mIntent);
        }
        super.onBackPressed();
    }


/*
    Handler handler = new Handler();//创建Handler
    Runnable updateThread = new Runnable() {
        public void run() {
            handler.postDelayed(updateThread, 10);
            mInputPassword = mEditPassword.getText().toString();
            if(mInputPassword.length()==4){
                if(mInputPassword.equals(mMySettingInfo)){
                    finish();
                }else {
                    mInputHint.setVisibility(View.GONE);
                    mErrorHint.setVisibility(View.VISIBLE);
                    mEditPassword.setText("");
                }
            }
        }
    };  */
}
