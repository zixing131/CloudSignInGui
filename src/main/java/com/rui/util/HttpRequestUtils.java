package com.rui.util;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
 * @date 2020/2/8 -下午 10:56
 **/
public class HttpRequestUtils {
    private static PoolingHttpClientConnectionManager connectionManager;
    static {
        connectionManager = new PoolingHttpClientConnectionManager();
        // 整个连接池最大连接数
        connectionManager.setMaxTotal(1000);
        // 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
        connectionManager.setValidateAfterInactivity(50000);
        // 每路由最大连接数，默认值是200
        connectionManager.setDefaultMaxPerRoute(1000);
    }
    /**
     * 获取Http客户端连接对象
     *
     * @return Http客户端连接对象
     */
    public static CloseableHttpClient getHttpClient() {
        // 创建Http请求配置参数
        RequestConfig requestConfig = RequestConfig.custom()
                // 连接超时时间
                .setConnectionRequestTimeout(60000)
                // 请求超时时间
                .setConnectTimeout(60000)
                //默认允许自动重定向
                .setRedirectsEnabled(true)
                // 响应超时时间
                .setSocketTimeout(60000)
                .build();
//        防止  Remote host closed connection during handshake
//设置https协议访问
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");


        /**
         * 测出超时重试机制为了防止超时不生效而设置
         *  如果直接放回false,不重试
         *  这里会根据情况进行判断是否重试
         */

        HttpRequestRetryHandler retry = (exception, executionCount, context) -> {
            System.out.println("请求错误,正在重试,已重试次数为"+executionCount);
            if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }if (exception instanceof SocketTimeoutException) {// 超时
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return true;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {// ssl异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };

        // 创建httpClient
        return HttpClients.custom()
                // 把请求相关的超时信息设置到连接客户端
                .setDefaultRequestConfig(requestConfig)
//                设置连接管理器
                .setConnectionManager(connectionManager)
                // 把请求重试设置到连接客户端
                .setRetryHandler(retry)
                .setConnectionManagerShared(true)
                .build();
    }

}
