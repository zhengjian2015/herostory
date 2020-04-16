package org.tinygame.herostory.rank;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    /**
     * 单例对象
     */
    static private final RankService _instance = new RankService();

    /**
     * 私有化类默认构造器
     */
    private RankService() {
    }

    /**
     * 获取单例对象
     *
     * @return 排行榜服务
     */
    static public RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排名列表
     * @param callback
     */
    public void getRank(Function<List<RankItem>,Void> callback){
        IAsyncOperation asyncOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);

    }

    private class AsyncGetRank implements IAsyncOperation {

        /**
         * 排名条目列表
         */
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名条目列表
         *
         * @return 排名条目列表
         */
        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getRedis()){
                //获取字符串集合
                Set<Tuple> valSet = redis.zrangeByScoreWithScores("Rank",0,9);
                _rankItemList = new ArrayList<>();
                for(Tuple t:valSet){
                    //获取用户ID
                    int userId = Integer.parseInt(t.getElement());
                    //获取用户的基本信息
                    String jsonStr = redis.hget("User_"+userId,"BasicInfo");
                    if(null == jsonStr || jsonStr.isEmpty()) {
                        continue;
                    }

                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);

                    RankItem newItem = new RankItem();
                    newItem.userId = userId;
                    newItem.userName = jsonObject.getString("userName");
                    newItem.heroAvatar = jsonObject.getString("heroAdvatar");
                    newItem.win = (int) t.getScore();

                    _rankItemList.add(newItem);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(),ex);
            }
        }
    }

}


