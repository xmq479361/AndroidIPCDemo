package com.xmq.ipc.util;

import android.util.Log;

import com.google.gson.Gson;
import com.xmq.ipc.bean.RequestBean;
import com.xmq.ipc.bean.RequestParameter;

import java.lang.reflect.Method;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/6 22:54
 */
public class InvokeUtil {
    public static Gson GSON = new Gson();
    public static String generateMethodKey(Method method) {
        StringBuffer buffer = new StringBuffer(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (Class<?> parameter : parameters) {
            buffer.append("-").append(parameter.getName());
        }
        return buffer.toString();
    }


    public static Class<?> getClassType(String clzName) {
        try {
            return Class.forName(clzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  String generateRequestMethod(RequestBean requestBean) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(requestBean.method);
        for (RequestParameter param : requestBean.params) {
            buffer.append("-").append(param.clzName);
        }
        return buffer.toString();
    }

    public static  Object[] makeParameterObject(RequestBean requestBean) {
        Object[] args = new Object[requestBean.params.length];
        RequestParameter[] params = requestBean.params;
        for (int i = 0; i < params.length; i++) {
            RequestParameter param = params[i];
            Class<?> clazz = InvokeUtil.getClassType(param.clzName);
            args[i] = GSON.fromJson(param.value, clazz);
        }
        return args;
    }
    public static Method generateInstanceMethod(Class<?> clazz, Object[] args) {
        final int argsLength = args.length;
        Class[] parameterClz = new Class[argsLength];
        for (int i = 0; i < argsLength; i++) {
            Object arg = args[i];
            parameterClz[i] = arg.getClass();
//            buffer.append("-").append(arg.getClass().getName());
        }
        try {
            return clazz.getMethod("getInstance", parameterClz);
        } catch (NoSuchMethodException e) {
            L.e(Log.getStackTraceString(e));
        }
        return null;
    }

    public static <T> String generateRequestBeanJson(Class<T> clazz, Method method, Object[] parameters, int type) {
        String methodName = method.getName();
        return generateRequestBeanJson(clazz, methodName, parameters, type);
    }

    public static <T> String generateRequestBeanJson(Class<T> clazz, String methodName, Object[] parameters, int type) {
        String className = clazz.getName();
        if (parameters == null) {
            parameters = new Object[0];
        }
        RequestParameter[] requestParameters = new RequestParameter[parameters.length];
        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            Object parameter = parameters[i];
            requestParameters[i] = new RequestParameter(parameter.getClass().getName(), GSON.toJson(parameter));
        }
        RequestBean requestBean = new RequestBean(className, methodName, type, requestParameters);
        return GSON.toJson(requestBean);
    }
}
