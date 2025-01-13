# HappyNews Backend

A Spring Boot application for fetching news from NewsAPI, storing articles and images using PostgreSQL and Minio/AWS S3, and providing a REST API with complex search functionality. The application uses Quartz for scheduling tasks.

## Features

- Periodic fetching of news articles using Quartz.
- Storage of images on Minio (development) or AWS S3 (production).
- PostgreSQL database for article persistence.
- Dynamic search with filters (keywords, source, language, publication date).
- Easy extensibility to add new news sources.

## Prerequisites

- Java 21
- PostgreSQL
- Minio (for development)
- Docker (optional, for running Minio)
- Git

## Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/happynews.git
cd happynews
cd happynews
2. Configure Environment
Database: Set up a PostgreSQL database. Update credentials in src/main/resources/application-dev.yaml.
Minio: Start Minio locally:
bash
Copy code
docker run -p 9000:9000 -p 9001:9001 --name minio \
  -e "MINIO_ROOT_USER=YOUR_MINIO_ACCESS_KEY" \
  -e "MINIO_ROOT_PASSWORD=YOUR_MINIO_SECRET_KEY" \
  quay.io/minio/minio server /data --console-address ":9001"
Replace with your credentials. Create a bucket named happynews-bucket or update configuration accordingly.
NewsAPI Key: Replace YOUR_NEWSAPI_KEY in src/main/resources/application.yaml with your actual NewsAPI key.
3. Build and Run Application
bash
Copy code
./gradlew build
./gradlew bootRun
4. Testing the Application
Manually Trigger News Fetch: If a manual endpoint is added:
bash
Copy code
curl -X POST http://localhost:8080/api/articles/fetch
Search Articles:
bash
Copy code
curl "http://localhost:8080/api/articles/search?keyword=Microsoft"
5. Git Workflow
After making changes, use:
bash
Copy code
git add .
git commit -m "Your commit message"
git push origin main
Future Enhancements
Integrate additional news sources.
Implement robust error handling and logging.
Secure API endpoints with Spring Security.
Expand search functionality and filtering options.
© 2023 HappyNews Project

---

By following these instructions, you'll have a professionally structured project with version control, a clear setup guide, and detailed testing procedures. Adjust configuration values as needed for your environment.

