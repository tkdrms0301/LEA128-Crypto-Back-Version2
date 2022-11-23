package kumoh.config;

import kumoh.exception.AsyncExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

//@Configuration
@EnableAsync
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    //기본 쓰레드 수
    private static int TASK_CORE_POOL_SIZE = Integer.MAX_VALUE;
    //최대 Thread 수
    private static int TASK_MAX_POOL_SIZE = Integer.MAX_VALUE;
    //Queue 수
    private static int TASK_QUEUE_CAPACITY = 0;
    //Thread Bean Name
    private final String EXECUTOR_BEAN_NAME = "executor";

    private final ThreadPoolTaskExecutor executor;
    private static String startTime;

    @Override
    public synchronized Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
        executor.setBeanName(EXECUTOR_BEAN_NAME);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    public synchronized void destroy() {
        executor.shutdown();
    }

    public synchronized void start(String startTime) {
        AsyncConfig.startTime = startTime;
    }

    public void setStartTime(String data){
        startTime = data;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    public boolean checkSampleTaskExecute() {
        boolean result = true;

        log.info("활성 Task 수 :::: " + executor.getThreadPoolExecutor().getActiveCount());

        if(executor.getActiveCount() >= (TASK_MAX_POOL_SIZE + TASK_QUEUE_CAPACITY)){

            result = false;
        }
        return result;
    }

    public synchronized void setTaskCorePoolSize(int size) {
        TASK_CORE_POOL_SIZE = size;
    }

    public synchronized void setTaskMaxPoolSize(int size) {
        TASK_MAX_POOL_SIZE = size;
    }

    public String getStartTime() {
        return startTime;
    }


}