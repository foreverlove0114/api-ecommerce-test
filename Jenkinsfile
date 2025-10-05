pipeline {
    agent any
    triggers {
            cron('H */4 * * 1-5')
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