package com.example.headersValidator;

import com.example.headersValidator.beanPostProcessor.RequiredHeadersBeanPostProcessor;
import com.example.headersValidator.config.RequiredHeadersCheckerWebMvcConfig;
import com.example.headersValidator.hook.EndpointHeadersChecker;
import com.example.headersValidator.interceptor.RequiredHeadersCheckerWebRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeadersValidatorConfiguration {
	@Bean
	public EndpointHeadersChecker endpointHeadersChecker() {
		return new EndpointHeadersChecker();
	}

	@Bean
	public RequiredHeadersCheckerWebRequestInterceptor requiredHeadersCheckerWebRequestInterceptor() {
		return new RequiredHeadersCheckerWebRequestInterceptor(endpointHeadersChecker());
	}

	@Bean
	public RequiredHeadersBeanPostProcessor requiredHeadersBeanPostProcessor() {
		return new RequiredHeadersBeanPostProcessor(endpointHeadersChecker());
	}

	@Bean
	public RequiredHeadersCheckerWebMvcConfig requiredHeadersCheckerWebMvcConfig() {
		return new RequiredHeadersCheckerWebMvcConfig(requiredHeadersCheckerWebRequestInterceptor());
	}
}