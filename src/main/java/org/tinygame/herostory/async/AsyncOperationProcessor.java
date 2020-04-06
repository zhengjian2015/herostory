package org.tinygame.herostory.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {

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


    public void process(Runnable r) {
        if(null == r) {
            return;
        }
        _es.submit(r);
    }
}
