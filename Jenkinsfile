pipeline {

    agent any

    options {
        disableConcurrentBuilds(abortPrevious: true)
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Build & Test') {
            steps {
                dir('student-management-backend') {
                    sh './mvnw clean verify'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('student-management-frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir('student-management-backend') {
                    withSonarQubeEnv('SonarQube') {
                        sh './mvnw sonar:sonar -Dsonar.projectKey=student-management-backend'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    docker build \
                        -t ashutosh12505/student-management-backend:build-${BUILD_NUMBER} \
                        ./student-management-backend

                    docker build \
                        -t ashutosh12505/student-management-frontend:build-${BUILD_NUMBER} \
                        ./student-management-frontend
                '''
            }
        }

        stage('Push Docker Images') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {

                    sh '''
                        set +x

                        echo "$DOCKERHUB_TOKEN" | docker login \
                            --username "$DOCKERHUB_USERNAME" \
                            --password-stdin

                        docker push \
                            "$DOCKERHUB_USERNAME/student-management-backend:build-${BUILD_NUMBER}"

                        docker push \
                            "$DOCKERHUB_USERNAME/student-management-frontend:build-${BUILD_NUMBER}"

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        kubectl --kubeconfig=/var/jenkins_home/.kube/config set image \
                            deployment/student-management \
                            student-management="$DOCKERHUB_USERNAME/student-management-backend:build-${BUILD_NUMBER}"

                        kubectl --kubeconfig=/var/jenkins_home/.kube/config set image \
                            deployment/student-management-frontend \
                            student-management-frontend="$DOCKERHUB_USERNAME/student-management-frontend:build-${BUILD_NUMBER}"
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    kubectl --kubeconfig=/var/jenkins_home/.kube/config rollout status \
                        deployment/student-management \
                        --timeout=120s

                    kubectl --kubeconfig=/var/jenkins_home/.kube/config rollout status \
                        deployment/student-management-frontend \
                        --timeout=120s
                '''
            }
        }
    }

    post {

        success {
            echo 'Full-stack Student Management CI/CD pipeline completed successfully!'
        }

        failure {
            echo 'Full-stack Student Management CI/CD pipeline failed!'
        }
    }
}