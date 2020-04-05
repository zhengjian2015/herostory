package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;


/**
 * 登陆服务
 */
public class LoginService {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private static final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {

    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     *
     * @param userName 用户名称
     * @param password 密码
     */
    public UserEntity userLogin(String userName, String password) {
        if (userName == null || null == password)
            return null;

        try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
            //获取DAO
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);
            //获取用户实体
            UserEntity userEntity = dao.getUserByName(userName);

            System.out.println("user");

            if (null != userEntity) {
                if (!password.equals(userEntity.password)) {
                    LOGGER.error("用户密码错误.userName={}", userName);
                    throw new RuntimeException("用户密码错误");
                }
            } else {
                    //新建用户实体
                    userEntity = new UserEntity();
                    userEntity.userName = userName;
                    userEntity.password = password;
                    userEntity.heroAvatar = "Hero_Shaman";
                    dao.insertInto(userEntity);
                }
            return userEntity;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
