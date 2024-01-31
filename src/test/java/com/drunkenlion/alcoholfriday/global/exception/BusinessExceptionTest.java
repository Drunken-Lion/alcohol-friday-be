package com.drunkenlion.alcoholfriday.global.exception;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BusinessExceptionTest {
    @Test
    void test() {
        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.CONFLICT)
                    .build();
        });

        assertThat(businessException.getStatus()).isEqualTo(HttpResponse.Fail.CONFLICT.getStatus());
        assertThat(businessException.getMessage()).isEqualTo(HttpResponse.Fail.CONFLICT.getMessage());
    }
}