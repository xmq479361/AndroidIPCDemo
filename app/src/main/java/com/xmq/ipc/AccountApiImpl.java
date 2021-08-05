package com.xmq.ipc;

import com.xmq.account.api.IAccountApi;
import com.xmq.account.api.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/4 20:20
 */
public class AccountApiImpl implements IAccountApi {
    ConcurrentHashMap<String, User> mUserCache = new ConcurrentHashMap<>();
    static AccountApiImpl INSTANCE;
    public static synchronized AccountApiImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AccountApiImpl();
        }
        return INSTANCE;
    }
    @Override
    public User getUser(String accountNo) {
        return mUserCache.get(accountNo);
    }

    public void setUser(String accountNo, User user) {
        mUserCache.put(accountNo, user);
    }
}
