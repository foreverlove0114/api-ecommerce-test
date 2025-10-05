package com.ecommerce.models;
//推送新代码测试
/**
 * 🧑‍💼 用户模型类 - 代表电商系统中的用户实体
 * 这个类封装了用户的所有基本信息，用于测试数据的管理和传递
 */
public class User {
    // 📝 用户属性定义 - 使用private封装保证数据安全
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String zipcode;
    private String city;
    private String state;
    private String country;
    private String phone;

    /**
     * 🔧 默认构造器 - 创建空用户对象
     * 用途：当需要逐步设置用户属性时使用
     * 示例：User user = new User(); user.setEmail("test@example.com");
     */
    public User(){}

    /**
     * 🔧 带参数构造器 - 快速创建基本用户信息
     * 用途：注册或登录时只需要基本信息的场景
     * @param email 用户邮箱
     * @param password 用户密码
     * @param firstName 名字
     * @param lastName 姓氏
     */
    public User(String email, String password, String firstName, String lastName){
        // 🎯 使用this区分成员变量和参数
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // 🔄 Getter和Setter区域 - 提供受控的属性访问
    // 📤 Getter - 从对象中"获取"数据
    public String getEmail() { return email; } // 🎯 返回String类型的数据
    // 📥 Setter - 向对象中"设置"数据
    public void setEmail(String email) { this.email = email; }// 🎯 不返回任何数据（void）

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
