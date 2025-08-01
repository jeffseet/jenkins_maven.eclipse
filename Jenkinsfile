pipeline {
    agent any

    tools {
        maven 'maven'         // Must match Maven name in Jenkins tools config
        jdk 'Java JDK 17'     // Must match JDK name in Jenkins tools config
    }

    stages {
        stage('Clean') {
            steps {
                echo 'Starting Clean Phase...'
                bat 'mvn clean'
            }
        }

        stage('Test') {
            steps {
                echo 'Starting Test Phase...'
                bat 'mvn test'
            }
        }

        stage('Build') {
            steps {
                echo 'Starting Build Phase...'
                bat 'mvn install -DskipTests'
            }
        }

        stage('Scan') {
            steps {
                echo 'Start scan'
                bat 'mvn sonar:sonar'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}