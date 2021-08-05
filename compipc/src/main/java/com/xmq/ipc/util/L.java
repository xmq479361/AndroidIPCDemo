package com.xmq.ipc.util;

import android.util.Log;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/5 20:00
 */
public class L {
    final static String TAG = "XmqIPC";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

}
