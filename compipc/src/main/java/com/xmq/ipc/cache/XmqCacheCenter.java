package com.xmq.ipc.cache;

import com.google.gson.Gson;
import com.xmq.ipc.bean.RequestBean;
import com.xmq.ipc.bean.RequestParameter;
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
    public <T> void register(Class<T> clazz, T instance) {
        String name = clazz.getName();
        register(clazz);
        L.d("register Instance: "+name+" :: "+instance);
        mApiInstanceMap.put(name, instance);
    }
    public <T> void register(Class<T> clazz) {
        String name = clazz.getName();
        mApiClazzMap.put(name, clazz);
        registerMethodMap(clazz);
    }

//    public <T> T get(Class<T> clazz) {
//        if (clazz.isAssignableFrom(IAccountApi.class)) {
//            Object api = mApiInstanceMap.get(clazz.getName());
//            if (api == null) {
//                api = new AccountApiImpl();
//                mApiInstanceMap.put(clazz.getName(), api);
//            }
//            return (T) api;
//        }
//        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new XmqInvokeHandler());
//    }

    private void registerMethodMap(Class<?> clazz){
        String name = clazz.getName();
        ConcurrentHashMap<String, Method> methodMaps = new ConcurrentHashMap<String, Method>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            final String methodKey = generateMethodKey(method);
            L.d("registerMethodMap: "+name+" :: "+methodKey);
            methodMaps.put(methodKey, method);
        }
        mApiMethodMap.put(name, methodMaps);
    }

    public static String generateMethodKey(Method method) {
        StringBuffer buffer = new StringBuffer(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (Class<?> parameter : parameters) {
            buffer.append("-").append(parameter.getName());
        }
        return buffer.toString();

    }

    public Method getMethod(String clzName, String methodName) {
        ConcurrentHashMap<String, Method> methodMap = mApiMethodMap.get(clzName);
        L.d(">>Method: "+clzName+" :: "+methodName);
        if (methodMap == null || !methodMap.containsKey(methodName)) {
            L.d(">>Method not Found: "+methodMap+" :: "+GSON.toJson(mApiMethodMap));
            return null;
        }
        return methodMap.get(methodName);
    }

    public Method getMethod(RequestBean requestBean) {
        return getMethod(requestBean.clzName, generateRequestMethod(requestBean));
    }

    private String generateRequestMethod(RequestBean requestBean) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(requestBean.method);
        for (RequestParameter param : requestBean.params) {
            buffer.append("-").append(param.clzName);
        }
        return buffer.toString();
    }

    public Object[] makeParameterObject(RequestBean requestBean) {
        Object[] args = new Object[requestBean.params.length];
        RequestParameter[] params = requestBean.params;
        for (int i = 0; i < params.length; i++) {
            RequestParameter param = params[i];
            Class<?> clazz = getClassType(param.clzName);
            args[i] = GSON.fromJson(param.value, clazz);
        }
        return args;
    }

    private Class<?> getClassType(String clzName) {
        try {
            return Class.forName(clzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> RequestBean generateRequestBean(Class<T> clazz, Method method, Object[] parameters, int type) {
        String className = clazz.getName();
        String methodName = method.getName();
        RequestParameter[] requestParameters = new RequestParameter[parameters.length];
        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            Object parameter = parameters[i];
            requestParameters[i] = new RequestParameter(parameter.getClass().getName(), GSON.toJson(parameter));
        }

        return new RequestBean(className, methodName, type, requestParameters);
    }

    public <T> String generateRequestBeanJson(Class<T> clazz, Method method, Object[] parameters, int type) {
        String className = clazz.getName();
        String methodName = method.getName();
        RequestParameter[] requestParameters = new RequestParameter[parameters.length];
        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            Object parameter = parameters[i];
            requestParameters[i] = new RequestParameter(parameter.getClass().getName(), GSON.toJson(parameter));
        }
        RequestBean requestBean = new RequestBean(className, methodName, type, requestParameters);
        return GSON.toJson(requestBean);
    }

    public Object getObject(String clzName) {
        if (mApiInstanceMap.containsKey(clzName)) {
            return mApiInstanceMap.get(clzName);
        }
        return null;
    }
}
