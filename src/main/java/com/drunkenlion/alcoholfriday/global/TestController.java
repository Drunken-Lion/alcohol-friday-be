package com.drunkenlion.alcoholfriday.global;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/test/exception")
    public void test() {
        throw BusinessException.builder()
                .response(HttpResponse.Fail.CONFLICT)
                .build();
    }
}
