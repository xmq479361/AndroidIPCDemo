package com.xmq.ipc.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.xmq.compipc.IPCWorkAidlInterface;
import com.xmq.ipc.cache.XmqCacheCenter;
import com.xmq.ipc.util.L;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:21
 */
public class XmqIPC {
    public static XmqIPC xmqIPC = new XmqIPC();

    private XmqIPC(){};

    public static XmqIPC getInstance() {
        return xmqIPC;
    }

    private Context mContext;
    XmqCacheCenter cacheCenter = new XmqCacheCenter();
    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public <T> void register(Class<T> clazz) {
        cacheCenter.register(clazz);
    }
    public <T> void register(Class<T> clazz, T service) {
        cacheCenter.register(clazz, service);
    }
    public <T> T getApi(Class<T> clazz, Object... parameters) {
        return (T) Proxy.newProxyInstance(mContext.getClassLoader(), new Class[]{clazz}, new XmqInvokeHandler(clazz));
    }

    public <T> T request(Class<T> clazz, Method method, Object[] parameters, int type) {
        String request = cacheCenter.generateRequestBeanJson(clazz, method, parameters, type);
        if (ipcWorkBinder != null && !TextUtils.isEmpty(request)) {
            try {
                L.i("request: "+request);
                String response = ipcWorkBinder.invoke(request);
                L.i("response: " + response);
                return (T) cacheCenter.GSON.fromJson(response, method.getReturnType());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void open(Context context) {
        open(context, null);
    }

    public void open(Context context, String packageName) {
        init(context);
        bind(context.getApplicationContext(), packageName, XmqServiceManager.class);
    }

    public void bind(Context context, String packageName, Class<? extends XmqServiceManager> service) {
        init(context);
        Intent intent = null;
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(context, service);
        } else {
            intent = new Intent(service.getName());
            intent.setComponent(new ComponentName(packageName, service.getName()));
        }
        L.i("bind: " + ipcWorkBinder);
        context.bindService(intent, new IPCWorkConnection(), Context.BIND_AUTO_CREATE);
    }
    static IPCWorkAidlInterface ipcWorkBinder;

    static class IPCWorkConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            service.queryLocalInterface().asBinder().
            ipcWorkBinder = IPCWorkAidlInterface.Stub.asInterface(service);
            L.i("onServiceConnected: " + ipcWorkBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.i("onServiceDisconnected: " + ipcWorkBinder);
            XmqIPC.getInstance().open(xmqIPC.mContext);
        }
    }

}
