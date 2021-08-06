package com.xmq.ipc.core;

import com.xmq.ipc.util.InvokeUtil;
import com.xmq.ipc.util.L;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:38
 */
public class XmqInvokeHandler implements InvocationHandler {
    protected XmqInvokeHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    Class<?> clazz;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        L.i(proxy.getClass() + " invoke: " + InvokeUtil.generateMethodKey(method) + ", " + Arrays.toString(args));
        Object response = XmqIPC.xmqIPC.request(clazz, method, args, XmqServiceManager.TYPE_INVOKE);
        L.i(proxy.getClass() + " invoke response: " + response);
        return response;
    }
}
