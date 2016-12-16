package com.cunteng008.daygram.activity;

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
                    daygramActivity.mMyLock.setLock(false);
                    Toast.makeText(settingActivity.this, "lock off", Toast.LENGTH_SHORT).show();
                } else if (checkedId == mRadioButtonLockOn.getId()) {
                    daygramActivity.mMyLock.setLock(true);
                    Toast.makeText(settingActivity.this, "lock on", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonResetLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mIntent = new Intent(settingActivity.this,inputPasswordActivity.class);
                mIntent.putExtra("input_password", Constant.RESET_LOCK+"");
                startActivity(mIntent);
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
}
