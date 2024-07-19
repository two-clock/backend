package com.twoclock.gitconnect;

import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/v1/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/api/v1/error")
    public void error() {
        throw new RuntimeException("This is a 500 error");
    }

    @GetMapping("/api/v1/custom-error")
    public void customError() {
        throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
    }
}
