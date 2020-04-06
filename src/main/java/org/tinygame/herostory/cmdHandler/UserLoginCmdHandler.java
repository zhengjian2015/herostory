package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserLoginCmdHandler implements IcmdHandler<GameMsgProtocol.UserLoginCmd> {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx ||
                null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        LOGGER.info(
                "用户登陆, userName = {}, password = {}",
                userName,
                password
        );


        /**
         * 单例新起一个线程
         */
        AsyncOperationProcessor.getInstance().process(()->{
            UserEntity userEntity = null;

            try {
                userEntity = LoginService.getInstance().userLogin(
                        cmd.getUserName(),
                        cmd.getPassword());

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(),ex);
            }

            if(null == userEntity) {
                LOGGER.error("用户登录失败，userName = {}",cmd.getUserName());
                return;
            }

            // 新建用户,
            User newUser = new User();
            newUser.userId = userEntity.userId;
            newUser.userName = userEntity.userName;
            newUser.heroAvatar = userEntity.heroAvatar;
            newUser.currHp = 100;
            //开启用户加入管理器
            UserManager.addUser(newUser);

            //将用户id附着到channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.userId);

            // 构建结果并发送
            GameMsgProtocol.UserLoginResult.Builder
                    resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            resultBuilder.setUserId(newUser.userId);
            resultBuilder.setUserName(newUser.userName);
            resultBuilder.setHeroAvatar(newUser.heroAvatar);

            // 构建结果并发送
            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);

        });

    }
}
