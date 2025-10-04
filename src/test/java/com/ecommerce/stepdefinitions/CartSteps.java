package com.ecommerce.stepdefinitions;

import com.ecommerce.models.User;
import com.ecommerce.testdata.TestDataManager;
import com.ecommerce.utils.ApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class CartSteps {
    private ApiClient apiClient;
    private String sessionId;
    private Response response;
    private User testUser;
    private String productId;

    public CartSteps(){
        this.apiClient = new ApiClient();
        this.testUser = TestDataManager.getDefaultUser();
    }

    @Given("a logged-in user")
    public void a_logged_in_user() {
        // 注册用户
        registerUser();

        // 登录用户
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", testUser.getEmail());
        loginParams.put("password", testUser.getPassword());

        Response loginResponse = apiClient.post("/login", loginParams);
        sessionId = apiClient.extractSessionCookie(loginResponse);
        Assert.assertNotNull(sessionId, "Session should be created after login");
        System.out.println("✓ User logged in successfully");
    }

    @Given("a product is available")
    public void a_product_is_available(){
        // 使用固定商品ID，简化测试
        this.productId = "1";
        System.out.println("✓ Using product ID: " + productId);
    }

    @When("the user adds a product to the cart")
    public void the_user_adds_a_product_to_the_cart() {
        response = apiClient.withSession(sessionId).get("/addToCart?productId=" + productId);
        System.out.println("Add to cart response status: " + response.getStatusCode());

        if(response.getStatusCode() == 302){
            String location = getLocationHeader(response);
            System.out.println("Redirecting to " + location);

            Response finalResponse = apiClient.withSession(sessionId).get(location);
            this.response = finalResponse;

            System.out.println("Final page status: " + finalResponse.getStatusCode());
        }
    }

    @Then("the product should be added successfully")
    public void the_product_should_be_added_successfully(){
        System.out.println("Validation of Items Added");

        // 验证响应状态码
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200, "Add to cart should succeed");

        String responseBody = response.getBody().asString();

        // 简单验证：检查购物车存在且有商品（数量>=1）
        boolean cartValid = responseBody.contains("cartIcon") &&
                responseBody.contains("CART") &&
                extractCartCount(responseBody) >= 1;

        Assert.assertTrue(cartValid, "Cart should show at least 1 item after adding product");
        System.out.println("✓ Product added to cart successfully");
    }

    @When("the user views the cart")
    public void the_user_views_the_cart(){
        response = apiClient.withSession(sessionId).get("/cart");
    }

    @Then("the cart should be accessible")
    public void the_cart_should_be_accessible(){
        Assert.assertEquals(response.getStatusCode(),200);
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Proceed to checkout"));
        System.out.println("ResponseBody includes Proceed to checkout: " + responseBody.contains("Proceed to checkout"));
        System.out.println("✓ Cart page accessible");
    }

    @When("the user removes a product from the cart")
    public void the_user_removes_a_product_from_the_cart(){
        response = apiClient.withSession(sessionId).get("/removeFromCart?productId=" + productId);
        System.out.println("Remove from cart response: " + response.getStatusCode());

        if(response.getStatusCode() == 302){
            String location = getLocationHeader(response);
            System.out.println("Redirecting to " + location);

            Response finalResponse = apiClient.withSession(sessionId).get(location);
            this.response = finalResponse;

            System.out.println("Final page status: " + finalResponse.getStatusCode());
        }
    }

    @Then("the product should be removed successfully")
    public void the_product_should_be_removed_successfully(){
        Assert.assertEquals(response.getStatusCode(),200);
        String responseBody = response.getBody().asString();

        System.out.println("ResponseBody includes CART 0: " + responseBody.contains("CART 0"));
        Assert.assertTrue(responseBody.contains("CART 0"));

        System.out.println("✓ Product removed successfully");
    }

    @Then("the cart should be empty")
    public void the_cart_should_be_empty(){
        Assert.assertEquals(response.getStatusCode(), 200);
        String ResponseBody = response.getBody().asString();

        System.out.println(ResponseBody.contains("$0"));
        Assert.assertTrue(ResponseBody.contains("$0"));
        System.out.println("✓ Empty cart verified");
    }

    @When("an unauthorized user views the cart")
    public void an_unauthorized_user_views_the_cart() {
        response = apiClient.get("/cart");
    }

    @Then("the user should be redirected to login")
    public void the_user_should_be_redirected_to_login(){
        Assert.assertTrue(response.getStatusCode()==302);

        String location = getLocationHeader(response);
        System.out.println("Redirecting to: " + location);

        Response redirectResponse = apiClient.withSession(sessionId).get(location);
        this.response = redirectResponse;

        Assert.assertTrue(response.getStatusCode()==200);
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Register here"));
        System.out.println("✓ Unauthorized access handled correctly");
    }

// ==================== 辅助方法区域 ====================

    private void registerUser() {
        Map<String, String> registerParams = new HashMap<>();
        registerParams.put("email", testUser.getEmail());
        registerParams.put("password", testUser.getPassword());
        registerParams.put("firstName", testUser.getFirstName());
        registerParams.put("lastName", testUser.getLastName());
        registerParams.put("address1", testUser.getAddress1());
        registerParams.put("address2", testUser.getAddress2());
        registerParams.put("zipcode", testUser.getZipcode());
        registerParams.put("city", testUser.getCity());
        registerParams.put("state", testUser.getState());
        registerParams.put("country", testUser.getCountry());
        registerParams.put("phone", testUser.getPhone());

        Response registerResponse = apiClient.post("/register", registerParams);
        Assert.assertTrue(registerResponse.getStatusCode() == 200 ||
                registerResponse.getStatusCode() == 302);
    }

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

    private int extractCartCount(String responseBody) {
        try {
            String cartPattern = "CART (\\d+)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(cartPattern);
            java.util.regex.Matcher matcher = pattern.matcher(responseBody);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            System.out.println("Error extracting cart count: " + e.getMessage());
        }
        return 0;
    }
}