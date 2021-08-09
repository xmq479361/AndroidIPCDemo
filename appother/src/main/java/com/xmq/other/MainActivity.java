package com.xmq.other;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xmq.account.api.IAccountApi;
import com.xmq.account.api.User;
import com.xmq.ipc.core.XmqIPC;

public class MainActivity extends AppCompatActivity {
    final String TAG = "XmqIPC-Second";
    IAccountApi accountApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XmqIPC.getClient(this).connect( "com.xmq.ipc");
        XmqIPC.getClient(this).register(IAccountApi.class);
    }

    public void getApi(View view){
        Log.i(TAG, "getApi: ");
        accountApi = XmqIPC.getClient(this).getApi(IAccountApi.class);
    }

    public void getUser(View view){
        if (accountApi != null) {
            XmqIPC.getClient(this).connect("com.xmq.ipc");
            User user = accountApi.getUser("100001");
            Log.i(TAG, "getUser: " + user);
            Toast.makeText(this, "getUser: " + user, Toast.LENGTH_LONG).show();
        } else {
            Log.w(TAG, "getUser: Api not initial");
        }
    }
}