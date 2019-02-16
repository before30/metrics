package cc.before30.home.metric.sample.domain.remote;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DaumSearchService {
    private final RestTemplate restTemplate;

    public DaumSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "testFallback")
    public String test() {
        String rval = restTemplate.getForObject("https://www.daum.net", String.class);

        return "success";
    }

    public String testFallback() {
        return "fail";
    }
}
