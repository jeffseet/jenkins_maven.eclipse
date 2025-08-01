pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'Java JDK 17'
    }
    stages {
        stage("clean") {
            steps {
                echo "Start Clean"
                bat "mvn clean"
            }
        }
        stage("test") {
            steps {
                echo "Start Test"
                bat "mvn test"
            }
        }
        stage("build") {
            steps {
                echo "Start Build"
                bat "mvn install -DskipTests"
            }
        }
        stage("scan") {
            steps {
                echo "Start SonarQube Scan"
                withSonarQubeEnv('SonarQube') {
                    bat "sonar-scanner"
                }
            }
        }
    }
}