package com.ecommerce.stepdefinitions;

import com.ecommerce.models.User;
import com.ecommerce.testdata.TestDataManager;
import com.ecommerce.utils.ApiClient;
import com.ecommerce.utils.TestConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 🎭 用户认证步骤定义类 - 处理所有用户注册、登录、登出相关的测试步骤
 * 这个类就像是测试剧本的"主演"，负责演出认证相关的所有戏份
 */
public class AuthSteps {
    private ApiClient apiClient; // 🎯 HTTP请求发送器 - 测试的"通信工具"
    private Response response; // 📄 存储最新HTTP响应 - 测试的"记忆单元"
    private String sessionId;  // 🔑 登录后的会话凭证 - 用户的"身份证明"
    private User testUser;  // 👤 测试用户数据 - 测试的"角色信息"

    /**
     * 🏗️ 构造函数 - 测试开始前的准备工作
     * 每次测试场景执行时，Cucumber都会创建新的AuthSteps实例
     */
    public AuthSteps(){
        this.apiClient = new ApiClient();
        this.testUser = TestDataManager.getDefaultUser();
    }

    /**
     * 📝 用户注册步骤 - 创建新的测试账户
     * 对应Gherkin: Given a new user is registered
     */
    @Given("a new user is registered")
    public void a_new_user_is_registered(){
        // 🎯 准备注册数据：将User对象转换为HTTP表单参数
        Map<String,String> formParams = createUserFormParams(testUser);
        System.out.println("=== REGISTERING USER ===");
        System.out.println("Email: " + testUser.getEmail());

        response = apiClient.post("/register", formParams);
        System.out.println("Registration status: " + response.getStatusCode());
        System.out.println("Registration headers: " + response.getHeaders());
        System.out.println("Registration cookies: " + response.getCookies());

        String responseBody = response.getBody().asString();
        int length = Math.min(responseBody.length(),500);
        System.out.println("Registration response (first" + length + " chars):" + responseBody.substring(0,length));
    }

    /**
     * ✅ 注册成功验证 - 检查用户注册是否成功
     * 对应Gherkin: Then the registration should be successful
     */
    @Then("the registration should be successful")
    public void the_registration_should_be_successful(){
        int statusCode = response.getStatusCode();
        System.out.println("=== REGISTRATION VERIFICATION ===");
        System.out.println("Status code: " + statusCode);

        // 🔍 分析响应内容
        String responseBody = response.getBody().asString();
        System.out.println("Response contains 'Registered Successfully': " + responseBody.contains("Registered Successfully"));
        System.out.println("Response contains 'Login': " + responseBody.contains("Login"));
        System.out.println("Response contains 'error': " + responseBody.toLowerCase().contains("error"));

        // 🎯 灵活验证：接受200或302状态码都算成功
        boolean success = statusCode == TestConfig.StatusCode.OK || statusCode == TestConfig.StatusCode.REDIRECT;
        if(success){
            System.out.println("✓ Registration completed successfully");
        }else{
            System.out.println("✗ Registration failed with status: " + statusCode);
        }
        Assert.assertTrue(success, "Registration should succeed with status 200 or 302");
    }

    /**
     * 🔐 有效凭证登录 - 使用正确的用户名密码登录
     * 对应Gherkin: When the user logs in with valid credentials
     */
    @When("the user logs in with valid credentials")
    public void the_user_logs_in_with_valid_credentials(){
        // 🎯 准备登录参数
        Map<String,String> formParams = new HashMap<>();
        formParams.put("email", testUser.getEmail());
        formParams.put("password", testUser.getPassword());

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Login email: " + testUser.getEmail());

        // 📨 发送登录请求
        response = apiClient.post("/login",formParams);
        sessionId = apiClient.extractSessionCookie(response);

        System.out.println("Login status: " + response.getStatusCode());
        System.out.println("Session cookie: " + sessionId);
        System.out.println("All cookies: " + response.getCookies());
    }

    /**
     * ✅ 登录成功验证 - 检查用户是否成功登录
     * 对应Gherkin: Then the login should be successful
     */
    @Then("the login should be successful")
    public void the_login_should_be_successful(){
        System.out.println("=== LOGIN VERIFICATION ===");
        System.out.println("Initial status code: " + response.getStatusCode());

        // 🎯 核心修复：手动跟随重定向（解决服务器重定向问题）
        Response finalResponse = response;

        if(response.getStatusCode() == TestConfig.StatusCode.REDIRECT){
            String location = getLocationHeader(response);
            System.out.println("Redirecting to: " + location);

            // 🎯 关键：手动访问重定向目标，获取真正的用户页面
            finalResponse = apiClient.withSession(sessionId).get(location);
            System.out.println("Final status code after redirect: " + finalResponse.getStatusCode());
        }

        // 🎯 现在检查的是最终页面的内容，不是重定向页面
        String finalResponseBody = finalResponse.getBody().asString();
        System.out.println("Final response length: " + finalResponseBody.length());

        // 🔍 分析页面内容，查找登录成功的特征
        boolean hasWelcome = finalResponseBody.contains("Welcome");
        boolean hasLogout = finalResponseBody.contains("Sign Out");

        System.out.println("Contains 'Welcome': " + hasWelcome);
        System.out.println("Contains 'Sign Out': " + hasLogout);

        Assert.assertNotNull(sessionId, "Session cookie should be set after login");
        Assert.assertFalse(sessionId.isEmpty(), "Session cookie should not be empty");

        boolean loginSuccess = hasWelcome && hasLogout;
        Assert.assertTrue(loginSuccess, "Should show logged-in indicators on the final page");

        System.out.println("✓ Login successful");
    }

    /**
     * ❌ 无效凭证登录 - 使用错误密码测试登录失败场景
     * 对应Gherkin: When the user logs in with invalid credentials
     */
    @When("the user logs in with invalid credentials")
    public void the_user_logs_in_with_invalid_credentials(){
        Map<String,String> formParams = new HashMap<>();
        formParams.put("email", testUser.getEmail());
        formParams.put("password","wrongpassword"); // 🎯 故意使用错误密码

        System.out.println("=== INVALID LOGIN ATTEMPT ===");

        response = apiClient.post("/login",formParams);
        System.out.println("Invalid login status: " + response.getStatusCode());
    }

    /**
     * ❌ 登录失败验证 - 检查使用错误密码时登录确实失败
     * 对应Gherkin: Then the login should fail
     */
    @Then("the login should fail")
    public void the_login_should_fail(){
        System.out.println("=== LOGIN FAILURE VERIFICATION ===");
        System.out.println("Status code: " + response.getStatusCode());

        // 🎯 核心修正：验证用户实际上没有登录，而不是session不存在
        String sessionAfterFailedLogin = apiClient.extractSessionCookie(response);
        System.out.println("Session detected: " + (sessionAfterFailedLogin != null));

        boolean loginFailed = false;
        String failureReason = "";

        if (response.getStatusCode() == TestConfig.StatusCode.REDIRECT){
            String location = getLocationHeader(response);
            System.out.println("Redirects to: " + location);

            // 🎯 访问重定向目标验证实际登录状态
            Response finalResponse = apiClient.withSession(sessionAfterFailedLogin).get(location);
            String finalBody = finalResponse.getBody().asString();

            // 🔍 检查是否仍然显示登录状态
            boolean showLoggedIn = finalBody.contains("Logout") && finalBody.contains("Welcome, ");
            System.out.println("Actually logged in: " + showLoggedIn);

            loginFailed = !showLoggedIn;
            if (!loginFailed) failureReason = "User appears to be logged in despite wrong password";

        }else {
            // 直接返回错误信息的情况,如果没有重新定向的话
            String responseBody = response.getBody().asString();
            boolean showsError = responseBody.contains("Invalid");

            System.out.println("Shows error message: " + showsError);

            loginFailed = showsError;
            if (!loginFailed) failureReason = "No error message shown for failed login";
        }

        Assert.assertTrue(loginFailed, "Login should fail: " + failureReason);
        System.out.println("✓ Login correctly failed");
    }

    /**
     * 🚪 用户登出 - 执行退出登录操作
     * 对应Gherkin: When the user logs out
     */
    @When("the user logs out")
    public void the_user_logs_out() {
        System.out.println("=== LOGOUT PROCESS ===");
        System.out.println("Session before logout: " +
                (sessionId != null ? sessionId.substring(0, Math.min(10, sessionId.length())) + "..." : "null"));

        response = apiClient.withSession(sessionId).get("/logout");

        System.out.println("Logout status: " + response.getStatusCode());

        // 🎯 修复session提取 - 处理空字符串情况
        String newSession = apiClient.extractSessionCookie(response);

        // 🎯 明确处理各种session状态
        if (newSession == null) {
            System.out.println("Session after logout request: CLEARED");
            sessionId = null;
        } else if (newSession.isEmpty()) {
            System.out.println("Session after logout request: CLEARED (empty string)");  // 🎯 明确显示
            sessionId = null;  // 🎯 空字符串也应该视为清除
        } else {
            System.out.println("Session after logout request: CHANGED: " +
                    newSession.substring(0, Math.min(10, newSession.length())) + "...");
            sessionId = newSession;
        }
    }

    /**
     * ✅ 登出成功验证 - 检查用户是否成功退出登录
     * 对应Gherkin: Then the user should be logged out
     */
    @Then("the user should be logged out")
    public void the_user_should_be_logged_out() {
        System.out.println("=== LOGOUT VERIFICATION ===");

        // ✅ 验证登出响应
        Assert.assertEquals(response.getStatusCode(), TestConfig.StatusCode.REDIRECT,
                "Logout should trigger redirect");

        String location = getLocationHeader(response);
        System.out.println("Redirects to: " + location);

        // 🎯 使用全新客户端验证
        ApiClient freshClient = new ApiClient();

        // ✅ 验证1: 首页显示登录选项
        Response homeResponse = freshClient.get("/");
        String homeBody = homeResponse.getBody().asString();

        boolean showsSignIn = homeBody.contains("Sign In");  // 🎯 明确检查"Sign In"
        boolean noUserMenu = !homeBody.contains("Hello,") && !homeBody.contains("Sign Out");

        System.out.println("Home page shows 'Sign In': " + showsSignIn);
        System.out.println("Home page hides user menu: " + noUserMenu);

        // ✅ 验证2: 不能访问个人资料页
        Response profileResponse = freshClient.get("/account/profile");
        boolean blockedFromProfile = profileResponse.getStatusCode() != 200;
        System.out.println("Blocked from profile page: " + blockedFromProfile);

        // ✅ 综合验证
        boolean logoutSuccess = showsSignIn && noUserMenu && blockedFromProfile;

        if (logoutSuccess) {
            System.out.println("✓ Logout successful - user properly signed out");
        } else {
            System.out.println("✗ Logout failed:");
            System.out.println("  - Shows Sign In: " + showsSignIn);
            System.out.println("  - Hides user menu: " + noUserMenu);
            System.out.println("  - Blocked from profile: " + blockedFromProfile);
        }

        Assert.assertTrue(logoutSuccess, "User should be completely logged out");
    }

    /**
     * 👤 访问个人资料页 - 测试登录后访问需要认证的页面
     * 对应Gherkin: When the user accesses the profile page
     *
     * 🎯 这个步骤验证：已登录用户能否成功访问受保护的资料页面
     * 这是认证测试的关键环节，确认session有效且权限正确
     */
    @When("the user accesses the profile page")
    public void the_user_accesses_the_profile_page(){
        System.out.println("=== ACCESSING PROFILE PAGE ===");

        // 🎯 使用当前session访问需要认证的个人资料页面
        // apiClient.withSession(sessionId) - 携带登录凭证
        // .get("/account/profile") - 访问受保护的个人资料端点
        response = apiClient.withSession(sessionId).get("/account/profile");

        // 🔍 记录访问结果状态码，用于后续验证
        // 期望：200 OK (成功访问)
        // 异常：302 Redirect (未登录重定向) 或 403 Forbidden (权限不足)
        System.out.println("Profile page status: " + response.getStatusCode());
    }

    /**
     * ✅ 个人资料页访问验证 - 确认用户可以正常访问个人资料
     * 对应Gherkin: Then the profile page should be accessible
     *
     * 🎯 这个验证确保：
     * 1. 服务器返回成功状态码 (200)
     * 2. 页面内容包含个人资料相关信息
     * 3. 用户确实看到了个人资料界面而非错误页面
     */
    @Then("the profile page should be accessible")
    public void the_profile_page_should_be_accessible(){
        System.out.println("=== PROFILE PAGE VERIFICATION ===");

        // ✅ 验证1: HTTP状态码检查
        // 个人资料页面应该返回200状态码，表示成功访问
        // 如果返回302表示被重定向（未登录），404表示页面不存在
        Assert.assertEquals(response.getStatusCode(),TestConfig.StatusCode.OK);

        // 📄 获取响应体内容用于详细分析
        String profileBody = response.getBody().asString();

        // 🔍 验证2: 页面内容基础检查
        // 检查响应中是否包含"profile"关键词（不区分大小写）
        // 这是页面类型的快速验证
        System.out.println("Profile page contains user info: " +
                profileBody.toLowerCase().contains("profile"));

        // 🎯 验证3: 详细内容验证
        // 检查页面是否包含个人资料页面的特征元素：
        // - "Profile" - 页面标题或主导航
        // - "View Profile" - 查看资料功能
        // - "Edit Profile" - 编辑资料功能
        // 这些是个人资料页面的典型UI元素
        boolean profileAccessible = profileBody.contains("Profile") &&
                profileBody.contains("View Profile") &&
                profileBody.contains("Edit Profile");

        // ✅ 最终断言：页面应该可访问且显示正确的个人资料内容
        // 如果失败，说明用户虽然能访问端点，但看到的不是预期的个人资料页面
        Assert.assertTrue(profileAccessible, "Profile page should be accessible to logged-in user - " +
                "missing expected profile elements. Page contains: " +
                "Profile=" + profileBody.contains("Profile") + ", " +
                "View Profile=" + profileBody.contains("View Profile") + ", " +
                "Edit Profile=" + profileBody.contains("Edit Profile"));

        System.out.println("✓ Profile page accessible - user can view their profile");
    }


// ==================== 辅助方法区域 ====================

    /**
     * 🔄 创建用户表单参数 - 将User对象转换为HTTP表单参数
     *
     * @param 'user' User对象
     * @return Map<String, String> HTTP表单参数
     */
    private Map<String,String> createUserFormParams(User user){
        Map<String,String> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("password", user.getPassword());
        params.put("firstName", user.getFirstName());
        params.put("lastName", user.getLastName());
        params.put("address1", user.getAddress1());
        params.put("address2", user.getAddress2());
        params.put("zipcode", user.getZipcode());
        params.put("city", user.getCity());
        params.put("state", user.getState());
        params.put("country", user.getCountry());
        params.put("phone", user.getPhone());
        return params;
    }

    /**
     * 📍 获取重定向地址 - 从HTTP响应中提取Location头
     * 处理header名称大小写不敏感的问题
     *
     * @param response HTTP响应对象
     * @return String 重定向地址，如果不存在返回null
     */
    private String getLocationHeader(Response response) {
        // 处理header名称大小写不敏感的问题
        io.restassured.http.Headers headers = response.getHeaders();
        for (io.restassured.http.Header header : headers) {
            if ("location".equalsIgnoreCase(header.getName())) {
                return header.getValue();
            }
        }
        return null;
    }

}