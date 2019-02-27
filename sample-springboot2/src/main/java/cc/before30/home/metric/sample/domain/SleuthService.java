package cc.before30.home.metric.sample.domain;

import brave.Span;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SleuthService {

    @Autowired
    private Tracer tracer;

    @Autowired
    private Executor executor;

    public void doSomeWorkSameSpan() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        log.info("Doing some work");
    }

    public void doSomeWorkNewSpan() {
        log.info("I'm in the original span");
        Span newSpan = tracer.newTrace().name("newSpan").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
            try {
                TimeUnit.SECONDS.sleep(1);
                log.info("I'm in the new span doing some cool work that needs its own span");
            } catch (InterruptedException e) {
            } finally {
                newSpan.finish();
            }
        }
        log.info("I'm in the original span");
    }

    public void doSomeWorkInNewThread() {
        log.info("New Thread");
        executor.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            log.info("I'm in the new thread - with a new span");
        });
        log.info("I'm done - with the original span");
    }

    @Async
    public void asyncMethod() {
        log.info("Start async method");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        log.info("End async method");

    }
}
