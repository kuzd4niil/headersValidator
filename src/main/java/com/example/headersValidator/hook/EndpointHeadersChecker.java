package com.example.headersValidator.hook;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author : daniil
 * @description :
 * @create : 2023-01-30
 */
@Component
public class EndpointHeadersChecker {
    private final HashMap<EndpointHookObject, String[]> endpointHeadersRules = new HashMap<>();

    public static class EndpointHookObject {
        private final String httpMethod;
        private final String uri;

        public EndpointHookObject(String httpMethod, String uri) {
            this.httpMethod = httpMethod;
            this.uri = uri;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EndpointHookObject that = (EndpointHookObject) o;
            return httpMethod.equals(that.httpMethod) && uri.equals(that.uri);
        }

        @Override
        public int hashCode() {
            return 13 * httpMethod.hashCode() + 7 * uri.hashCode();
        }

        @Override
        public String toString() {
            return "EndpointHookObject{" +
                    "httpMethod=" + httpMethod +
                    ", uri='" + uri + '\'' +
                    '}';
        }
    }

    public void registerEndpoint(EndpointHookObject endpoint, String[] headers) {
        endpointHeadersRules.put(endpoint, headers);
    }

    public HashMap<EndpointHookObject, String[]> getEndpointHeadersRules() {
        return endpointHeadersRules;
    }
}
