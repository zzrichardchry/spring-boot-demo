package com.okrichard.springbootdemo.service;

import org.springframework.stereotype.Service;

/**
 * @Author zhuhaotian
 * @Date 2024/1/20
 */
@Service
public class TestService {
    public String test() {
        return "test";
    }

    public String test2() {
        throw new RuntimeException("test2");
    }
}
