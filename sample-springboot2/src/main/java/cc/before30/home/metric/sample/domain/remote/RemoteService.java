package cc.before30.home.metric.sample.domain.remote;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@Slf4j
public class RemoteService {

    private final RestTemplate restTemplate;

    private final RetryPolicy<Object> retryPolicy;

    public RemoteService(RestTemplate restTemplate,
                         RetryPolicy<Object> retryPolicy) {
        this.restTemplate = restTemplate;
        this.retryPolicy = retryPolicy;
    }

    public String test1() {
        String val = Failsafe.with(retryPolicy).get(() -> restTemplate.getForObject("https://www.daum.net", String.class));
        if (Objects.isNull(val)) {
            return "bad";
        }

        return "good";
    }

    @HystrixCommand(fallbackMethod = "hystrixTest1Fallback")
    public String hystrixTest1(Long arg) {
        log.info("hystrix test1 method {}", arg);
        if (arg == 1) {
            throw new RuntimeException("exception!!!");
        }
        return "good-hystrix";
    }

    public String hystrixTest1Fallback(Long arg) {
        log.info("hystrix test1Fallback method {}", arg);

        return "bad-hystrix";
    }

}
