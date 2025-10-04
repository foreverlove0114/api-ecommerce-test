package com.ecommerce.utils;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * 🛠️ HTTP客户端封装类 - 测试框架的"通信专家"
 * 职责：封装所有HTTP请求细节，提供简洁易用的API给测试步骤使用
 */
public class ApiClient {

    /**
     * 📝 HTTP请求规范对象 - 所有请求的配置基础
     * 包含认证信息、超时设置、内容类型等通用配置
     */
    private RequestSpecification request;

    /**
     * 🏗️ 构造函数 - 初始化HTTP客户端配置
     * 设置所有请求的通用参数，确保一致性
     */
    public ApiClient(){
        // 🌐 设置基础URL - 所有请求的起点
        RestAssured.baseURI = TestConfig.BASE_URL;

        // ⏱️ 配置超时设置 - 防止请求无限期挂起
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", TestConfig.TIMEOUT)  // 🔌 连接建立超时
                        .setParam("http.socket.timeout", TestConfig.TIMEOUT));    // 📡 数据传输超时

        this.request = RestAssured.given()
                .config(config)
                .contentType(ContentType.URLENC)
                .accept(ContentType.HTML)
                .redirects().follow(false)
                .urlEncodingEnabled(true);
    }

    /**
     * 🔐 会话管理 - 为请求添加Session Cookie
     * 实现链式调用，方便连续设置多个参数
     *
     * @param sessionId 会话ID，登录后获得的身份凭证
     * @return ApiClient 返回当前对象，支持链式调用
     */

    public ApiClient withSession(String sessionId){
        if(sessionId != null && !sessionId.isEmpty()){
            // 🍪 添加Session Cookie到请求头
            this.request = request.cookie("session",sessionId);
        }

        return this;// 🔗 返回this支持链式调用：apiClient.withSession(...).get(...)
    }

    /**
     * 📨 GET请求 - 发送HTTP GET请求
     *
     * @param endpoint 请求端点（不包含基础URL）
     * @return Response HTTP响应对象
     */
    public Response get(String endpoint){
        return request.get(endpoint);
    }

    /**
     * 📨 POST请求 - 发送带表单数据的HTTP POST请求
     *
     * @param 'endpoint' 请求端点
     * @param 'formParams' 表单参数键值对
     * @return Response HTTP响应对象
     */
    public Response post(String endpoint, Map<String,String> formParams){
        return request.formParams(formParams).post(endpoint);
    }

    /**
     * 🍪 提取Session Cookie - 从HTTP响应中获取会话标识
     * 主要用于登录后提取session供后续请求使用
     *
     * @param response HTTP响应对象
     * @return String session cookie值，如果不存在返回null
     */
    public String extractSessionCookie(Response response){
        return response.getCookie("session");
    }
}