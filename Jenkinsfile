pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Build and Test') {
            steps {
                dir('student-management-backend') {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('student-management-frontend') {
                    bat 'npm ci'
                    bat 'npm run build'
                }
            }
        }
    }

    post {
        success {
            echo 'Student Management pipeline completed successfully!'
        }

        failure {
            echo 'Student Management pipeline failed!'
        }
    }
}