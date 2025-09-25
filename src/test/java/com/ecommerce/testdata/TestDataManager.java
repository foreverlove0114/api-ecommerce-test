package com.ecommerce.testdata;

import com.ecommerce.models.User;
import com.ecommerce.utils.TestConfig;

public class TestDataManager {

    public static User getDefaultUser() {
        User user = new User();
        user.setEmail(TestConfig.TestUser.EMAIL);
        user.setPassword(TestConfig.TestUser.PASSWORD);
        user.setFirstName(TestConfig.TestUser.FIRST_NAME);
        user.setLastName(TestConfig.TestUser.LAST_NAME);
        user.setAddress1(TestConfig.TestUser.ADDRESS1);
        user.setAddress2(TestConfig.TestUser.ADDRESS2);
        user.setZipcode(TestConfig.TestUser.ZIPCODE);
        user.setCity(TestConfig.TestUser.CITY);
        user.setState(TestConfig.TestUser.STATE);
        user.setCountry(TestConfig.TestUser.COUNTRY);
        user.setPhone(TestConfig.TestUser.PHONE);
        return user;
    }

    public static User getUpdatedUser() {
        User user = new User();
        user.setEmail(TestConfig.TestUser.EMAIL);
        user.setFirstName("JieXiang");
        user.setLastName("Yu");
        user.setAddress1("Yong");
        user.setAddress2("Peng");
        user.setZipcode("83700");
        user.setCity("Yong Peng");
        user.setState("Johor");
        user.setCountry("Malaysia");
        user.setPhone("011101111");
        return user;
    }
}