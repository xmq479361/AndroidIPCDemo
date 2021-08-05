package com.xmq.ipc.bean;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 22:48
 */
public class RequestBean {
    public String clzName;
    public String method;
    public int type;
    public RequestParameter[] params;

    public RequestBean(String clzName, String method, int type, RequestParameter[] params) {
        this.clzName = clzName;
        this.method = method;
        this.type = type;
        this.params = params;
    }
}
