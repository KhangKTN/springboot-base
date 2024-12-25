package com.khangktn.springbase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloSpringController {
    @GetMapping("/")
    String home() {
        return "index";
    }

    @GetMapping("/hello")
    String hello() {
        return "hello/hello";
    }
}
