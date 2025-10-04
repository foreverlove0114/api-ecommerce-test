package com.ecommerce.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;


/**
 * 测试运行器 - 整个测试框架的启动入口
 * 继承AbstractTestNGCucumberTests来集成Cucumber和TestNG
 */
@CucumberOptions(
        // 🗺️ 指定Feature文件的位置 - Cucumber测试场景的"剧本"
        features = "src/test/resources/features",
        // 🎭 指定Step Definitions的包路径 - 测试步骤的"演员休息室"
        glue = "com.ecommerce.stepdefinitions",
        // 📊 配置测试报告插件 - 测试结果的"记录设备"
        plugin = {
                "pretty", // 控制台美化输出
                "html:target/cucumber-reports/cucumber.html", // 生成HTML格式测试报告
                "json:targer/cucumber-reports/cucumber.json" // 生成JSON格式测试报告（用于CI/CD集成）
        },

        // 🎨 控制台输出模式 - 确保在不同终端显示一致
        monochrome = true // 设置为true避免控制台显示乱码
)


public class TestRunner extends AbstractTestNGCucumberTests {
    /**
     * 🎯 重写scenarios方法 - TestNG数据提供者
     * 这个方法告诉TestNG有哪些Cucumber测试场景需要执行
     *
     * @DataProvider(parallel = false) - 控制测试执行方式：
     *   - parallel = true: 并行执行测试，速度快但可能不稳定
     *   - parallel = false: 顺序执行测试，速度慢但稳定可靠
     *
     * @return 返回所有需要执行的测试场景数据
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios(){
        // 🔄 调用父类实现 - 自动扫描并返回所有Feature文件中的测试场景
        // 父类已经实现了复杂的场景发现和数据处理逻辑
        return super.scenarios();
    }


}