package cc.before30.home.metric.sample.config;

import com.netflix.concurrency.limits.Limiter;
import com.netflix.concurrency.limits.executors.BlockingAdaptiveExecutor;
import com.netflix.concurrency.limits.limit.TracingLimitDecorator;
import com.netflix.concurrency.limits.limit.VegasLimit;
import com.netflix.concurrency.limits.limiter.SimpleLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();

        return builder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(5000))
                .build();
    }

    @Bean
    public BlockingAdaptiveExecutor executor() {
        Limiter<Void> limiter = SimpleLimiter.newBuilder()
                .limit(TracingLimitDecorator.wrap(VegasLimit.newBuilder()
                        .initialLimit(10)
                        .build())).build();
        return new BlockingAdaptiveExecutor(limiter);
    }
}
