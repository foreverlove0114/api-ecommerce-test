pipeline {
    agent any

    parameters {
        choice(
            name: 'BASE_URL',
            choices: ['http://localhost:5000/', 'http://staging.example.com/', 'http://prod.example.com/'],
            description: 'Target environment'
        )
        string(
            name: 'TIMEOUT',
            defaultValue: '10000',
            description: 'Request timeout in milliseconds'
        )
        booleanParam(
            name: 'RUN_IN_PARALLEL',
            defaultValue: false,
            description: 'Run tests in parallel'
        )
    }

    triggers {
        // 代码推送触发：每2分钟检查一次代码是否有变更
        pollSCM('H/2 * * * *')
        // 定时构建触发：在北京时间每周一至周五上午8点到下午6点，每4小时的第45分钟构建一次
        cron('45 0-10/4 * * 1-5') // 注意：Jenkins默认使用UTC时间
    }

    tools {
        maven 'M3'
        jdk 'JDK11'
    }

    environment {
        // 使用参数化构建中的参数
        BASE_URL = "${params.BASE_URL}"
        TIMEOUT = "${params.TIMEOUT}"
        RUN_IN_PARALLEL = "${params.RUN_IN_PARALLEL}"
    }

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
                    if (env.RUN_IN_PARALLEL == 'true') {
                        bat 'mvn test -Dtest.parallel=true'
                    } else {
                        bat 'mvn test'
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
                echo "Testing against: ${BASE_URL}"
                echo "Timeout: ${TIMEOUT}ms"
                echo "Parallel execution: ${RUN_IN_PARALLEL}"
            }
        }
    }

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