# ColLab App Project

ColLab is a collaborative learning platform based on microservices architecture, designed to help educational institutions manage courses, faculty, students, and interactive learning sessions.

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technologies](#technologies)
- [Setup and Installation](#setup-and-installation)
- [Development](#development)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)

## Overview

ColLab is a comprehensive platform for educational institutions that facilitates teaching and learning through a microservices-based architecture. The platform supports interactive lectures, quizzes, course management, and provides separate interfaces for faculty and students.

## Architecture

The application follows a microservices architecture with the following components:

- **Gateway Service**: Acts as an API gateway for all client requests
- **Eureka Service**: Service discovery and registration
- **Auth Service**: Handles authentication and authorization
- **User Service**: Manages user profiles (students and faculty)
- **Master Data Service**: Manages core entity data like schools, specializations, and courses
- **Question Service**: Handles the creation and management of questions for quizzes and lectures
- **Lecture Service**: Manages live lecture sessions and interactions

Each service is designed to be independently deployable, with its own database schema within a shared PostgreSQL database.

## Microservices

### Eureka Service
- Service discovery and registration server
- Port: 8761

### Gateway Service
- API Gateway with routing and authentication filter
- Port: 8080
- Technologies: Spring Cloud Gateway, JWT Authentication

### Auth Service
- Handles user authentication and token management
- Port: 8081
- Features: JWT-based authentication, token refresh, and Redis-based token blacklisting

### User Service
- Manages faculty and student profiles
- Port: 8083
- Features: User creation/management, profile updates, CRUD operations

### Master Data Service
- Manages core entity data for the application
- Port: 8084
- Features: School, specialization, course management, enrollments

### Question Service
- Handles question bank management
- Port: 8085
- Features: Question creation, categorization, answer management

### Lecture Service
- Manages interactive lecture sessions
- Features: Live lecture creation, student participation, real-time interaction

## Technologies

- **Backend**: Java 17, Spring Boot 3.x, Spring Cloud
- **Database**: PostgreSQL
- **Caching**: Redis
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JWT with Redis token blacklisting
- **Build Tool**: Maven
- **API Documentation**: Swagger/OpenAPI
- **Containerization**: Docker

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Docker and Docker Compose
- Maven
- PostgreSQL (or use Docker Compose)
- Redis (or use Docker Compose)

### Database Setup
1. Start the database using Docker:
   ```
   docker-compose up -d postgres-db redis-cache
   ```

2. Initialize the database schema:
   ```
   psql -U postgres -h localhost -d mydb -f database-config.sql
   ```

3. Load initial data:
   ```
   psql -U postgres -h localhost -d mydb -f data.sql
   ```

### Running the Services
1. Start the Eureka Server:
   ```
   cd eureka-service
   ./mvnw spring-boot:run
   ```

2. Start the other services in the following order:
    - Gateway Service
    - Auth Service
    - User Service
    - Master Data Service
    - Question Service
    - Lecture Service

   For each service:
   ```
   cd <service-directory>
   ./mvnw spring-boot:run
   ```

Alternatively, build and run using Docker Compose:
```
./mvnw clean package -DskipTests
docker-compose up -d
```

## Development

### Adding a New Service
1. Create a new Spring Boot project
2. Add dependencies for Eureka Client, Spring Cloud, and other required components
3. Configure the application.yml with appropriate service name and Eureka settings
4. Register the service in the API Gateway

### API Structure
All APIs follow a standard structure:
- Base path: `/api/v1/[resource]`
- Standard response format through `ApiResponse` wrapper
- Validation through Jakarta Bean Validation
- Error handling through global exception handlers

## API Documentation

Each service has its own Swagger UI documentation available at:
```
http://localhost:<service-port>/swagger-ui.html
```

The Gateway service provides aggregated API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/some-feature`)
3. Commit your changes (`git commit -m 'Added some feature'`)
4. Push to the branch (`git push origin feature/some-feature`)
5. Open a Pull Request