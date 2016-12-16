package com.cunteng008.daygram.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cunteng008.daygram.constant.Constant;
import com.cunteng008.daygram.model.Message;
import com.cunteng008.daygram.R;

import java.util.Calendar;

public class iputTextActivity extends AppCompatActivity {

    public static final String IPUT_TEXT_RETURN_CONTENT = "com.cunteng008.daygram.iput_text_return";
    //private static f

    private Button mTimeButton;
    private Button mDoneButton;
    private TextView mTextView;
    private  EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iput_text);
        AndroidBug5497Workaround.assistActivity(this);

        mTextView = (TextView) findViewById(R.id.iputTextTitle);
        mTimeButton = (Button) findViewById(R.id.Time);
        mDoneButton = (Button) findViewById(R.id.Done);
        mEditText= (EditText) findViewById(R.id.editText);

        //mDoneButton.setVisibility(View.INVISIBLE);
        //mTimeButton.setVisibility(View.INVISIBLE);

        final Message msg = (Message) getIntent().getSerializableExtra(daygramActivity.SER_KEY);

        int whatWeek = msg.getWeek();
        int whatYear = msg.getYear();
        int date = msg.getDate();
        int whatMonth = msg.getMonth();

        String str = Constant.WEEK[whatWeek]+"/"+Constant.MONTH[whatMonth-1]+" "+date+
                "/"+whatYear;

        SpannableStringBuilder style=new SpannableStringBuilder(str);
        if(whatWeek == 0){
            //找到需要改变内容的起点和终点
            int fstart=str.indexOf(Constant.WEEK[whatWeek]);
            int fend=fstart+Constant.WEEK[whatWeek].length();

            style.setSpan(new ForegroundColorSpan(Color.RED),fstart,fend,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        mTextView.setText(style);

        mEditText.setText(msg.getContent());
        mEditText.setSelection(msg.getContent().length());
        /*
        mEditText.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v,MotionEvent event){
                mTimeButton.setVisibility(View.VISIBLE);
                mDoneButton.setVisibility(View.VISIBLE);
                return false;
            }
        });  */

        mDoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String input = mEditText.getText().toString();
                //mEditText.setText(input);
                Intent data=new Intent();  //onActivityResult的data

                Bundle mBundle = new Bundle();
                msg.setContent(input);
                mBundle.putSerializable(IPUT_TEXT_RETURN_CONTENT,msg);
                data.putExtras(mBundle);
                setResult(Activity.RESULT_OK, data);
                finish();

            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int index = mEditText.getSelectionStart();//获取光标所在位置
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (index < 0 || index >= mEditText.length()) {
                    mEditText.append(insertTime(hour,minute));
                } else {
                    //直接有时edT无法调用insert
                    Editable text = mEditText.getText();
                    text.insert(index,insertTime(hour,minute)); //光标所在位置插入文字
                }

            }
        });
    }
    private String insertTime(int hour,int minute){
        if(hour>=10&& minute>=10){
            return "("+hour+":"+minute+")";
        }else if(hour<10&&minute>=10){
            return "(0"+hour+":"+minute+")";
        }else if(hour>=10&&minute<10){
            return "("+hour+":0"+minute+")";
        }else {
            return "(0"+hour+":0"+minute+")";
        }
    }
}
