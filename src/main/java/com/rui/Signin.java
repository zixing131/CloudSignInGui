package com.rui;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rui.util.HttpRequestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 所属项目:pa
 *
 * @author rui10038 邮箱：2450782689@qq.com
 * @version 1.0
 * //                .-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * //            __.'              ~.   .~              `.__
 * //          .'//    JAVA无涯      \./     回头是岸     \\`.
 * //        .'//                     |                     \\`.
 * //      .'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * //    .'//.-"                 `-.  |  .-'                 "-.\\`.
 * //  .'//______.============-..   \ | /   ..-============.______\\`.
 * //.'______________________________\|/______________________________`.
 * @date 2020/2/7 -上午 8:49
 **/

public class Signin {
    public static final Pattern TBS = Pattern.compile(".tbs.:\\s*\"\\S*");
    public static final String TIEBA_SEED_URL = "http://tieba.baidu.com/f/like/mylike?pn=1";
    private static final Log log = LogFactory.get();
    public ArrayList<String> backToList = new ArrayList<>();
    LinkedHashMap<String, String> headers = null;
    private String cookie;

    public Signin(String cookie) {
        this.cookie = cookie.replace("Cookie: ", "").trim();
    }

    public Signin() {
    }
    public ArrayList<String> getBackToList() {
        return backToList;
    }

    /**
     * 将用户复制的请求头信息转换为linkedhashmap令程序可识别
     *
     * @param str 用户复制的请求头信息
     * @return 包含所有请求头信息的linkedhashmap
     */
    public static LinkedHashMap<String, String> resolveHeadsToMap(String str) {
        String[] split = str.split("\\n");
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        for (String s : split) {
            String[] strings = s.split(": ");
            Console.log(strings);
            if (Objects.equals(strings[0], "Content-Length")) {
                continue;
            }
            linkedHashMap.put(strings[0], strings[1]);
        }
        log.info(linkedHashMap.toString());
        return linkedHashMap;
    }

    /**
     * 将用户复制的post参数转换为linkedhashmap令程序可识别
     *
     * @param str 复制的post参数
     * @return 包含所有post参数的linkedhashmap
     */
    public static LinkedHashMap<String, String> resolveArgsToMap(String str) {
        String[] strings = str.split("&");
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        for (String string : strings) {
            String[] split = string.split("=");
            if (split.length >= 2) {
                linkedHashMap.put(split[0], split[1]);
            } else {
                linkedHashMap.put(split[0], null);
            }

        }
        return linkedHashMap;
    }

    /**
     * 发送post请求
     *
     * @param url     签到链接
     * @param headers 头标识
     * @param args    参数
     * @return 返回值
     */
    public static String post(String url, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> args) {
        try (CloseableHttpClient httpClient = HttpRequestUtils.getHttpClient()) {
            //第一步 配置 Post 请求 Url
            HttpPost httpPost = new HttpPost(url);
//           第二步 配置请求头
            if (Objects.nonNull(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 装配post请求参数
            if (Objects.nonNull(args)) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> entry : args.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
            }
            //第二步 创建一个自定义的 response handler
            ResponseHandler<String> responseHandler = response -> {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
            };
            //第三步 执行请求
            String responseBody = httpClient.execute(httpPost, responseHandler);
            if (Objects.nonNull(responseBody)) {
                return responseBody;
            } else {
                log.info("异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送get请求
     *
     * @param url     签到链接
     * @param headers 头标识
     * @return 返回值
     */
    public static String get(String url, LinkedHashMap<String, String> headers) {
        try (CloseableHttpClient httpclient = HttpRequestUtils.getHttpClient()) {
            //第一步 配置 get 请求 Url
            HttpGet httpGet = new HttpGet(url);
//           第二步 配置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            //第二步 创建一个自定义的 response handler
            ResponseHandler<String> responseHandler = (HttpResponse response) -> {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
            };

            //第三步 执行请求
            String responseBody = httpclient.execute(httpGet, responseHandler);
            if (Objects.nonNull(responseBody)) {
                return responseBody;

            } else {
                log.info("异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
