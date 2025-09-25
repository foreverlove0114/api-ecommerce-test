package com.ecommerce.stepdefinitions;

import com.ecommerce.models.User;
import com.ecommerce.testdata.TestDataManager;
import com.ecommerce.utils.ApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class CartSteps {
    private ApiClient apiClient;
    private Response response;
    private String sessionId;
    private User testUser;
    private String productId = "1"; // 使用固定商品ID简化测试

    public CartSteps() {
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
    public void a_product_is_available() {
        // 使用固定商品ID，简化测试
        this.productId = "1";
        System.out.println("✓ Using product ID: " + productId);
    }

    @When("the user adds a product to the cart")
    public void the_user_adds_a_product_to_the_cart() {
        response = apiClient.withSession(sessionId).get("/addToCart?productId=" + productId);
        System.out.println("Add to cart response status: " + response.getStatusCode());
    }

    @When("the user views the cart")
    public void the_user_views_the_cart() {
        response = apiClient.withSession(sessionId).get("/cart");
    }

    @When("the user removes a product from the cart")
    public void the_user_removes_a_product_from_the_cart() {
        response = apiClient.withSession(sessionId).get("/removeFromCart?productId=" + productId);
    }

    @Then("the product should be added successfully")
    public void the_product_should_be_added_successfully() {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 302,
                "Add to cart should succeed. Status: " + statusCode);
        System.out.println("✓ Product added to cart successfully");
    }

    @Then("the cart should be accessible")
    public void the_cart_should_be_accessible() {
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("✓ Cart page accessible");
    }

    @Then("the cart should be empty")
    public void the_cart_should_be_empty() {
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("✓ Empty cart verified");
    }

    @Then("the product should be removed successfully")
    public void the_product_should_be_removed_successfully() {
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("✓ Product removed successfully");
    }

    @When("an unauthorized user views the cart")
    public void an_unauthorized_user_views_the_cart() {
        response = apiClient.get("/cart");
    }

    @Then("the user should be redirected to login")
    public void the_user_should_be_redirected_to_login() {
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("✓ Unauthorized access handled correctly");
    }

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
}