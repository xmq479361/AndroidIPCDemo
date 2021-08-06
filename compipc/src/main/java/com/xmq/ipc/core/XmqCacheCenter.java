package com.xmq.ipc.core;

import android.util.Log;

import com.google.gson.Gson;
import com.xmq.ipc.bean.RequestBean;
import com.xmq.ipc.bean.RequestParameter;
import com.xmq.ipc.util.InvokeUtil;
import com.xmq.ipc.util.L;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:50
 */
public class XmqCacheCenter {
    private ConcurrentHashMap<String, Class<?>> mApiClazzMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Method>> mApiMethodMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> mApiInstanceMap = new ConcurrentHashMap<>();
    public Gson GSON = new Gson();

    public <T> void register(Class<T> clazz, Object instance) {
        String name = clazz.getName();
        register(clazz);
        L.d("register Instance: " + name + " :: " + instance);
        if (instance != null) {
            mApiInstanceMap.put(name, instance);
        }
    }

    public <T> void register(Class<T> clazz) {
        String name = clazz.getName();
        mApiClazzMap.put(name, clazz);
        registerMethodMap(clazz);
    }

    private void registerMethodMap(Class<?> clazz) {
        String name = clazz.getName();
        ConcurrentHashMap<String, Method> methodMaps = new ConcurrentHashMap<String, Method>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            final String methodKey = InvokeUtil.generateMethodKey(method);
            L.d("registerMethodMap: " + name + " :: " + methodKey);
            methodMaps.put(methodKey, method);
        }
        mApiMethodMap.put(name, methodMaps);
    }

    Method getMethod(String clzName, String methodName) {
        ConcurrentHashMap<String, Method> methodMap = mApiMethodMap.get(clzName);
        if (methodMap == null) {
            Class<?> classType = InvokeUtil.getClassType(clzName);
            L.d(">>classType find: " + clzName + " :: " + classType);
            if (classType == null) {
                return null;
            }
            register(classType);
            return getMethod(clzName, methodName);
        }
        L.d(">>Method: " + clzName + " :: " + methodName);
        if (methodMap == null || !methodMap.containsKey(methodName)) {
            L.w(">>Method not Found: " + methodMap + " :: " + GSON.toJson(mApiMethodMap));
            return null;
        }
        return methodMap.get(methodName);
    }

    Method getMethod(RequestBean requestBean) {
        return getMethod(requestBean.clzName, InvokeUtil.generateRequestMethod(requestBean));
    }
     Object getObject(String clzName) {
        if (mApiInstanceMap.containsKey(clzName)) {
            return mApiInstanceMap.get(clzName);
        }
        return null;
    }

}
