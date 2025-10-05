pipeline {
    agent any // 在任何可用的Jenkins节点上运行

    // 2. 构建参数（用户输入）
    parameters {
        choice( // 下拉选择框
            name: 'BASE_URL',
            choices: ['http://localhost:5000/', 'http://staging.example.com/', 'http://prod.example.com/'],
            description: 'Target environment'
        )
        string(   // 文本输入框
            name: 'TIMEOUT',
            defaultValue: '10000',
            description: 'Request timeout in milliseconds'
        )
        booleanParam( // 复选框
            name: 'RUN_IN_PARALLEL',
            defaultValue: false,
            description: 'Run tests in parallel'
        )
    }

 // 3. 自动触发条件
    triggers {
        pollSCM('H/15 * * * *')
        // 定时构建触发：在北京时间每周一至周五上午8点到下午6点，每4小时的第45分钟构建一次
        cron('45 0-10/4 * * 1-5') // 注意：Jenkins默认使用UTC时间
    }

// 4. 工具配置
    tools {
        maven 'M3' // 使用Jenkins中配置的Maven
        jdk 'JDK11' // 使用Jenkins中配置的JDK11
    }

   // 5. 环境变量
    environment {
        // 使用参数化构建中的参数
        BASE_URL = "${params.BASE_URL}"
        TIMEOUT = "${params.TIMEOUT}"
        RUN_IN_PARALLEL = "${params.RUN_IN_PARALLEL}"
    }

    // 6. 构建阶段
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/foreverlove0114/api-ecommerce-test'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'  // Windows 使用 bat
            }
        }

        stage('Test') {
            steps {
                    script {
                        // 构建Maven命令参数
                        def mavenCommand = "mvn test -Dbase.url=${BASE_URL} -Dtimeout=${TIMEOUT}"

                        if (env.RUN_IN_PARALLEL == 'true') {
                            bat "${mavenCommand} -Dtest.parallel=true"
                        } else {
                            bat "${mavenCommand}"
                        }
                    }
                }
            post {
                always {
                    cucumber reportTitle: 'Cucumber Report',
                        fileIncludePattern: '**/cucumber.json',
                        jsonReportDirectory: 'target/cucumber-reports'
                }
            }
        }

        stage('Example Build') {
            steps {
                echo 'Hello, World!'
                echo "Testing against: ${BASE_URL}"      // 显示: http://staging.example.com/
                echo "Timeout: ${TIMEOUT}ms"            // 显示: 15000ms
                echo "Parallel execution: ${RUN_IN_PARALLEL}" // 显示: false
            }
        }
    }

    // 7. 构建后处理
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/cucumber-reports',
                reportFiles: 'cucumber.html',
                reportName: 'Cucumber HTML Report'
            ])

            // 可选：清理工作空间
            cleanWs()
        }
        success {
            echo '构建成功！所有测试通过。'
        }
        failure {
            echo '构建失败！请检查测试报告。'
        }
    }
}

//testing