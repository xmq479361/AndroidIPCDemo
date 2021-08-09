package com.xmq.ipc.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.xmq.compipc.IPCWorkAidlInterface;
import com.xmq.ipc.api.IXmqIPCClient;
import com.xmq.ipc.api.IXmqIPCServer;
import com.xmq.ipc.util.InvokeUtil;
import com.xmq.ipc.util.L;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:21
 */
public class XmqIPC implements IXmqIPCServer, IXmqIPCClient {
    final static XmqIPC xmqIPC = new XmqIPC();

    private XmqIPC(){}

    private Context mContext;
    XmqCacheCenter cacheCenter = new XmqCacheCenter();
    /*************************************************************************************
     *                           Code for IXmqIPCServer
     *************************************************************************************/
    public static IXmqIPCServer getServer(Context context) {
        if (xmqIPC.mContext == null) {
            xmqIPC.mContext = context.getApplicationContext();
        }
        return xmqIPC;
    }
    @Override
    public <T> void register(Class<T> clazz, T service) {
        cacheCenter.register(clazz, service);
    }

    /*************************************************************************************
     *                           Code for IXmqIPCClient
     *************************************************************************************/

    public static IXmqIPCClient getClient(Context context) {
        if (xmqIPC.mContext == null) {
            xmqIPC.mContext = context.getApplicationContext();
        }
        return xmqIPC;
    }

    @Override
    public <T> void register(Class<T> clazz) {
        cacheCenter.register(clazz);
    }

    @Override
    public <T> T getApi(Class<T> clazz, Object... parameters) {
        String initialRequest = InvokeUtil.generateRequestBeanJson(clazz, "getInstance", parameters, XmqServiceManager.TYPE_GET);
        if (initialRequest != null) {
            request(initialRequest, Void.class);
        }
        Object apiImpl = cacheCenter.getObject(clazz.getName());
        if (apiImpl == null) {
            apiImpl = Proxy.newProxyInstance(mContext.getClassLoader(), new Class[]{clazz}, new XmqInvokeHandler(clazz));
            cacheCenter.register(clazz, apiImpl);
        }
        return (T) apiImpl;
    }

    @Override
    public void connect() {
        connect(null);
    }

    @Override
    public void connect(String packageName) {
        bind(packageName, XmqServiceManager.class);
    }

    void bind(String packageName, Class<XmqServiceManager> service) {
        Intent intent;
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(mContext, service);
        } else {
            intent = new Intent(service.getName());
            intent.setComponent(new ComponentName(packageName, service.getName()));
        }
        L.i("bind: " + ipcWorkBinder);
        mContext.bindService(intent, new IPCWorkConnection(), Context.BIND_AUTO_CREATE);
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
            XmqIPC.getClient(xmqIPC.mContext).connect();
        }
    }

     <T> Object request(Class<T> clazz, Method method, Object[] parameters, int type) {
        String requestBeanJson = InvokeUtil.generateRequestBeanJson(clazz, method, parameters, type);
        return request(requestBeanJson, method.getReturnType());
    }
    static <T> Object request(String request, Class<T> returnClazz) {
        if (ipcWorkBinder != null && !TextUtils.isEmpty(request)) {
            try {
                L.i("request: "+request);
                String response = ipcWorkBinder.invoke(request);
                L.i("response: " + response);
                return (T) xmqIPC.cacheCenter.GSON.fromJson(response, returnClazz);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
