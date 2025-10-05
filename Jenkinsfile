pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK11'
    }

    environment {
        BASE_URL = 'http://localhost:5000/'
        TIMEOUT = '10000'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/your-username/your-ecommerce-test-project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    cucumber reportTitle: 'Cucumber Report',
                        fileIncludePattern: '**/cucumber.json',
                        jsonReportDirectory: 'target/cucumber-reports'
                }
            }
        }

        stage('Reports') {
            steps {
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

    post {
        always {
            emailext (
                subject: "Build Result: ${currentBuild.fullDisplayName}",
                body: """
                Build: ${env.BUILD_URL}
                Result: ${currentBuild.currentResult}
                Duration: ${currentBuild.durationString}

                Test Reports: ${env.BUILD_URL}cucumber-html-reports/
                """,
                to: "your-email@example.com"
            )
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

triggers {
    pollSCM('H/5 * * * *')  // 每5分钟检查代码变更
    cron('0 2 * * *')       // 每天凌晨2点执行
}