package com.ecommerce.testdata;

import com.ecommerce.models.User;
import com.ecommerce.utils.TestConfig;

/**
 * 🏭 测试数据管理类 - 测试数据的"工厂"
 * 职责：集中管理所有测试用例需要的数据，保证测试数据的一致性和可维护性
 */
public class TestDataManager {

    /**
     * 🎯 获取默认测试用户 - 测试数据的"标准模板"
     *
     * 📝 方法签名解析：
     * public static User getDefaultUser()
     *   ↑       ↑     ↑        ↑
     *   访问修饰符 静态关键字 返回类型  方法名
     *
     * @return User 返回一个完全配置好的User对象，包含所有必要的测试数据
     *
     * 🏗️ 这个方法的作用：
     * 1. 创建标准的测试用户对象
     * 2. 设置所有必要的用户属性
     * 3. 返回可以直接使用的测试数据
     */

    public static User getDefaultUser(){
        // 🆕 创建新的User对象实例 - 空的"用户表格"
        User user = new User();

        // 📝 使用setter方法填充用户数据 - 从TestConfig获取预设值
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

        // 📤 返回完全配置好的User对象 - "成品用户
        return user;
    }
}