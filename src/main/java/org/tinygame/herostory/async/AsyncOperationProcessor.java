package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {

    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    private static final AsyncOperationProcessor _instance = new AsyncOperationProcessor();


    private static final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable)-> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("AsyncOperationProcessor");
        return newThread;
    });

    /**
     * 获取单例对象
     *
     * @return 异步操作处理器
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    private AsyncOperationProcessor(){}


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
        _es.submit(() -> {
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
