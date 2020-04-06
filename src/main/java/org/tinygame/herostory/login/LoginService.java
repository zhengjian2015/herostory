package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
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
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (userName == null || null == password)
            return;
        // 创建异步操纵
        AsyncGetUserByName asyncOp = new AsyncGetUserByName(userName, password) {
            @Override
            public void doFinish() {
                if (null != callback) {
                    // 执行回调函数
                    callback.apply(this.getUserEntity());
                }
            }
        };

        // 执行异步操纵
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }


    /**
     * 异步方式获取用户
     */
    private class AsyncGetUserByName implements IAsyncOperation {

        /**
         * 用户名称
         */
        private String _userName;
        /**
         * 密码
         */
        private String _password;


        /**
         * 用户实体
         */
        private UserEntity _userEntity = null;

        /**
         * 获取用户实体
         *
         * @return 用户实体
         */
        public UserEntity getUserEntity() {
            return _userEntity;
        }

        /**
         * 类参数构造器
         *
         * @param userName
         * @param password
         */
        AsyncGetUserByName(String userName, String password) {
            _userName = userName;
            _password = password;
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                //获取DAO
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                // 看看当前线程
                LOGGER.info("login 的当前线程 = {}", Thread.currentThread().getName());

                //获取用户实体
                UserEntity userEntity = dao.getUserByName(_userName);

                if (null != userEntity) {
                    if (!_password.equals(userEntity.password)) {
                        LOGGER.error("用户密码错误.userName={}", _userName);
                        throw new RuntimeException("用户密码错误");
                    }
                } else {
                    //新建用户实体
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman";
                    dao.insertInto(userEntity);
                }

                _userEntity = userEntity;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
