package com.drunkenlion.alcoholfriday.domain.auth.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;

@Component
public class PathProviderConverter implements Converter<String, ProviderType> {
    @Override
    public ProviderType convert(String provider) {
        return ProviderType.byProviderName(provider);
    }
}
