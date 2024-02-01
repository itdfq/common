package com.itdfq.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.itdfq.common.Exception.BizException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author duanfangqin 2022/9/23 17:37
 * @implNote
 */
@Slf4j
public class OkHttpUtils {

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * 不适用ssl证书验证的okHttp
     */
    private static final OkHttpClient notSSLClient = getNotSSLClient();

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    // 不验证客户端证书
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    // 不验证服务器证书
                }
            }
    };






    private static OkHttpClient getNotSSLClient()  {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            // 不验证客户端证书
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            // 不验证服务器证书
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 同步get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return String
     */
    public static <T> T syncGet(String url, Map<String, String> queries, Class<T> responseClass) throws IOException, BizException {
        StringBuilder sb = new StringBuilder(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = !url.contains("?");
            // 拼参
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                if (firstFlag) {
                    sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    firstFlag = false;
                    continue;
                }
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            ParserConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            return JSON.parseObject(responseBody, responseClass);
        }
        String message = response.body().string();
        log.error("OkHttpUtil调用api失败,url:{},response:{}", url, message);
        throw new BizException("响应失败,code:[" + response.code() + "],message:[" + message + "]");
    }


    /**
     * post 上传一个附件文件
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static <T> T syncFormOneFilePost(String url, File file, Map<String, String> params, String formFileName, Class<T> responseClass) throws IOException, BizException {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 根据file的后缀进行mediaType的创建..
        Optional<MediaType> mediaTypeHttp = MediaTypeFactory.getMediaType(file.getName());
        String contentType = mediaTypeHttp.orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(contentType);
        // 把文件封装进请求体
        RequestBody fileBody = RequestBody.create(mediaType, file);
        RequestBody body = builder
                .setType(MultipartBody.FORM)
                // form 表单中file的name  file:123.jpeg
                .addFormDataPart(formFileName, file.getName(), fileBody)
                .build();
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(body);
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                post.addHeader(key, params.get(key));
            }
        }
        Response response = null;
        response = okHttpClient.newCall(post.build()).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            ParserConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            return JSON.parseObject(responseBody, responseClass);
        }
        String message = response.body().string();
        throw new BizException("响应失败,code:[" + response.code() + "],message:[" + message + "]");
    }


    public static <T> T syncHeadersAndJson(String url, Map<String, String> headersMap, Map<String, String> queries, String jsonStr, String methodName, Class<T> responseClass) throws Exception {
        StringBuilder sb = new StringBuilder(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = !url.contains("?");
            // 拼参
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                if (firstFlag) {
                    sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    firstFlag = false;
                    continue;
                }
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        if (StringUtils.isBlank(jsonStr)){
            jsonStr="";
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonStr);
        Headers.Builder header = new Headers.Builder();
        if (Objects.nonNull(headersMap) && !headersMap.isEmpty()) {
            headersMap.forEach(header::add);
        }
        Request request = new Request.Builder()
                .url(sb.toString())
                .method(methodName, body)
                .headers(header.build())
                .build();
        Response res = okHttpClient.newCall(request).execute();
        if (res.isSuccessful()) {
            String responseBody = res.body().string();
            ParserConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            return JSON.parseObject(responseBody, responseClass);
        }
        String message = res.body().string();
        log.error("OkHttpUtil调用api失败,url:{},headers:{},,response:{}", request.url(), request.headers().toString(), message);
        throw new BizException("响应失败,code:[" + res.code() + "],message:[" + message + "]");
    }

}
