package com.yagnik.hospito.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloWorld {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello World from Spring Boot!";
    }

    @GetMapping("/")
    public String index()
    {
        return "Hello World from yagnik!";
    }
}