package cc.before30.home.metric.sample;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@Slf4j
@EnableCircuitBreaker
public class SampleBoot2Application {

    public static void main(String[] args) {
        log.info("hello world");
        SpringApplication.run(SampleBoot2Application.class, args);

    }
}
