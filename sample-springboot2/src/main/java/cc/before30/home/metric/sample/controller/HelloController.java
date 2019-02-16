package cc.before30.home.metric.sample.controller;

import cc.before30.home.metric.sample.domain.remote.DaumSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private DaumSearchService daumSearchService;

    @GetMapping("/hello")
    public String hello() {
        return "world";
    }

    @GetMapping("/hello2")
    public String hello2() {
        return daumSearchService.test();
    }
}
