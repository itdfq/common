package com.itdfq.common.apiencory.annotition.support;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.itdfq.common.Exception.BizException;
import com.itdfq.common.apiencory.annotition.SecurityParameter;
import com.itdfq.common.utils.encry.AesEncryptUtils;
import com.itdfq.common.utils.encry.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author: QianMo
 * @date: 2022/2/17 14:52
 * @mark:请求数据解密
 */
@ControllerAdvice
@Slf4j
public class DecodeRequestBodyAdvice implements RequestBodyAdvice {


    @Value("${decrypt.private.key}")
    private String SERVER_PRIVATE_KEY;

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        try {
            boolean encode = false;
            if (methodParameter.getMethod().isAnnotationPresent(SecurityParameter.class)) {
                //获取注解配置的包含和去除字段
                SecurityParameter serializedField = methodParameter.getMethodAnnotation(SecurityParameter.class);
                //入参是否需要解密
                encode = serializedField.inDecode();
            }
            if (encode) {
                log.info("对方法method :【" + methodParameter.getMethod().getName() + "】返回数据进行解密");
                return new MyHttpInputMessage(inputMessage);
            } else {
                return inputMessage;
            }
        } catch (Exception e) {
            log.error("对方法method :【" + methodParameter.getMethod().getName() + "】返回数据进行解密出现异常：" + e.getMessage());
            return inputMessage;
        }
    }


    @Override
    public Object afterBodyRead(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }


    class MyHttpInputMessage implements HttpInputMessage {
        private HttpHeaders headers;
        private InputStream body;

        public MyHttpInputMessage(HttpInputMessage inputMessage) throws Exception {
            this.headers = inputMessage.getHeaders();
            this.body = IOUtils.toInputStream(easpString(IOUtils.toString(inputMessage.getBody(), "utf-8")));
        }

        @Override
        public InputStream getBody() throws IOException {
            return body;
        }

        @Override

        public HttpHeaders getHeaders() {
            return headers;
        }

        /**
         * @param requestData
         * @return
         */
        public String easpString(String requestData) {
            if (requestData != null && !requestData.equals("")) {
                Map<String, String> map = new Gson().fromJson(requestData, new TypeToken<Map<String, String>>() {
                }.getType());
                // 密文
                String data = map.get("requestData");
                // 加密的ase秘钥
                String encrypted = map.get("encrypted");
                if (StringUtils.isBlank(data) || StringUtils.isBlank(encrypted)) {
                    throw new BizException("参数【requestData】缺失异常！");
                } else {
                    String content = null;
                    String aseKey = null;
                    try {
                        aseKey = RSAUtils.decryptDataOnJava(encrypted, SERVER_PRIVATE_KEY);
                    } catch (Exception e) {
                        throw new BizException("参数【aseKey】解析异常！");
                    }
                    try {
                        content = AesEncryptUtils.decrypt(data, aseKey);
                    } catch (Exception e) {
                        throw new BizException("参数【content】解析异常！");
                    }
                    if (StringUtils.isBlank(content) || StringUtils.isBlank(aseKey)) {
                        throw new BizException("参数【requestData】解析参数空指针异常!");
                    }
                    return content;
                }
            }
            throw new BizException("参数【requestData】不合法异常！");
        }
    }
}

