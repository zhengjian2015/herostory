package org.tinygame.herostory;

import com.google.protobuf.Message;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class GameMessageRecognizer {



    private GameMessageRecognizer(){

    }

    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        switch (msgCode) {
            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                //把字节赋给每个类的成员
                return GameMsgProtocol.UserEntryCmd.newBuilder();
            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                return GameMsgProtocol.WhoElseIsHereCmd.newBuilder();
            case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
                return GameMsgProtocol.UserMoveToCmd.newBuilder();
            default:
                return null;
        }
    }


    public static int getMsgCodeByMsgClazz(Object msg){
        if (msg instanceof GameMsgProtocol.UserEntryResult) {
            return GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else if(msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
            return GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        } else if(msg instanceof GameMsgProtocol.UserMoveToResult) {
            return GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        }else if(msg instanceof GameMsgProtocol.UserQuitResult) {
            return GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        } else {
            System.out.println("无法识别消息类型");
            return -1;
        }

    }
}
