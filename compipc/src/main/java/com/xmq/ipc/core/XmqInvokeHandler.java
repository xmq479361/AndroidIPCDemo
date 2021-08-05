package com.xmq.ipc.core;

import com.xmq.ipc.cache.XmqCacheCenter;
import com.xmq.ipc.util.L;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:38
 */
public class XmqInvokeHandler implements InvocationHandler {
    protected XmqInvokeHandler(Class clazz) {
        this.clazz = clazz;
    }
    Class clazz;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        L.i(proxy.getClass()+" invoke: "+XmqCacheCenter.generateMethodKey(method) +", "+ Arrays.toString(args));
        XmqIPC.getInstance().request(clazz, method, args, XmqServiceManager.TYPE_GET);

        Object response = XmqIPC.getInstance().request(clazz, method, args, XmqServiceManager.TYPE_INVOKE);
        L.i(proxy.getClass()+" invoke response: "+ response);
        return response;
//        Class clazz = proxy.getClass();
//        if (clazz.isAssignableFrom(IAccountApi.class)) {
//            Object api = mApiInstanceMap.get(clazz.getName());
//            if (api == null) {
//                api = new AccountApiImpl();
//                mApiInstanceMap.put(clazz.getName(), api);
//            }
//            return (T) api;
//        }
//        XmqIPC.getInstance().cacheCenter.
    }
}
