package com.xmq.ipc.bean;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 22:49
 */
public class RequestParameter {
    public String clzName;
    public String value;

    public RequestParameter(String clzName, String value) {
        this.clzName = clzName;
        this.value = value;
    }
}
