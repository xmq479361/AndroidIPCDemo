package com.xmq.account.api;

import com.xmq.ipc.api.ClassId;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:18
 */
@ClassId("com.xmq.account.impl.AccountApiImpl")
public interface IAccountApi {

    User getUser(String accountNo);

}
