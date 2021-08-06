package com.xmq.ipc.core;

import android.content.Context;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/6 21:02
 */
public interface IXmqIPCServer {
    void init(Context context);
    <T> void register(Class<T> clazz, T instance);
}
