package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

public final class CmdHandlerFactory {

    private static Map<Class<?>,IcmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap();
    /**
     * 私有化
     */
    private CmdHandlerFactory(){

    }

    public static void init(){
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class,new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class,new UserMoveToCmdHandler());
    }

    public static IcmdHandler<? extends GeneratedMessageV3> create(Class<?> myclazz){
        if(null == myclazz) {
            return null;
        }

        return _handlerMap.get(myclazz);
    }


}
