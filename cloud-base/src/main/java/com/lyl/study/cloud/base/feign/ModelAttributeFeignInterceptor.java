package com.lyl.study.cloud.base.feign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 让Feign可以进行Get方法的@ModelAttribute形式传参
 */
public class ModelAttributeFeignInterceptor implements RequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ModelAttributeFeignInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        byte[] body = template.body();
        if (HttpMethod.GET.matches(template.method()) && body != null && body.length > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> map = objectMapper.readValue(body, new TypeReference<HashMap<String, Object>>() {
                });
                for (String key : map.keySet()) {
                    Object o = map.get(key);
                    if (o != null) {
                        // 时间转时间戳
                        if (o instanceof Date) {
                            o = ((Date) o).getTime();
                        }

                        // 处理数组
                        if (o.getClass().isArray()) {
                            int length = Array.getLength(o);
                            for (int i = 0; i < length; i++) {
                                Object value = Array.get(o, i);
                                if (value != null) {
                                    template.query(false, key, value.toString());
                                }
                            }
                        }
                        // 处理迭代器类型
                        else if (o instanceof Iterable) {
                            Iterator iterator = ((Iterable) o).iterator();
                            while (iterator.hasNext()) {
                                Object value = iterator.next();
                                if (value != null) {
                                    template.query(false, key, value.toString());
                                }
                            }
                        }
                        // 默认类型
                        else {
                            template.query(false, key, o.toString());
                        }
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            template.body(null);
        }
    }
}
