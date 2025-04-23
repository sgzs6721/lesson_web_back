# Lesson Management System Backend

[![Java](https://img.shields.io/badge/Java-8-orange.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.0-green.svg)](https://spring.io/projects/spring-boot)
[![JOOQ](https://img.shields.io/badge/JOOQ-3.14.16-blue.svg)](https://www.jooq.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-7.14.0-red.svg)](https://flywaydb.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3.0-green.svg)](https://swagger.io/)

A comprehensive backend system for educational institutions to manage courses, students, coaches, campuses, and financial operations.

## System Overview

This system provides a complete solution for educational institutions to manage their operations, including:

- Multi-campus management
- Student enrollment and course tracking
- Coach management with certification tracking
- Course creation and scheduling
- Financial operations including payments and expenses
- Role-based access control
- Comprehensive reporting and analytics

## Technology Stack

- **Java 8**: Core programming language
- **Spring Boot 2.7.0**: Application framework
- **Spring Security**: Authentication and authorization
- **JOOQ 3.14.16**: Type-safe SQL query builder
- **MySQL 8.0**: Database
- **Flyway 7.14.0**: Database migration
- **Redis**: Caching and session management
- **Swagger/OpenAPI 3.0**: API documentation
- **JWT**: Token-based authentication
- **Maven**: Dependency management and build tool

## Project Structure

```
lesson_web_back/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── lesson/
│   │   │           ├── common/       # Common utilities, exceptions, and constants
│   │   │           ├── config/       # Configuration classes (Spring, Security, etc.)
│   │   │           ├── controller/   # REST API controllers
│   │   │           ├── enums/        # Enumeration classes
│   │   │           ├── interceptor/  # Request interceptors
│   │   │           ├── model/        # Data models and repositories
│   │   │           ├── repository/   # JOOQ generated classes
│   │   │           ├── service/      # Business logic services
│   │   │           ├── utils/        # Utility classes
│   │   │           └── vo/           # Value objects for API requests/responses
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/        # Flyway migration scripts
│   │       └── application.yml       # Application configuration
│   └── test/                         # Test classes
└── pom.xml                           # Maven configuration
```

## Key Features

### Campus Management
- Create and manage multiple campuses
- Track campus status (Operating/Closed)
- Manage campus financial metrics (rent, property fees, utilities)
- Assign campus managers and contact information

### Student Management
- Student enrollment and profile management
- Course assignment and tracking
- Payment processing and financial record keeping
- Attendance tracking

### Coach Management
- Comprehensive coach profiles
- Certificate and qualification tracking
- Financial details (salary, commission, performance bonuses)
- Coach availability and status tracking

### Course Management
- Course creation and management
- Course type categorization (Private, Group, Online)
- Course status tracking (Draft, Published, Suspended, Terminated)
- Coach assignment to courses
- Course pricing and hours tracking

### User Management
- Role-based access control (Super Admin, Collaborator, Campus Admin)
- User status management (Enabled/Disabled)
- Secure authentication with JWT
- Permission management

### Financial Management
- Income and expense tracking
- Financial reporting and analytics
- Transaction history

## API Endpoints

The system provides a comprehensive set of RESTful APIs:

### Student Management
- `POST /api/students` - Create a new student
- `GET /api/students/{id}` - Get student details
- `PUT /api/students/{id}` - Update student information
- `GET /api/students` - List students with pagination and filtering

### Course Management
- `POST /api/courses/create` - Create a new course
- `GET /api/courses/{id}` - Get course details
- `PUT /api/courses` - Update course information
- `DELETE /api/courses/{id}` - Delete a course
- `PUT /api/courses/{id}/status` - Update course status
- `GET /api/courses` - List courses with pagination and filtering

### Coach Management
- `POST /api/coaches` - Create a new coach
- `GET /api/coaches/{id}` - Get coach details
- `PUT /api/coaches/{id}` - Update coach information
- `GET /api/coaches` - List coaches with pagination and filtering

### Campus Management
- `POST /api/campus` - Create a new campus
- `GET /api/campus/{id}` - Get campus details
- `PUT /api/campus/{id}` - Update campus information
- `GET /api/campus` - List campuses with pagination and filtering

### User Management
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/users` - List users with pagination and filtering
- `GET /api/roles/assignable` - Get assignable roles for current user
- `GET /api/roles` - Get all roles (admin only)

## Database Design

The database is designed to support all aspects of educational institution management:

### Core Tables
- `sys_user`: System users
- `sys_role`: User roles
- `sys_permission`: System permissions
- `edu_institution`: Educational institutions
- `edu_campus`: Campus locations
- `edu_coach`: Coaches/instructors
- `edu_student`: Students
- `edu_course`: Courses
- `edu_student_course`: Student-course relationships
- `edu_payment`: Payment records
- `edu_expense`: Expense records

## Getting Started

### Prerequisites
- JDK 8+
- MySQL 8.0+
- Redis
- Maven 3.6+

### Database Setup
```sql
CREATE DATABASE lesson DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'lesson'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON lesson.* TO 'lesson'@'localhost';
FLUSH PRIVILEGES;
```

### Configuration
Update the database connection in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lesson?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: lesson
    password: your_password
```

### Build and Run
```bash
# Run database migrations
mvn flyway:migrate

# Generate JOOQ classes
mvn jooq-codegen:generate

# Build the application
mvn clean package

# Run the application
java -jar target/lesson-web-back-1.0.0.jar
```

## API Documentation

API documentation is available via Swagger UI at:
```
http://localhost:8080/lesson/swagger-ui/index.html
```

## Security

- JWT-based authentication
- Role-based access control
- Secure password handling
- CORS protection
- API request validation

## CI/CD

The project includes GitHub Actions workflows for:
- Building and testing the application
- Database migrations
- Deployment to test and production environments

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
