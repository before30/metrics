package cc.before30.home.metric.sample.controller;

import cc.before30.home.metric.sample.domain.SleuthService;
import cc.before30.home.metric.sample.domain.customer.Customer;
import cc.before30.home.metric.sample.domain.customer.CustomerService;
import cc.before30.home.metric.sample.domain.remote.RemoteService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Timed
@Slf4j
public class APIController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private SleuthService sleuthService;

    @GetMapping("/api/customers")
    public Iterable<Customer> customers() {
        return customerService.findAll();
    }

    @GetMapping("/api/customer/{id}")
    public Optional<Customer> customer(@PathVariable("id") Long id) {
        return customerService.findOne(id);
    }

    @GetMapping("/api/remote/test1")
    public String test1() {
        return remoteService.test1();
    }

    @GetMapping("/api/remote/test1hystrix/{id}")
    public String test1(@PathVariable("id") Long id) {
        return remoteService.hystrixTest1(id);
    }

    @GetMapping("/same-span")
    public String helloSleuthSameSpan() {
        log.info("Same Span");
        sleuthService.doSomeWorkSameSpan();

        return "success";
    }

    @GetMapping("/new-span")
    public String helloSleuthNewSpan() {
        log.info("New Span");
        sleuthService.doSomeWorkNewSpan();

        return "success";
    }

    @GetMapping("/new-thread")
    public String helloSleuthNewThread() {
        log.info("New Thread");
        sleuthService.doSomeWorkInNewThread();

        return "success";
    }

    @GetMapping("/async")
    public String helloSleuthAsync() {
        log.info("before async method call");
        sleuthService.asyncMethod();
        log.info("after async method call");

        return "success";
    }
}
