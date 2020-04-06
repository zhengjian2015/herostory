package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {

    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    private static final AsyncOperationProcessor _instance = new AsyncOperationProcessor();


    /**
     * 线程数组  不用多线程的线程池是因为，可能出现连续点2下类似 刷单的行为
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];
    /**
     * 获取单例对象
     *
     * @return 异步操作处理器
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 私有化类默认构造器
     */
    private AsyncOperationProcessor(){
        for (int i = 0; i < _esArray.length; i++) {
            // 线程名称
            final String threadName = "AsyncOperationProcessor_" + i;
            // 创建单线程服务
            _esArray[i] = Executors.newSingleThreadExecutor((newRunnable) -> {
                Thread newThread = new Thread(newRunnable);
                newThread.setName(threadName);
                return newThread;
            });
        }
    }


    /**
     * 处理异步操作
     *
     * 异步执行 然后回到主线程执行
     * @param asyncOp 异步操作
     */
    public void process(IAsyncOperation asyncOp) {
        if (null == asyncOp) {
            return;
        }

        // 根据绑定 Id 获取线程索引
        int bindId = Math.abs(asyncOp.getBindId());
        int esIndex = bindId % _esArray.length;
        System.out.println("*******");
        System.out.println(esIndex);
        _esArray[esIndex].submit(() -> {
            try {
                //执行异步操作
                asyncOp.doAsync();

                //返回主线程执行完成的逻辑
                MainThreadProcessor.getInstance().process(() -> {
                    asyncOp.doFinish();
                });
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(),ex);
            }
        });
    }
}
