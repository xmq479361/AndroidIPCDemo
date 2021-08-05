package com.xmq.ipc.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.xmq.compipc.IPCWorkAidlInterface;
import com.xmq.ipc.bean.RequestBean;
import com.xmq.ipc.cache.XmqCacheCenter;
import com.xmq.ipc.util.L;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 21:02
 */
public class XmqServiceManager extends Service {

    public final static int TYPE_INVOKE = 2;
    public final static int TYPE_GET = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IPCWorkAidlInterface.Stub() {
            @Override
            public String invoke(String request) throws RemoteException {
                L.d("try method invoke: " + request);
                RequestBean requestBean = new Gson().fromJson(request, RequestBean.class);
                if (requestBean == null) {
                    return null;
                }
                XmqCacheCenter cacheCenter = XmqIPC.getInstance().cacheCenter;
                switch (requestBean.type) {
                    case TYPE_GET:
                    case TYPE_INVOKE:
                        Method method = cacheCenter.getMethod(requestBean);
                        if (null == method) {
                            return null;
                        }
                        try {
                            Object object = cacheCenter.getObject(requestBean.clzName);
                            L.d(">>method invoke object: " + object);
                            if (null == object) {
                                return null;
                            }
                            Object[] args = cacheCenter.makeParameterObject(requestBean);
                            L.d(">>method invoke args: " + cacheCenter.GSON.toJson(args));
                            Object response = method.invoke(object, args);
                            L.d("method invoke response: " + response);
                            return cacheCenter.GSON.toJson(response);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return "";
            }
        };
    }
}