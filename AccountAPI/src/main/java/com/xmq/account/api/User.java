package com.xmq.account.api;

/**
 * @author xmqyeah
 */
public class User {
    public String mUNo;
    public String mName;
    public int mAge;

    public User(String mUNo, String mName, int mAge) {
        this.mUNo = mUNo;
        this.mName = mName;
        this.mAge = mAge;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUNo='" + mUNo + '\'' +
                ", mName='" + mName + '\'' +
                ", mAge=" + mAge +
                '}';
    }
}