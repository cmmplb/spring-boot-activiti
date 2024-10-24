package io.github.cmmplb.activiti.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author penglibo
 * @date 2024-10-20 20:47:56
 * @since jdk 1.8
 */

@RestController
public class TestController {

    @GetMapping(value = "/test")
    public String test() {
        return "success";
    }
}
