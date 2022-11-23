package kumoh.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Thread Error Exception");
        log.error("exception Message :: " + ex.getMessage());
        log.error("method name :: " + method.getName());
            for(Object param : params)
                System.out.println("param Val ::: + " + param);
    }
}
