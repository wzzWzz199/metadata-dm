package com.hayden.hap.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务工具类，使用线程池来执行异步任务
 *
 * @author wushuangyang
 * @Date 2016-07-07 9:30:00
 */
@Component
public class AsynTaskUtils {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static AsynTaskUtils instance;
    private ExecutorService executorService;

    private AsynTaskUtils() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final ThreadFactory defaultFactory = Executors
                    .defaultThreadFactory();
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                if (!thread.isDaemon()) {
                    thread.setDaemon(true);
                }
                thread.setName("Hayden.AsynTask-" + threadNumber.getAndIncrement());
                return thread;
            }
        };
        logger.error(Runtime.getRuntime().availableProcessors() == 0 ? "null" : String.valueOf(Runtime.getRuntime().availableProcessors()));
        this.executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 50, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
    }

    @Bean(name = "asynTaskUtils")
    public static AsynTaskUtils getInstance() {
        if (instance == null) {
            synchronized (AsynTaskUtils.class) {
                if (instance == null) {
                    instance = new AsynTaskUtils();
                }
            }
        }
        return instance;
    }

    public Future<?> submit(Runnable task) {
        return this.executorService.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return this.executorService.submit(task, result);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.executorService.submit(task);
    }

    public void execute(Runnable command) {
        this.executorService.execute(command);
    }

    @PreDestroy
    public void destory() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
