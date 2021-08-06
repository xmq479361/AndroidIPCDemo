package com.xmq.ipc.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.xmq.compipc.IPCWorkAidlInterface;
import com.xmq.ipc.api.ClassId;
import com.xmq.ipc.bean.RequestBean;
import com.xmq.ipc.util.InvokeUtil;
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
            public String invoke(String request) {
                L.d("try method invoke: " + request);
                RequestBean requestBean = new Gson().fromJson(request, RequestBean.class);
                if (requestBean == null) {
                    return null;
                }
                XmqCacheCenter cacheCenter = XmqIPC.xmqIPC.cacheCenter;
                try {
                    switch (requestBean.type) {
                        case TYPE_GET:
                            if (cacheCenter.getObject(requestBean.clzName) != null) {
                                return null;
                            }
                            Class<?> classType = InvokeUtil.getClassType(requestBean.clzName);
                            ClassId annotation = classType.getAnnotation(ClassId.class);
                            if (annotation != null) {
                                Object[] args = InvokeUtil.makeParameterObject(requestBean);
                                Class<?> interfaceClz = InvokeUtil.getClassType(annotation.value());
                                Method method = InvokeUtil.generateInstanceMethod(interfaceClz, args);
                                Object apiInstance = method.invoke(null, args);
                                cacheCenter.register(classType, apiInstance);
                            }
                            break;
                        case TYPE_INVOKE:
                            Method method = cacheCenter.getMethod(requestBean);
                            return invokeRequestMethod(cacheCenter, requestBean, method);
                        default:
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return "";
            }

        };
    }

    private String invokeRequestMethod(XmqCacheCenter cacheCenter, RequestBean requestBean, Method method) throws InvocationTargetException, IllegalAccessException {
        if (null == method) {
            return null;
        }
        Object object = cacheCenter.getObject(requestBean.clzName);
        L.d(">>method invoke object: " + object);
        if (null == object) {
            return null;
        }
        Object[] args = InvokeUtil.makeParameterObject(requestBean);
        L.d(">>method invoke args: " + cacheCenter.GSON.toJson(args));
        Object response = method.invoke(object, args);
        L.d("method invoke response: " + response);
        return cacheCenter.GSON.toJson(response);
    }
}