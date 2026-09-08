# Student Management — DevOps Runbook

This document contains the procedures required to start, verify, operate,
test, and safely stop the DevOps environment for the Student Management
application.

Current scope:

- Spring Boot backend
- MySQL database
- Docker
- Docker Desktop Kubernetes
- Kubernetes Deployments
- Kubernetes Services
- Kubernetes Persistent Volume / Persistent Volume Claim
- ConfigMap
- Secret
- Jenkins
- SonarQube
- Docker Hub
- GitHub
- ngrok
- GitHub → Jenkins webhook
- CI/CD deployment to Kubernetes

Frontend deployment will be documented separately when the frontend is added.

---

# 1. Project Structure

Current project structure:

Student Management/
│
├── .github/
├── .vscode/
│
├── student-management-backend/
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── Dockerfile
│
├── student-management-frontend/
│   └── ...
│
├── k8s/
│   ├── backend-deployment.yaml
│   ├── backend-service.yaml
│   ├── configmap.yaml
│   ├── mysql-deployment.yaml
│   ├── mysql-pv.yaml
│   ├── mysql-pvc.yaml
│   ├── mysql-service.yaml
│   ├── secret.yaml
│   └── secret.example.yaml
│
├── .gitignore
├── compose.yaml
├── Jenkinsfile
├── Jenkinsfile-backend
└── DEVOPS-RUNBOOK.md


Important:

- Kubernetes manifests are stored under k8s/.
- Real Kubernetes secrets must NOT be committed to Git.
- Jenkins configuration is stored in the Jenkins Docker volume.
- MySQL Kubernetes data is stored using a Persistent Volume.
- Docker Hub stores the application images.

2. Prerequisites

Before starting the project, the following must be installed and available.

2.1 Operating System

Windows with:

Docker Desktop
WSL2
Kubernetes enabled in Docker Desktop
2.2 Java

Java 21 is required for the Spring Boot backend.

Verify:

java -version

Expected version:

Java 21
2.3 Maven

The project uses Maven Wrapper, so Maven does not need to be installed globally.

Windows:

mvnw.cmd -version

Linux/macOS:

./mvnw -version
2.4 Git

Verify:

git --version

Git is required for:

source control
GitHub
Jenkins checkout
webhook-triggered builds
2.5 Docker

Verify:

docker version

Also verify:

docker ps

Docker Desktop must be running before using Docker commands.

2.6 Kubernetes

Verify:

kubectl version --client

Then:

kubectl get nodes

Expected:

desktop-control-plane   Ready   control-plane   ...

The current Docker Desktop Kubernetes cluster uses:

Kubernetes version: 1.36.1
Cluster: Docker Desktop / kind
Node: desktop-control-plane
2.7 Docker Hub

The backend image is pushed to:

ashutosh12505/student-management-backend

Images are tagged using the Jenkins build number.

Example:

ashutosh12505/student-management-backend:build-13

Docker Hub credentials are stored in Jenkins and must NOT be hardcoded in the Jenkinsfile.

2.8 Jenkins

Jenkins is running in a custom Docker image:

jenkins-docker:2.568.3

The container name is:

jenkins

Jenkins uses the persistent Docker volume:

jenkins_home

Jenkins is exposed on:

http://localhost:8080
2.9 SonarQube

SonarQube runs in Docker.

Container name:

sonarqube

SonarQube is exposed on:

http://localhost:9000

Jenkins is configured to communicate with SonarQube using:

http://host.docker.internal:9000
2.10 ngrok

ngrok is used to expose the local Jenkins server to GitHub.

The current setup uses:

ngrok
    ↓
host.docker.internal:8080
    ↓
Jenkins

ngrok is required only when GitHub webhook → Jenkins automation is needed.

3. Important Persistent Data

There are two especially important persistence mechanisms.

3.1 Jenkins Persistence

Jenkins uses:

jenkins_home

Docker volume.

It contains Jenkins data such as:

jobs
pipeline configuration
credentials
plugins
build history
Jenkins configuration
Jenkins-related files

The Jenkins container can be stopped/recreated while preserving the volume.

DO NOT delete:

jenkins_home
3.2 MySQL Persistence

MySQL runs inside Kubernetes.

The persistence hierarchy is:

MySQL Pod
    ↓
PersistentVolumeClaim
    ↓
PersistentVolume
    ↓
MySQL data directory

Resources:

PersistentVolume:
mysql-pv

PersistentVolumeClaim:
mysql-pvc

The MySQL data directory is:

/var/lib/mysql

The database contains application data such as:

student_management
├── app_user
└── student

Deleting/recreating the MySQL Pod does NOT delete the database data as long as the PVC/PV is preserved.

4. Kubernetes Resources

Current Kubernetes resources:

MySQL

Deployment:

mysql

Service:

mysql-service

PVC:

mysql-pvc

PV:

mysql-pv
Backend

Deployment:

student-management

Service:

student-management-service
Configuration

ConfigMap:

student-db-config

Secret:

student-db-secret
5. Kubernetes Architecture

The current backend architecture is:

                 Kubernetes Cluster
                       │
              ┌────────┴────────┐
              │                 │
        Backend Service    MySQL Service
              │                 │
              ▼                 ▼
       Backend Pod          MySQL Pod
              │                 │
              │                 ▼
              │             mysql-pvc
              │                 │
              │                 ▼
              │             mysql-pv
              │
              ▼
       Spring Boot

Backend connects to MySQL using:

jdbc:mysql://mysql-service:3306/student_management

The important point is that Kubernetes Service DNS provides:

mysql-service

as the hostname inside the cluster.

The backend does NOT use:

localhost

to reach MySQL.

6. Daily Startup Procedure

Use this procedure after restarting the laptop.

Step 1 — Start Docker Desktop

Open Docker Desktop.

Wait until Docker Desktop reports that Docker is running.

Verify:

docker version

Then:

docker ps
7. Verify Kubernetes

Check the cluster:

kubectl get nodes

Expected:

desktop-control-plane   Ready   control-plane

If Kubernetes is still starting, wait for Docker Desktop Kubernetes to become ready.

Then:

kubectl get pods
8. Verify Kubernetes MySQL

Check the MySQL Deployment:

kubectl get deployment mysql

Check the Pod:

kubectl get pods -l app=mysql

Check the Service:

kubectl get service mysql-service

Check persistence:

kubectl get pvc

and:

kubectl get pv

Expected:

mysql deployment     1/1
mysql pod            Running
mysql-pvc            Bound
mysql-pv             Bound
9. Verify Backend

Check the Deployment:

kubectl get deployment student-management

Check the Pod:

kubectl get pods -l app=student-management

Check the Service:

kubectl get service student-management-service

Expected:

student-management deployment   1/1
student-management pod          Running
10. Check Backend Logs

If the backend Pod is running:

kubectl logs deployment/student-management

The logs should show that:

Spring Boot started
Tomcat started
JPA initialized
MySQL connection succeeded
application started successfully

For more detailed Pod information:

kubectl describe pod -l app=student-management
11. Start Jenkins

Jenkins does not have to run continuously.

Start it when CI/CD work is required:

docker start jenkins

Verify:

docker ps

Open:

http://localhost:8080
12. Start SonarQube

Start SonarQube when a Jenkins pipeline containing SonarQube analysis is going to run:

docker start sonarqube

Verify:

docker ps

Open:

http://localhost:9000

Wait until SonarQube is fully started before running a Jenkins build.

13. Start ngrok

ngrok is required for GitHub webhook automation.

Start:

docker start ngrok

Verify:

docker ps

Check ngrok:

docker logs ngrok

The ngrok API can also be checked through:

http://localhost:4040
14. GitHub Webhook

The GitHub webhook points to the public ngrok URL.

The webhook path must end with:

/github-webhook/

Example:

https://<ngrok-public-url>/github-webhook/

Important:

The free ngrok public URL can change when the tunnel is recreated.

If the URL changes:

Get the new ngrok URL.
Open the GitHub repository.
Go to:
GitHub → Settings → Webhooks
Edit the existing webhook.
Replace the old public URL.
Keep:
/github-webhook/
Save the webhook.
15. Jenkins → Kubernetes Authentication

Jenkins uses kubectl to communicate with the Docker Desktop Kubernetes cluster.

The Jenkins container contains:

/var/jenkins_home/.kube/config

The kubeconfig uses:

https://host.docker.internal:65506

because:

127.0.0.1

inside the Jenkins container refers to the Jenkins container itself, not Windows.

The local lab kubeconfig currently uses:

insecure-skip-tls-verify: true

This is acceptable for the local learning environment.

It is NOT recommended for a production Kubernetes setup.

In production, TLS hostname/certificate configuration should be fixed instead of disabling TLS verification.

16. Verify Jenkins → Kubernetes Connectivity

Run:

docker exec jenkins kubectl --kubeconfig=/var/jenkins_home/.kube/config get nodes

Expected:

desktop-control-plane   Ready   control-plane

Also:

docker exec jenkins kubectl --kubeconfig=/var/jenkins_home/.kube/config get deployments

and:

docker exec jenkins kubectl --kubeconfig=/var/jenkins_home/.kube/config get pods

If these work, Jenkins can communicate with Kubernetes.

17. CI/CD Flow

The current backend CI/CD pipeline is:

Developer
    │
    ▼
Git
    │
    ▼
GitHub
    │
    │ Webhook
    ▼
ngrok
    │
    ▼
Jenkins
    │
    ├── Checkout
    │
    ├── Maven Build
    │
    ├── Unit Tests
    │
    ├── SonarQube Analysis
    │
    ├── Quality Gate
    │
    ├── Docker Build
    │
    ├── Docker Hub Push
    │
    └── Kubernetes Deployment
             │
             ▼
       Student Management
             │
             ▼
           MySQL
18. Detailed CI/CD Pipeline
Stage 1 — Checkout

Jenkins checks out the GitHub repository.

Source:

GitHub
Stage 2 — Backend Build

Jenkins enters:

student-management-backend

and runs:

./mvnw clean package -DskipTests

Windows/local equivalent:

mvnw.cmd clean package -DskipTests

The Spring Boot JAR is created in:

target/

Current JAR:

student-management-0.0.1-SNAPSHOT.jar
19. Unit Tests

Jenkins runs:

./mvnw -Dtest=StudentServiceTest verify

The pipeline must stop if the test fails.

20. SonarQube Analysis

Jenkins runs:

./mvnw sonar:sonar -Dsonar.projectKey=student-management-backend

SonarQube analyzes the backend source code.

The project is:

student-management-backend

SonarQube checks items such as:

Bugs
Vulnerabilities
Code Smells
Security Hotspots
Code Coverage
Duplications
Quality Gate
21. Quality Gate

Jenkins waits for SonarQube's Quality Gate result.

Conceptually:

Jenkins
   │
   ▼
SonarQube
   │
   ▼
Quality Gate
   │
   ├── PASSED → Continue
   │
   └── FAILED → Stop pipeline

The Jenkins pipeline uses:

waitForQualityGate abortPipeline: true

Therefore a failed Quality Gate prevents deployment.

22. Docker Image Build

If all previous stages pass, Jenkins builds the Docker image.

The image name is generated using the Jenkins build number.

Example:

ashutosh12505/student-management-backend:build-13

The Dockerfile:

student-management-backend/Dockerfile

uses:

eclipse-temurin:21-jre

and runs:

java -jar app.jar
23. Docker Hub Push

Jenkins logs into Docker Hub using a Jenkins credential.

Credential ID:

dockerhub-credentials

The credentials are NOT stored directly in the Jenkinsfile.

The image is pushed to:

ashutosh12505/student-management-backend

Example:

build-13
build-14
build-15
...
24. Kubernetes Deployment

After the Docker image is pushed, Jenkins runs:

kubectl set image deployment/student-management \
    student-management=ashutosh12505/student-management-backend:build-<BUILD_NUMBER>

Then Jenkins waits for:

kubectl rollout status deployment/student-management

This verifies that Kubernetes successfully updated the Deployment.

25. Deployment Strategy

Kubernetes Deployment manages the backend Pod.

The Deployment specifies:

replicas: 1

When Jenkins changes the image:

Old image
    ↓
New image
    ↓
Kubernetes creates/updates Pod
    ↓
New container starts
    ↓
Old Pod is replaced

Kubernetes maintains the desired state.

26. Zero-Touch Deployment

The desired workflow is:

Developer changes code
        ↓
git add
        ↓
git commit
        ↓
git push
        ↓
GitHub
        ↓
GitHub Webhook
        ↓
ngrok
        ↓
Jenkins
        ↓
Build
        ↓
Tests
        ↓
SonarQube
        ↓
Quality Gate
        ↓
Docker Build
        ↓
Docker Hub
        ↓
Kubernetes
        ↓
New Backend Version

No manual Jenkins "Build Now" operation should be necessary when the webhook is functioning.

27. Jenkins Concurrent Build Protection

The Jenkinsfile contains:

options {
    disableConcurrentBuilds(abortPrevious: true)
}

This prevents multiple webhook-triggered builds from consuming resources simultaneously.

This is particularly important on a local development laptop.

28. Verify a Successful CI/CD Deployment

After pushing code:

git status

Then:

git add .
git commit -m "your message"
git push

GitHub should trigger Jenkins.

Check Jenkins

Open:

http://localhost:8080

Verify the new build has started.

Check Jenkins stages

The pipeline should pass:

Checkout
   ↓
Backend Build
   ↓
Unit Tests
   ↓
SonarQube Analysis
   ↓
Quality Gate
   ↓
Docker Build & Push
   ↓
Deploy to Kubernetes
29. Verify Docker Hub

Check the Docker Hub repository:

ashutosh12505/student-management-backend

Verify that the new build tag exists.

For example:

build-13
30. Verify Kubernetes Image

Run:

kubectl get deployment student-management -o=jsonpath="{.spec.template.spec.containers[0].image}"

Expected example:

ashutosh12505/student-management-backend:build-13

The build number should correspond to the successful Jenkins build.

31. Verify Kubernetes Rollout

Run:

kubectl rollout status deployment/student-management

Expected:

deployment "student-management" successfully rolled out

Check:

kubectl get deployment student-management

Expected:

READY   UP-TO-DATE   AVAILABLE
1/1     1            1
32. Verify Backend Pod

Run:

kubectl get pods -l app=student-management

Expected:

student-management-xxxxx   1/1   Running
33. Verify Backend Service

Run:

kubectl get service student-management-service

The Service should exist as:

student-management-service

Type:

ClusterIP

The Service provides a stable internal Kubernetes endpoint for the backend.

34. Verify MySQL

Run:

kubectl get pods -l app=mysql

Expected:

mysql-xxxxx   1/1   Running

Check:

kubectl get service mysql-service

Check persistence:

kubectl get pvc mysql-pvc

Expected:

Bound
35. Verify Backend → MySQL Connection

Check backend logs:

kubectl logs deployment/student-management

Look for successful:

datasource initialization
Hibernate/JPA initialization
MySQL connection
application startup

The backend datasource should use:

jdbc:mysql://mysql-service:3306/student_management
36. Verify Kubernetes Resources Together

Useful command:

kubectl get all

Then:

kubectl get pvc

and:

kubectl get pv

Configuration:

kubectl get configmap
kubectl get secret

Do not display or share real Secret values.

37. Troubleshooting — Kubernetes
Pod is Pending

Run:

kubectl describe pod <pod-name>

Check:

scheduling problems
resource limits
volume problems
image pull problems
Pod is CrashLoopBackOff

Run:

kubectl logs <pod-name>

If necessary:

kubectl logs <pod-name> --previous

Then:

kubectl describe pod <pod-name>
Backend cannot connect to MySQL

Check:

kubectl get service mysql-service

Then:

kubectl get endpointslice

Check MySQL:

kubectl get pods -l app=mysql

Check backend configuration:

SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

The URL should use:

mysql-service

not:

localhost
38. Troubleshooting — Jenkins

Check Jenkins:

docker ps

If stopped:

docker start jenkins

Check logs:

docker logs jenkins

Follow logs:

docker logs -f jenkins

Jenkins UI:

http://localhost:8080
39. Troubleshooting — SonarQube

Check:

docker ps

Start if necessary:

docker start sonarqube

Check:

docker logs sonarqube

Open:

http://localhost:9000

SonarQube must be fully started before Jenkins performs analysis.

40. Troubleshooting — GitHub Webhook

If a GitHub push does not trigger Jenkins:

Check Jenkins is running.
Check ngrok is running.
Check ngrok URL.
Check GitHub webhook URL.
Verify the URL ends with:
/github-webhook/
Check GitHub webhook delivery status.
Check Jenkins build history.

Check ngrok:

docker logs ngrok

Open:

http://localhost:4040
41. Troubleshooting — Docker

Check:

docker version

Then:

docker ps

If Docker commands return:

500 Internal Server Error

do NOT immediately delete containers, images, or volumes.

First:

Check Docker Desktop.
Check whether Docker Engine is running.
Check available disk space.
Check Docker Desktop resource usage.
Retry the command.
Restart Docker Desktop if necessary.

Useful commands:

docker stats --no-stream
docker system df
42. Resource Management

This project runs several heavy services:

Docker Desktop
Kubernetes
MySQL
Spring Boot
Jenkins
SonarQube
ngrok

Jenkins and SonarQube are not required to run continuously.

For normal application development, it is preferable to keep only the required services running.

43. Application Mode

When only working on the application:

Docker Desktop
      ↓
Kubernetes
      ├── MySQL
      └── Backend

Jenkins, SonarQube and ngrok can remain stopped.

44. CI/CD Mode

When testing the complete DevOps pipeline:

Docker Desktop
      │
      ├── Jenkins
      ├── SonarQube
      ├── ngrok
      │
      └── Kubernetes
           ├── MySQL
           └── Backend
45. Safe Stop Procedure

When finished working for the day:

Step 1 — Stop Jenkins
docker stop jenkins
Step 2 — Stop SonarQube
docker stop sonarqube
Step 3 — Stop ngrok
docker stop ngrok
Step 4 — Kubernetes

Normally, do NOT delete Kubernetes resources.

Do NOT delete:

Deployments
Services
PV
PVC
ConfigMap
Secret

Simply shut down Windows normally.

Docker Desktop and Kubernetes will stop as part of the system shutdown.

46. Safe Laptop Shutdown

Before shutting down:

docker ps

Make sure no important operation is currently running.

If a Jenkins build is running, allow it to finish if possible.

If it is a stuck/unwanted build:

Abort the Jenkins build from Jenkins.
Wait for the build to stop.
Stop Jenkins if required.
Stop SonarQube.
Stop ngrok.
Shut down Windows normally.

A normal Windows shutdown does NOT delete Docker or Kubernetes data.

47. After Laptop Restart

After starting Windows:

Step 1

Start Docker Desktop.

Step 2

Verify:

docker version
Step 3

Verify Kubernetes:

kubectl get nodes
Step 4

Verify application:

kubectl get pods
Step 5

If CI/CD is required:

docker start jenkins
docker start sonarqube
docker start ngrok

Then verify:

docker ps
48. What NOT to Delete

The following resources are important.

Docker

DO NOT casually delete:

jenkins_home

It contains Jenkins data.

Docker Volumes

Do NOT casually run:

docker volume prune

because it may remove persistent volumes that are no longer attached to running containers.

Always inspect volumes before removing anything.

Docker System Prune

Do NOT blindly run:

docker system prune -a

This can remove unused images and other Docker resources that may be needed later.

Use targeted cleanup instead.

Kubernetes PVC

DO NOT delete:

mysql-pvc

unless you intentionally want to remove the database storage.

Kubernetes PV

DO NOT delete:

mysql-pv

unless you intentionally want to remove the persistent database storage.

Kubernetes Cluster

DO NOT use:

Docker Desktop → Reset Kubernetes Cluster

unless you intentionally want to rebuild the Kubernetes environment.

Resetting the cluster can remove the Kubernetes resources created for this project.

Docker Desktop Data Reset

DO NOT use Docker Desktop's:

Clean / Purge data

or similar destructive reset options without understanding what data will be removed.

49. Kubernetes Pod Deletion vs Database Deletion

Deleting a MySQL Pod is not the same as deleting MySQL storage.

For example:

kubectl delete pod <mysql-pod-name>

Kubernetes will create another Pod because the Deployment requires:

replicas: 1

The new Pod mounts:

mysql-pvc

and the existing database data remains.

However:

kubectl delete pvc mysql-pvc

is a completely different operation and can affect persistent database storage.

50. Secrets

The Kubernetes Secret is:

student-db-secret

The real Secret file must NOT be committed to Git.

The repository should contain:

k8s/secret.example.yaml

with placeholder values.

Example:

apiVersion: v1
kind: Secret
metadata:
  name: student-db-secret
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: <username>
  SPRING_DATASOURCE_PASSWORD: <password>

The real:

k8s/secret.yaml

must be included in .gitignore.

Never commit:

database passwords
Docker Hub tokens
Jenkins secrets
API tokens
private keys
kubeconfig credentials
51. Important Git-Ignored Files

The following sensitive/local files should not be committed:

k8s/secret.yaml
jenkins-kubeconfig.yaml
ca.crt
client.crt
client.key
ca-base64.txt
client-cert-base64.txt
client-key-base64.txt

The exact .gitignore should be checked before every push.

52. Useful Verification Commands
Docker
docker ps
docker ps -a
docker images
docker volume ls
docker system df
Kubernetes
kubectl get nodes
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get pvc
kubectl get pv
kubectl get configmap
Backend
kubectl logs deployment/student-management
MySQL
kubectl logs deployment/mysql
Jenkins → Kubernetes
docker exec jenkins kubectl --kubeconfig=/var/jenkins_home/.kube/config get nodes
53. Complete Environment Verification

Use the following checklist before starting a major CI/CD test.

[ ] Docker Desktop is running
[ ] Docker Engine is working
[ ] Kubernetes is enabled
[ ] Kubernetes node is Ready
[ ] MySQL Deployment is 1/1
[ ] MySQL Pod is Running
[ ] mysql-pvc is Bound
[ ] mysql-pv is Bound
[ ] Backend Deployment is 1/1
[ ] Backend Pod is Running
[ ] MySQL Service exists
[ ] Backend Service exists
[ ] Jenkins is running
[ ] SonarQube is running
[ ] ngrok is running
[ ] Jenkins UI is accessible
[ ] SonarQube UI is accessible
[ ] ngrok tunnel is active
[ ] GitHub webhook URL is correct
[ ] Jenkins can access Kubernetes
[ ] Docker Hub credentials are configured
54. Complete CI/CD Verification

After pushing a change:

[ ] Code pushed to GitHub
[ ] GitHub webhook delivered successfully
[ ] Jenkins build started automatically
[ ] Checkout passed
[ ] Maven build passed
[ ] Unit tests passed
[ ] SonarQube analysis passed
[ ] Quality Gate passed
[ ] Docker image built
[ ] Docker image pushed to Docker Hub
[ ] Kubernetes Deployment image updated
[ ] Kubernetes rollout succeeded
[ ] New backend Pod is Running
[ ] Backend logs show successful startup
55. Important Operational Rule

Do not treat containers as permanent storage.

Use:

Container → disposable
Volume/PV → persistent data

For this project:

Jenkins container
      ↓
jenkins_home volume

and:

MySQL Pod
      ↓
mysql-pvc
      ↓
mysql-pv

Therefore:

Container/Pod recreation
        ≠
Data deletion

as long as persistent storage is preserved.

56. Current URLs

Jenkins:

http://localhost:8080

SonarQube:

http://localhost:9000

ngrok local dashboard:

http://localhost:4040

GitHub webhook:

https://<current-ngrok-url>/github-webhook/

The ngrok public URL is not permanent on the free setup and must be checked when ngrok is restarted/recreated.

57. Current Docker Containers

Important container names:

jenkins
sonarqube
ngrok

Useful commands:

docker start jenkins
docker start sonarqube
docker start ngrok

Stop:

docker stop jenkins
docker stop sonarqube
docker stop ngrok
58. Current Docker Image

Custom Jenkins image:

jenkins-docker:2.568.3

Backend image repository:

ashutosh12505/student-management-backend

Backend images use build-number tags:

build-<JENKINS_BUILD_NUMBER>

Example:

ashutosh12505/student-management-backend:build-13
59. Current Kubernetes Resource Names
MySQL
Deployment: mysql
Service: mysql-service
PV: mysql-pv
PVC: mysql-pvc
Backend
Deployment: student-management
Service: student-management-service
Configuration
ConfigMap: student-db-config
Secret: student-db-secret
60. Quick Start — Application Only

For normal backend/application work:

docker version
kubectl get nodes
kubectl get pods
kubectl get deployments
kubectl get services

If Kubernetes resources are already present and healthy, no Jenkins/SonarQube/ngrok startup is required.

61. Quick Start — Full CI/CD

For a complete CI/CD test:

docker version
kubectl get nodes
kubectl get pods

Then:

docker start jenkins
docker start sonarqube
docker start ngrok

Verify:

docker ps

Verify Jenkins → Kubernetes:

docker exec jenkins kubectl --kubeconfig=/var/jenkins_home/.kube/config get nodes

Open:

http://localhost:8080

Open:

http://localhost:9000

Verify ngrok:

http://localhost:4040

Then push code to GitHub and allow the webhook to trigger Jenkins.

62. Quick Stop — End of Day

If CI/CD is not running:

docker stop jenkins
docker stop sonarqube
docker stop ngrok

Do NOT delete Kubernetes resources.

Do NOT delete PVC/PV.

Do NOT reset Kubernetes.

Do NOT prune Docker volumes.

Then shut down Windows normally.

63. Emergency Rule

If Docker Desktop becomes unstable or commands start returning:

500 Internal Server Error

do NOT immediately delete containers, images, volumes, or Kubernetes resources.

Use:

docker ps
docker stats --no-stream
docker system df

Check Docker Desktop.

If necessary, restart Docker Desktop.

Only perform cleanup after identifying what is consuming resources.

64. Golden Rules
Normal laptop shutdown is safe.
Stopping a container is not the same as deleting it.
Deleting a Pod is not the same as deleting its persistent storage.
Jenkins data is stored in jenkins_home.
MySQL data is stored through mysql-pvc / mysql-pv.
Never casually delete jenkins_home.
Never casually delete mysql-pvc or mysql-pv.
Never reset the Docker Desktop Kubernetes cluster unless intentionally rebuilding it.
Never commit real Kubernetes secrets.
Never hardcode Docker Hub tokens in Jenkinsfiles.
Do not run docker system prune -a blindly.
Do not run docker volume prune blindly.
Jenkins, SonarQube and ngrok do not need to run all the time.
Use Kubernetes for application runtime.
Use Jenkins for CI/CD automation.
Use SonarQube for code quality/security analysis.
Use Docker Hub as the container image registry.
Use GitHub webhook + ngrok for automatic Jenkins triggering in this local setup.
Verify Kubernetes health before troubleshooting the application.
When something fails, troubleshoot the failing layer instead of deleting/rebuilding everything.
65. Overall Architecture Summary

The current DevOps architecture is:

                         GitHub
                           │
                           │ Push
                           ▼
                    GitHub Webhook
                           │
                           ▼
                         ngrok
                           │
                           ▼
                        Jenkins
                           │
             ┌─────────────┼──────────────┐
             │             │              │
             ▼             ▼              ▼
           Maven       SonarQube      Unit Tests
             │             │
             └──────┬──────┘
                    │
                    ▼
               Quality Gate
                    │
                    ▼
              Docker Build
                    │
                    ▼
                Docker Hub
                    │
                    ▼
             kubectl set image
                    │
                    ▼
          Kubernetes Deployment
                    │
                    ▼
          Student Management Pod
                    │
                    ▼
             Backend Service
                    │
                    ▼
               MySQL Service
                    │
                    ▼
                MySQL Pod
                    │
                    ▼
               mysql-pvc
                    │
                    ▼
                mysql-pv
                    │
                    ▼
             Persistent Data
66. Final Recovery Principle

If the laptop is shut down:

Laptop shutdown
       ↓
Docker Desktop stops
       ↓
Containers stop
       ↓
Kubernetes stops

After restarting:

Docker Desktop starts
       ↓
Kubernetes becomes Ready
       ↓
Existing Kubernetes resources remain
       ↓
Persistent storage remains
       ↓
Required containers can be started
       ↓
Application can be verified

A normal shutdown is therefore an operational stop, not a rebuild.

The project should only be rebuilt/reset when there is an intentional reason to rebuild it.


### One small recommendation

Keep this file at the **root of the repository**:

```text
Student Management/
├── DEVOPS-RUNBOOK.md
├── Jenkinsfile
├── compose.yaml
├── k8s/
├── student-management-backend/
└── student-management-frontend/