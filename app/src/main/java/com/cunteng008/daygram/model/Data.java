package com.cunteng008.daygram.model;

import java.io.Serializable;

/**
 * Created by CMJ on 2016/9/20.
 */
public class Data implements Serializable {
    private int mYear;
    private int mDate;
    private int mWeek;
    private int mMonth;
    private String mContent;
    private int mContentSize;

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getDate() {
        return mDate;
    }

    public int getWeek() {
        return mWeek;
    }

    public void setWeek(int week) {
        mWeek = week;
    }

    public void setDate(int date) {
        mDate = date;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public void setContentSize(int contentSize) {
        mContentSize = contentSize;
    }

    public String getTime() {
        return mYear+"年"+mMonth+"月"+mDate+"日";
    }
}
