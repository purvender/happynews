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
