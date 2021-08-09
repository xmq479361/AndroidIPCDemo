package com.xmq.ipc.api;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/6 21:03
 */
public interface IXmqIPCClient {
    void connect();
    void connect(String packageName);
    <T> void register(Class<T> clazz);
    <T> T getApi(Class<T> clazz, Object... parameters);
}
