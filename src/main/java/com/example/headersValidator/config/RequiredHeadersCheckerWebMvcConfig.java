package com.example.headersValidator.config;

import com.example.headersValidator.interceptor.RequiredHeadersCheckerWebRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author : daniil
 * @description :
 * @create : 2023-01-30
 */
@Component
public class RequiredHeadersCheckerWebMvcConfig extends WebMvcConfigurationSupport {
    private final RequiredHeadersCheckerWebRequestInterceptor interceptor;

    @Autowired
    public RequiredHeadersCheckerWebMvcConfig(RequiredHeadersCheckerWebRequestInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
