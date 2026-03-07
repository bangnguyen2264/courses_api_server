# 📚 Courses API Server

A comprehensive **Online Learning Management System (LMS)** REST API built with Spring Boot. This server manages educational content including subjects, chapters, lessons, quizzes, and exams, along with user authentication, authorization, and media file uploads.

---

## 🚀 Tech Stack

| Component      | Technology                          |
|----------------|-------------------------------------|
| **Framework**  | Spring Boot 4.0.2                   |
| **Language**   | Java 21                             |
| **Database**   | PostgreSQL 16                       |
| **Cache**      | Redis 7                             |
| **Security**   | Spring Security + JWT (Auth0)       |
| **ORM**        | Hibernate / Spring Data JPA         |
| **API Docs**   | Springdoc OpenAPI / Swagger UI      |
| **Mapping**    | MapStruct                           |
| **Build Tool** | Maven                               |
| **Container**  | Docker & Docker Compose             |

---

## 📋 Prerequisites

- **Java 21** JDK
- **Maven 3.6+** (or use the included Maven wrapper `./mvnw`)
- **Docker & Docker Compose** (for PostgreSQL and Redis)

---

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/bangnguyen2264/courses_api_server.git
cd courses_api_server
```

### 2. Start Infrastructure Services

Use Docker Compose to start PostgreSQL and Redis:

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL 16** on port `5432`
- **Redis 7** on port `6379`

### 3. Configure Environment Variables

The `.env.local` file is already present with default values for local development:

```env
# PostgreSQL
POSTGRES_URL=jdbc:postgresql://localhost:5432/course_db
POSTGRES_USER=course
POSTGRES_PASSWORD=course

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
PRIMARY_KEY=course_redis_pass    # Redis password
```

Update these values if your setup differs.

### 4. Build the Project

```bash
./mvnw clean package -DskipTests
```

---

## ▶️ Running the Application

### Option A: Maven Wrapper

```bash
./mvnw spring-boot:run
```

### Option B: JAR File

```bash
java -jar target/course-0.0.1-SNAPSHOT.jar
```

### Option C: Docker

```bash
# Build the image
docker build -t courses-api .

# Run the container
docker run -p 8081:8081 \
  -e POSTGRES_URL=jdbc:postgresql://host.docker.internal:5432/course_db \
  -e POSTGRES_USER=course \
  -e POSTGRES_PASSWORD=course \
  -e REDIS_HOST=host.docker.internal \
  -e REDIS_PORT=6379 \
  -e PRIMARY_KEY=course_redis_pass `# Redis password` \
  courses-api
```

The application will be available at **`http://localhost:8081`**.

---

## 🌐 API Documentation

Once the application is running, access the interactive Swagger UI:

- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

---

## 🔐 Authentication

This API uses **JWT (JSON Web Tokens)** for authentication.

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

The response contains an **access token** and a **refresh token**.

### Using the Token

Include the access token in the `Authorization` header for all protected endpoints:

```
Authorization: Bearer <access_token>
```

### Refresh Token

```http
POST /api/auth/refresh
X-Refresh-Token: <refresh_token>
```

| Token Type        | Expiry   |
|-------------------|----------|
| **Access Token**  | 10 hours |
| **Refresh Token** | 1 week   |

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint            | Description           | Auth Required |
|--------|---------------------|-----------------------|---------------|
| POST   | `/api/auth/login`   | Login                 | ❌            |
| POST   | `/api/auth/register`| Register new user     | ❌            |
| POST   | `/api/auth/refresh` | Refresh access token  | ❌            |

### Users

| Method | Endpoint                         | Description          | Auth Required |
|--------|----------------------------------|----------------------|---------------|
| GET    | `/api/user`                      | Get all users        | ✅ Admin      |
| GET    | `/api/user/{id}`                 | Get user by ID       | ✅            |
| PATCH  | `/api/user/{id}`                 | Update user profile  | ✅            |
| DELETE | `/api/user/{id}`                 | Delete user          | ✅            |
| PUT    | `/api/user/{id}/change-password` | Change password      | ✅            |

### Subjects

| Method | Endpoint                  | Description            | Auth Required |
|--------|---------------------------|------------------------|---------------|
| GET    | `/api/subject`            | Get all subjects       | ✅            |
| GET    | `/api/subject/{id}`       | Get subject by ID      | ✅            |
| POST   | `/api/subject`            | Create subject         | ✅            |
| POST   | `/api/subject/batch`      | Batch create subjects  | ✅            |
| PATCH  | `/api/subject/{id}`       | Update subject         | ✅            |
| DELETE | `/api/subject/{id}`       | Delete subject         | ✅            |

### Chapters

| Method | Endpoint                  | Description            | Auth Required |
|--------|---------------------------|------------------------|---------------|
| GET    | `/api/chapter`            | Get all chapters       | ✅            |
| GET    | `/api/chapter/{id}`       | Get chapter by ID      | ✅            |
| POST   | `/api/chapter`            | Create chapter         | ✅            |
| POST   | `/api/chapter/batch`      | Batch create chapters  | ✅            |
| PATCH  | `/api/chapter/{id}`       | Update chapter         | ✅            |
| DELETE | `/api/chapter/{id}`       | Delete chapter         | ✅            |

### Lessons

| Method | Endpoint                  | Description            | Auth Required |
|--------|---------------------------|------------------------|---------------|
| GET    | `/api/lesson`             | Get all lessons        | ✅            |
| GET    | `/api/lesson/{id}`        | Get lesson by ID       | ✅            |
| POST   | `/api/lesson`             | Create lesson          | ✅            |
| POST   | `/api/lesson/batch`       | Batch create lessons   | ✅            |
| PATCH  | `/api/lesson/{id}`        | Update lesson          | ✅            |
| DELETE | `/api/lesson/{id}`        | Delete lesson          | ✅            |

### Lesson Sections

| Method | Endpoint                        | Description               | Auth Required |
|--------|---------------------------------|---------------------------|---------------|
| GET    | `/api/lesson-section`           | Get all sections          | ✅            |
| GET    | `/api/lesson-section/{id}`      | Get section by ID         | ✅            |
| POST   | `/api/lesson-section`           | Create section (multipart)| ✅            |
| POST   | `/api/lesson-section/batch`     | Batch create sections     | ✅            |
| PATCH  | `/api/lesson-section/{id}`      | Update section            | ✅            |
| DELETE | `/api/lesson-section/{id}`      | Delete section            | ✅            |

### Quizzes

| Method | Endpoint              | Description           | Auth Required |
|--------|-----------------------|-----------------------|---------------|
| GET    | `/api/quiz`           | Get all quizzes       | ✅            |
| GET    | `/api/quiz/{id}`      | Get quiz by ID        | ✅            |
| GET    | `/api/quiz/review`    | Get quiz with answers | ✅            |
| POST   | `/api/quiz`           | Create quiz           | ✅            |
| POST   | `/api/quiz/batch`     | Batch create quizzes  | ✅            |
| PATCH  | `/api/quiz/{id}`      | Update quiz           | ✅            |
| DELETE | `/api/quiz/{id}`      | Delete quiz           | ✅            |

### Exams

| Method | Endpoint              | Description         | Auth Required |
|--------|-----------------------|---------------------|---------------|
| GET    | `/api/exam`           | Get all exams       | ✅            |
| GET    | `/api/exam/{id}`      | Get exam by ID      | ✅            |
| POST   | `/api/exam`           | Create exam         | ✅            |
| PATCH  | `/api/exam/{id}`      | Update exam         | ✅            |
| DELETE | `/api/exam/{id}`      | Delete exam         | ✅            |

### Exam Results

| Method | Endpoint                    | Description            | Auth Required |
|--------|-----------------------------|------------------------|---------------|
| POST   | `/api/exam-result/submit`   | Submit exam answers    | ✅            |
| GET    | `/api/exam-result`          | Get user exam results  | ✅            |
| GET    | `/api/exam-result/{id}`     | Get result details     | ✅            |

### Media

| Method | Endpoint                        | Description             | Auth Required |
|--------|---------------------------------|-------------------------|---------------|
| POST   | `/api/media/upload/public`      | Upload public media     | ✅            |
| POST   | `/api/media/upload/private`     | Upload private media    | ✅            |
| GET    | `/api/media/public/{id}`        | View public media       | ❌            |
| GET    | `/api/media/private/{id}`       | View private media      | ✅            |

---

## 🗂️ Project Structure

```
courses_api_server/
├── src/
│   ├── main/
│   │   ├── java/com/example/course/
│   │   │   ├── config/           # Application configuration classes
│   │   │   ├── constant/         # Enums and constants
│   │   │   ├── controller/       # REST API controllers
│   │   │   ├── exception/        # Custom exception handlers
│   │   │   ├── filter/           # Request filtering and pagination
│   │   │   ├── mapper/           # MapStruct entity/DTO mappers
│   │   │   ├── model/            # Entities and DTOs (request/response)
│   │   │   ├── permission/       # Custom authorization logic
│   │   │   ├── repository/       # Spring Data JPA repositories
│   │   │   ├── security/         # JWT and Spring Security configuration
│   │   │   ├── service/          # Business logic layer
│   │   │   ├── specification/    # JPA Specifications for dynamic queries
│   │   │   ├── utils/            # Utility classes
│   │   │   └── CourseApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       └── application-staging.yaml
│   └── test/
│       └── java/com/example/course/
│           └── CourseApplicationTests.java
├── Dockerfile
├── docker-compose.yaml
├── pom.xml
└── .env.local
```

---

## 🗄️ Data Model

```
Subject
  └── Chapter (one-to-many)
        └── Lesson (one-to-many)
              └── LessonSection (one-to-many)

Subject
  ├── Quiz (one-to-many)
  └── Exam (one-to-many)
        └── Quiz (many-to-many)

User
  └── ExamResult (one-to-many)
        └── Exam (many-to-one)
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 🐳 Docker Compose Services

The `docker-compose.yaml` file defines the following services:

| Service      | Image              | Port   | Description          |
|--------------|--------------------|--------|----------------------|
| `postgres`   | postgres:16-alpine | 5432   | PostgreSQL database  |
| `redis`      | redis:7-alpine     | 6379   | Redis cache          |

### Stop Services

```bash
docker-compose down
```

To remove volumes (wipes database data):

```bash
docker-compose down -v
```

---

## 🌱 Seed Data

When running with the `dev` profile, the `DataSeeder` automatically populates the database with sample data on the first run, including subjects, chapters, lessons, quizzes, and users.

---

## 🔧 Spring Profiles

| Profile   | Description                              |
|-----------|------------------------------------------|
| `dev`     | Local development with data seeding      |
| `staging` | Staging environment configuration        |

Set the active profile via environment variable:

```bash
export SPRING_PROFILES_ACTIVE=dev
```

---

## 📄 License

This project is for educational purposes.
