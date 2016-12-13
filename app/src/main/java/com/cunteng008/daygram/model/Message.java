package com.cunteng008.daygram.model;

import java.io.Serializable;

/**
 * Created by CMJ on 2016/9/23.
 */
public class Message implements Serializable {
    private String mContent;
    private int mPos;
    private int mWeek;
    private int mYear;
    private int mMonth;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getPos() {
        return mPos;
    }

    public void setPos(int pos) {
        mPos = pos;
    }

    public int getWeek() {
        return mWeek;
    }

    public void setWeek(int week) {
        mWeek = week;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }
}
