package com.xmq.ipc.core;

import android.content.Context;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/6 21:03
 */
public interface IXmqIPCClient {
    void connect(Context context);
    void connect(Context context, String packageName);
    <T> void register(Class<T> clazz);
    <T> T getApi(Class<T> clazz, Object... parameters);
}
