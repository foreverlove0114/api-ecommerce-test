pipeline {
    agent any
    triggers {
            // 代码推送触发：每2分钟检查一次代码是否有变更:cite[8]
            pollSCM('H/2 * * * *')
            // 定时构建触发：在北京时间每周一至周五上午8点到下午6点，每4小时的第45分钟构建一次:cite[4]
            cron('45 0-10/4 * * 1-5') // 注意：Jenkins默认使用UTC时间
        }
        stages {
            stage('Example Build') {
                steps {
                    echo 'Hello, World!'
                }
            }
        }

    tools {
        maven 'M3'
        jdk 'JDK11'
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
                bat 'mvn test'
            }
            post {
                always {
                    cucumber reportTitle: 'Cucumber Report',
                        fileIncludePattern: '**/cucumber.json',
                        jsonReportDirectory: 'target/cucumber-reports'
                }
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
        }
    }
}

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

// triggers {
//     pollSCM('H/5 * * * *')  // 每5分钟检查代码变更
//     cron('0 2 * * *')       // 每天凌晨2点执行
// }