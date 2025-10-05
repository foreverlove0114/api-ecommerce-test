package com.ecommerce.utils;

/**相关的配置参数
 * 这个类就像是测试框架的"控制面
 * ⚙️ 测试配置中心 - 集中管理所有测试板"，所有重要的设置都在这里定义
 */

public class TestConfig {
    /**
     * 🌐 基础URL - 被测系统的入口地址
     * 所有HTTP请求都会基于这个URL发送
     */
    // 从环境变量读取配置，支持CI/CD动态配置
    public static final String BASE_URL = System.getProperty("base.url",
            System.getenv().getOrDefault("BASE_URL", "http://localhost:5000/"));

    /**
     * ⏱️ 超时时间 - HTTP请求的最大等待时间（毫秒）
     * 防止测试因网络问题无限期挂起
     */
    public static final int TIMEOUT = Integer.parseInt(
            System.getProperty("timeout",
                    System.getenv().getOrDefault("TIMEOUT", "10000")));

    /**
     * 👤 测试用户数据配置 - 封装所有测试用户的预设信息
     * 使用静态内部类组织相关配置，提高代码可读性
     */
    public static class TestUser{
        public static final String EMAIL = generateTestEmail();
        public static final String PASSWORD = "testpass1234";
        public static final String FIRST_NAME = "Test";
        public static final String LAST_NAME = "User";
        public static final String ADDRESS1 = "123 Test St";
        public static final String ADDRESS2 = "Apt 1";
        public static final String ZIPCODE = "12345";
        public static final String CITY = "TestCity";
        public static final String STATE = "TS";
        public static final String COUNTRY = "TestCountry";
        public static final String PHONE = "1234567890";
    }

    private static String generateTestEmail() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "testuser_" + timestamp + "@example.com";
    }

    /**
     * 📊 HTTP状态码常量 - 统一管理响应状态码的语义化常量
     * 避免在代码中直接使用魔法数字，提高可读性和可维护性
     */
    public static class StatusCode{
        /** ✅ 请求成功 - 服务器已成功处理请求 */
        public static final int OK = 200;

        /** 🔄 重定向 - 请求需要进一步操作，通常需要跟随Location头 */
        public static final int REDIRECT = 302;

        /** ❌ 未找到 - 请求的资源不存在 */
        public static final int NOT_FOUND = 404;
    }
}