package com.cunteng008.daygram.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cunteng008.daygram.R;
import com.cunteng008.daygram.constant.Constant;

import static com.cunteng008.daygram.constant.Constant.LOCK_OFF;
import static com.cunteng008.daygram.constant.Constant.LOCK_ON;
import static com.cunteng008.daygram.constant.Constant.RESET_LOCK;

public class settingActivity extends AppCompatActivity {
    //控件
    private RadioButton mRadioButtonLockOn;
    private RadioButton mRadioButtonLockOff;
    private RadioGroup mRadioGroupLock;
    private Button mButtonResetLock;
    private ImageView mSettingImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mRadioButtonLockOn = (RadioButton) findViewById(R.id.lock_on);
        mRadioButtonLockOff = (RadioButton) findViewById(R.id.lock_off);
        mRadioGroupLock = (RadioGroup) findViewById(R.id.lock_radio_group);
        mButtonResetLock = (Button) findViewById(R.id.lock_reset);
        mSettingImageView = (ImageView)findViewById(R.id.setting_imageView);

        if (daygramActivity.mMyLock.isLock()) {
            mRadioButtonLockOn.setChecked(true);
        } else {
            mRadioButtonLockOff.setChecked(true);
        }
        mRadioGroupLock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mRadioButtonLockOff.getId()) {
                    Intent mIntent = new Intent(settingActivity.this,inputPasswordActivity.class);
                    mIntent.putExtra("input_password", LOCK_OFF+"");
                    startActivity(mIntent);
                    finish();
                } else if (checkedId == mRadioButtonLockOn.getId()) {
                    Intent mIntent = new Intent(settingActivity.this,inputPasswordActivity.class);
                    mIntent.putExtra("input_password", LOCK_ON+"");
                    startActivity(mIntent);
                    finish();
                }
            }
        });

        mButtonResetLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mIntent = new Intent(settingActivity.this,inputPasswordActivity.class);
                mIntent.putExtra("input_password", RESET_LOCK+"");
                startActivity(mIntent);
                finish();
            }
        });

        mSettingImageView = (ImageView) findViewById(R.id.setting_imageView);
        mSettingImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               finish();
            }
        });
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode!= Activity.RESULT_OK)  //请求失败
            return;
        if(requestCode == 3){    //对应启动的代号3
           if(daygramActivity.mMyLock.isLock()){
               mRadioButtonLockOn.setChecked(true);
           }else {
               mRadioButtonLockOff.setChecked(true);
           }
        }
    } */
}
