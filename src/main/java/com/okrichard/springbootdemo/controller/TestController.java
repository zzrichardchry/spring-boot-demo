package com.okrichard.springbootdemo.controller;

import com.okrichard.springbootdemo.service.TestService;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zhuhaotian
 * @Date 2024/1/20
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/run")
    public String test() {
        return "Hi, Spring Boot!" + testService.test();
    }

    @GetMapping("/run2")
    public String test2(@RequestParam("name") String name, @RequestParam("age") Integer age) {
        return String.format("Name: %s, Age: %d", name, age);
    }

    @GetMapping("/run3/{name}")
    public String test3(@PathVariable String name) {
        return String.format("Name: %s", name);
    }

    @GetMapping("/run4")
    public String test4() {
        testService.test2();
        throw new RuntimeException("Test Exception");
    }
}
