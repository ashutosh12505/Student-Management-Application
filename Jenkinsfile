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
                bat 'student-management-backend\\mvnw.cmd clean test'
            }
        }

        stage('Frontend Build') {
            steps {
                bat 'cd student-management-frontend && npm ci && npm run build'
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