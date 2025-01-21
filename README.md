# HappyNews

## Overview
HappyNews is a Spring Boot-based web application designed to aggregate, process, and present positive, motivational, and uplifting news. The platform utilizes external news APIs, stores data in a PostgreSQL database, and supports both S3 and MinIO for image storage, depending on the environment.

## Features
- Fetches articles from external news APIs.
- Stores fetched articles in a PostgreSQL database.
- Supports advanced search functionality for articles.
- Scheduler (Quartz) for periodic fetching of new articles.
- Dual storage service (S3 for production, MinIO for development).

## Prerequisites
### Development Environment:
- **Java**: Version 21 or later.
- **Gradle**: Version 7.0 or later.
- **Docker**: Installed and running.
- **PostgreSQL**: Version 12 or later.
- **MinIO**: For local image storage.

### Production Environment:
- **AWS S3**: Configured with access and secret keys.
- **PostgreSQL**: Hosted database.
- **Docker**: Installed and running.

## Installation

### 1. Clone the Repository
```bash
$ git clone https://github.com/<your-repo>/happynews.git
$ cd happynews
```

### 2. Configure Application Properties
Edit the `application.yml` file in the `src/main/resources` directory to update your database and storage configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<host>:<port>/<dbname>
    username: <db-username>
    password: <db-password>
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

app:
  storage:
    type: minio
    minio:
      endpoint: http://localhost:9000
      accessKey: <minio-access-key>
      secretKey: <minio-secret-key>
      bucket: happynews-bucket
```

### 3. Build the Application
```bash
$ ./gradlew build
```

### 4. Run with Docker
#### Prepare the Docker Environment:
1. **Build the Docker Image**:
   ```bash
   $ docker build -t happynews:latest .
   ```

2. **Run the Container**:
   ```bash
   $ docker run -d -p 8080:8080 --name happynews-app happynews:latest
   ```

#### Verify the Application:
Open your browser and navigate to `http://<server-ip>:8080`.

## Usage
### Scheduled Jobs
The application uses Quartz Scheduler to fetch new articles periodically. You can modify the fetch interval in the `QuartzConfig` class:

```java
.withSchedule(SimpleScheduleBuilder
    .simpleSchedule()
    .withIntervalInHours(6) // Run every 6 hours
    .repeatForever())
```

### Logging
Logs are available in the container and can be viewed using Docker:
```bash
$ docker logs -f happynews-app
```

## Deployment on AWS EC2
1. **Transfer JAR and Dockerfile**:
   Use `scp` to copy the JAR and Dockerfile to your EC2 instance.
   ```bash
   $ scp -i "<keypair>.pem" happynews-0.0.1-SNAPSHOT.jar Dockerfile ubuntu@<ec2-ip>:/home/ubuntu/
   ```

2. **Build and Run**:
   ```bash
   $ docker build -t happynews:latest .
   $ docker run -d -p 8080:8080 --name happynews-app happynews:latest
   ```

## Testing
Run tests with Gradle:
```bash
$ ./gradlew test
```

## Contributors
- **Purvender Hooda**

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Future Enhancements
- Add a front-end for better user interaction.
- Implement caching for API responses.
- Enhance logging and monitoring.

## Contact
For queries or issues, please reach out to:
- **Email**: purvender@gmail.com
- **GitHub**: [https://github.com/purvender]
