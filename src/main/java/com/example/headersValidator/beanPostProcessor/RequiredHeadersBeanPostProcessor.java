package com.example.headersValidator.beanPostProcessor;

import com.example.headersValidator.annotation.RequiredHeaders;
import com.example.headersValidator.hook.EndpointHeadersChecker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author : daniil
 * @description :
 * @create : 2023-01-30
 */
@Component
public class RequiredHeadersBeanPostProcessor implements BeanPostProcessor {
    private final EndpointHeadersChecker endpointHeadersChecker;

    @Autowired
    public RequiredHeadersBeanPostProcessor(EndpointHeadersChecker endpointHeadersChecker) {
        this.endpointHeadersChecker = endpointHeadersChecker;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> result = bean.getClass();
        if (result.isAnnotationPresent(RestController.class) || result.isAnnotationPresent(Controller.class)) {
            String[] value = {"/"};

            RequestMapping requestMapping = result.getAnnotation(RequestMapping.class);
            if (requestMapping != null)  {
                value = requestMapping.value();
            }

            for (String s : value) {
                if (!s.startsWith("$")) {
                    if (!s.startsWith("/")) {
                        s = "/".concat(s);
                    }

                    String finalS = s;
                    Arrays.stream(result.getMethods()).forEach(m -> {
                        RequiredHeaders requiredHeaders = m.getAnnotation(RequiredHeaders.class);
                        if (requiredHeaders != null) {
                            List<EndpointHeadersChecker.EndpointHookObject> endpointHookObject = getEndpointHookObject(m, finalS);
                            if (endpointHookObject != null) {
                                String[] headers = requiredHeaders.value();
                                // Если нет требуемых заголовков, то просто опускаем требования
                                if (headers.length > 0) {
                                    endpointHookObject.forEach(e -> {
                                        endpointHeadersChecker.registerEndpoint(e, headers);
                                    });
                                }
                            }
                        }
                    });
                }
            }
        }

        return bean;
    }

    private List<EndpointHeadersChecker.EndpointHookObject> getEndpointHookObject(Method method, String uri) {
        List<EndpointHeadersChecker.EndpointHookObject> list = new LinkedList<>();
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);

        String methodName = "";
        String[] paths = new String[0];
        getHttpMethodNameByMapping(method, methodName, paths);

        for (String path : paths) {
            uri = insertSlashIntoEndURI(uri);
            uri = uri.concat(path);
            uri = insertSlashIntoEndURI(uri);
            list.add(new EndpointHeadersChecker.EndpointHookObject(
                    methodName,
                    uri
            ));
        }

        if (list.size() == 0)
            return null;

        return list;
    }

    private final void getHttpMethodNameByMapping(Method method, String methodName, String[] paths) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            methodName = HttpMethod.GET.name();
            paths = method.getAnnotation(GetMapping.class).value();
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            methodName = HttpMethod.POST.name();
            paths = method.getAnnotation(PostMapping.class).value();
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            methodName = HttpMethod.PUT.name();
            paths = method.getAnnotation(PutMapping.class).value();
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            methodName = HttpMethod.DELETE.name();
            paths = method.getAnnotation(DeleteMapping.class).value();
        }
        if (method.isAnnotationPresent(PatchMapping.class)) {
            methodName = HttpMethod.PATCH.name();
            paths = method.getAnnotation(PatchMapping.class).value();
        }
    }

    private String insertSlashIntoEndURI(String uri) {
        if (!uri.endsWith("/")) {
            return uri.concat("/");
        }
        return uri;
    }
}
