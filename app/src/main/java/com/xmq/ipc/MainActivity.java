package com.xmq.ipc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xmq.account.api.IAccountApi;
import com.xmq.account.api.User;
import com.xmq.account.impl.AccountApiImpl;
import com.xmq.ipc.core.XmqIPC;
import com.xmq.ipc.process_second.SecondActivity;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "XmqIPC-Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void register(View view){
        Log.i(TAG, "register: ");
        XmqIPC.getServer(this).register(IAccountApi.class, AccountApiImpl.getInstance());
    }

    public void setUser(View view){
        Log.i(TAG, "SetUser: ");
        AccountApiImpl.getInstance().setUser("100001",
                new User("100001", "xmqyeah", 18));
    }

    public void goSecond(View view) {
        Log.i(TAG, "Second: ");
        startActivity(new Intent(this, SecondActivity.class));
    }
}