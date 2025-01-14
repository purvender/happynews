# HappyNews Backend

A Spring Boot application for fetching news from NewsAPI, storing articles and images using PostgreSQL and S3, and providing a REST API with advanced search functionality. The application uses Quartz for scheduling tasks, supports dynamic parameter updates, and is designed for deployment on AWS EC2 using Free Tier resources.

## Features

- Dynamic Article Fetching: Periodic fetching of news articles using Quartz Scheduler.
- Advanced Search: Search with filters (keywords, source, language, publication date, etc.).
- Image Storage: Stores images in AWS S3.
- Database Persistence: Stores articles and fetch history in PostgreSQL RDS.
- Configurable Scheduler: Update Quartz job parameters dynamically.
- Extensibility: Easily add new news sources and adjust search parameters.

## Prerequisites

- Java 21
- PostgreSQL (RDS)
- AWS S3
- AWS Free Tier account
- Docker (for containerizing the application)
- Git

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/happynews.git
cd happynews
```

### 2. Configure AWS Resources

#### a. Default VPC
We are using the default VPC, which comes pre-configured with public subnets, an Internet Gateway, and default route tables.

#### b. S3 Bucket Setup
- Create an S3 bucket named `happynews-bucket` in your AWS Console under Services > S3.
- Leave default settings or configure bucket policies as needed.

#### c. RDS PostgreSQL Setup
- In the AWS Console, navigate to RDS > Create database.
- Choose PostgreSQL with Free Tier template.
- Set DB Instance Identifier as `happynewsdb`, and configure the master username and password.
- Make sure the instance uses the default VPC and private subnets.
- Public access should be disabled, and a security group should allow inbound traffic on port `5432` from your EC2 instance.

### 3. Application Configuration

#### a. Update `application-prod.yaml`
In the `src/main/resources` directory, modify `application-prod.yaml` with your AWS RDS and S3 details:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<RDS_ENDPOINT>:5432/happynewsdb
    username: postgres
    password: bluetree
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

app:
  storage:
    type: s3
    s3:
      region: us-east-1
      bucket: happynews-bucket
```
Replace `<RDS_ENDPOINT>` with your actual RDS endpoint.

#### b. IAM Role for EC2
Ensure your EC2 instance has an IAM role attached (e.g., `HappyNewsEC2Role`) with permissions for S3 and RDS.

### 4. Install Required Tools on EC2 (Ubuntu)

#### a. Connect to Your EC2 Instance
```bash
ssh -i "/path/to/happynewskeypair.pem" ubuntu@<EC2_PUBLIC_IP>
```
Replace `<EC2_PUBLIC_IP>` with your instance's IP.

#### b. Update the System
```bash
sudo apt update
sudo apt upgrade -y
```

#### c. Install Docker
```bash
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt update
sudo apt install -y docker-ce
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ubuntu
```
Log out and log back in to apply group changes.

### 5. Transfer Application Files to EC2

On your local machine, run these commands to transfer the JAR and Dockerfile:

```bash
scp -i /path/to/happynewskeypair.pem /Users/purvenderhooda/Desktop/happynews/build/libs/happynews-0.0.1-SNAPSHOT.jar ubuntu@<EC2_PUBLIC_IP>:~/happynews.jar
scp -i /path/to/happynewskeypair.pem /Users/purvenderhooda/Desktop/happynews/Dockerfile ubuntu@<EC2_PUBLIC_IP>:~/Dockerfile
```
Replace `<EC2_PUBLIC_IP>` with your instance's public IP.

### 6. Build and Run the Docker Image on EC2

#### a. SSH into the EC2 Instance
```bash
ssh -i "/path/to/happynewskeypair.pem" ubuntu@<EC2_PUBLIC_IP>
```

#### b. Verify Files
```bash
ls -l ~/happynews.jar ~/Dockerfile
```

#### c. Build Docker Image
```bash
docker build -t happynews:latest .
```

#### d. Run Docker Container
```bash
docker run -d -p 8080:8080 happynews:latest
```

### 7. Test the Application

Use curl or a browser to test your endpoints. For example:
```bash
curl -X GET "http://<EC2_PUBLIC_IP>:8080/api/articles/search?keyword=technology&pageSize=10&page=1"
```
Replace `<EC2_PUBLIC_IP>` with your instance's public IP.

### Summary of How Components Connect

- **EC2 Instance**: Runs the Dockerized Spring Boot application. It uses an IAM role to securely access AWS services like S3 and RDS.
- **RDS PostgreSQL**: Stores application data. Your EC2 instance connects to it using JDBC with credentials specified in the configuration.
- **S3 Bucket**: Stores images and is accessed by your application through the IAM role, which provides necessary permissions.
- **Security Groups**:
    - EC2 Security Group controls who can SSH and access HTTP endpoints.
    - RDS Security Group restricts database connections to the EC2 instance.
- **IAM Role**: Grants the EC2 instance permissions to access S3 and RDS, eliminating the need for hardcoded credentials.
- **Local System**: Used for development, file transfer (via SCP), and SSH management of the EC2 instance.

---

### Future Enhancements and Next Steps

- **Monitoring**: Set up CloudWatch logs and metrics to monitor application performance and health.
- **Scaling**: Consider Auto Scaling Groups and load balancers for high availability.
- **Security**: Use HTTPS, restrict SSH access, and manage secrets securely.
- **CI/CD**: Automate builds and deployments using AWS CodePipeline, GitHub Actions, or similar tools.

---

Feel free to copy this content directly into your `README.md` file. This file uses fenced code blocks for commands and configuration snippets to ensure easy copying without formatting issues.
