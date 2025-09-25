package com.ecommerce.utils;

public class TestConfig {
    public static final String BASE_URL = "http://localhost:5000/";
    public static final int TIMEOUT = 10000; // 10 seconds

    // 测试用户数据
    public static class TestUser {
        public static final String EMAIL = "testuser@example.com";
        public static final String PASSWORD = "testpass123";
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

    // 响应状态码常量
    public static class StatusCode {
        public static final int OK = 200;
        public static final int REDIRECT = 302;
        public static final int NOT_FOUND = 404;
    }
}