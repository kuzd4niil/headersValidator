package com.example.headersValidator.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.headersValidator.hook.EndpointHeadersChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : daniil
 * @description :
 * @create : 2023-01-30
 */
@Component
public class RequiredHeadersCheckerWebRequestInterceptor implements HandlerInterceptor {
    private final EndpointHeadersChecker endpointHeadersChecker;

    @Autowired
    public RequiredHeadersCheckerWebRequestInterceptor(EndpointHeadersChecker endpointHeadersChecker) {
        this.endpointHeadersChecker = endpointHeadersChecker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/")) {
            uri = "/".concat(uri);
        }
        if (!uri.endsWith("/")) {
            uri = uri.concat("/");
        }

        EndpointHeadersChecker.EndpointHookObject endpoint = new EndpointHeadersChecker.EndpointHookObject(
                request.getMethod(),
                uri
        );

        String[] headersOfRequest = endpointHeadersChecker.getEndpointHeadersRules().get(endpoint);

        if (headersOfRequest != null) {
            Set<String> setOfHeadersOfRequest = new HashSet<>(Set.of(headersOfRequest));

            Enumeration<String> header = request.getHeaderNames();

            while (header.hasMoreElements()) {
                setOfHeadersOfRequest.remove(header.nextElement());
            }

            if (!setOfHeadersOfRequest.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        }

        return true;
    }
}