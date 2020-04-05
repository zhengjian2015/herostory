package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理器
 */
public final class UserManager {

    /**
     * 用户字典  似乎并不是线程安全的
     */
    private static final Map<Integer,User> _userMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private UserManager() {

    }

    public static void addUser(User newUser) {
        if(null != newUser) {
            _userMap.put(newUser.userId,newUser);
        }
    }

    /**
     * 根据id移除用户
     * @param usrId
     */
    public static void removeUserById(int usrId) {
        _userMap.remove(usrId);
    }

    /**
     *
     * @return
     */
    public static Collection<User> listUser(){
        return _userMap.values();
    }

    /**
     * 根据 Id 获取用户
     *
     * @param userId 用户 Id
     * @return 用户对象
     */
    static public User getUserById(int userId) {
        return _userMap.get(userId);
    }
}
