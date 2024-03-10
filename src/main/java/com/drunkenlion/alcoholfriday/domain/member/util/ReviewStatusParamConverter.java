package com.drunkenlion.alcoholfriday.domain.member.util;

import com.drunkenlion.alcoholfriday.domain.member.enumerated.ReviewStatus;
import org.springframework.core.convert.converter.Converter;

public class ReviewStatusParamConverter implements Converter<String, ReviewStatus> {
    @Override
    public ReviewStatus convert(String source) {
        return ReviewStatus.valueOf(source.toUpperCase());
    }
}
