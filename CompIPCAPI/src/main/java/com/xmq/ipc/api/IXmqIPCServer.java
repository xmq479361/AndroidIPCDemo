package com.xmq.ipc.api;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/6 21:02
 */
public interface IXmqIPCServer {
    <T> void register(Class<T> clazz, T instance);
}
