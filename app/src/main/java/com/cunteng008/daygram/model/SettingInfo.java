package com.cunteng008.daygram.model;

import java.io.Serializable;

/**
 * Created by CMJ on 2016/12/15.
 */

public class SettingInfo implements Serializable {
    //是否有锁，默认没有
    private boolean mLock = false;
    private String  mPassword = "1234";

    public boolean isLock() {
        return mLock;
    }

    public void setLock(boolean lock) {
        mLock = lock;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
