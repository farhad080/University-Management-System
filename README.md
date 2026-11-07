# Apex University Management System
### CSE 3230 Home Task — 4th Lab

A comprehensive, clean, and robust University Management System built with **Java Spring Boot**, **Spring Security (RBAC)**, **Spring Data JPA**, and **Docker**.

---

## 📌 Project Overview & Requirements Mapping

| Requirement | Implementation Details | Status |
| :--- | :--- | :---: |
| **REST API** | Full CRUD operations for `Admin`, `Teacher`, and `Student` resources using proper REST conventions, JSON responses, and standard HTTP status codes (`200`, `201`, `204`, `400`, `401`, `403`, `404`, `409`). | ✅ Complete |
| **Spring Security** | Session and HTTP Basic authentication, login/logout endpoints, password hashing using `BCryptPasswordEncoder`, and Role-Based Access Control (RBAC). | ✅ Complete |
| **RBAC Rules** | **Admins** manage all three resources; **Teachers** manage only Teachers; **Students** manage only Students. Unauthorized access returns `403 Forbidden`. | ✅ Complete |
| **Dockerization** | Multi-stage `Dockerfile` and `compose.yaml` configuring application and `postgres:16-alpine` database service to run together using Docker Compose. | ✅ Complete |
| **Web Dashboard** | Responsive, modern Single-Page Web Dashboard (`index.html`, `style.css`, `app.js`) with role switcher, directory tables, modals, and a **Live RBAC API Tester**. | ✅ Complete |

---

## 👥 Default Demo Credentials

The system automatically initializes and seeds these accounts on startup with **BCrypt-hashed** passwords:

| Role | Username | Password | Permitted Resources |
| :--- | :--- | :--- | :--- |
| **🛡️ Admin** | `admin` | `admin123` | Full CRUD on `Admins`, `Teachers`, and `Students` |
| **👨‍🏫 Teacher** | `teacher` | `teacher123` | Full CRUD on `Teachers` only (`Admins` & `Students` are forbidden) |
| **🎓 Student** | `student` | `student123` | Full CRUD on `Students` only (`Admins` & `Teachers` are forbidden) |

---

## 🔐 Role-Based Access Control (RBAC) Matrix

| Endpoint | HTTP Method | Admin (`ROLE_ADMIN`) | Teacher (`ROLE_TEACHER`) | Student (`ROLE_STUDENT`) | Unauthenticated |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `/api/auth/login` | `POST` | 200 OK | 200 OK | 200 OK | 200 OK |
| `/api/auth/logout` | `POST` | 200 OK | 200 OK | 200 OK | 200 OK |
| `/api/auth/me` | `GET` | 200 OK | 200 OK | 200 OK | 401 Unauthorized |
| `/api/admins/**` | `GET`, `POST`, `PUT`, `DELETE` | **200 / 201 / 204** | **403 Forbidden** | **403 Forbidden** | 401 Unauthorized |
| `/api/teachers/**` | `GET`, `POST`, `PUT`, `DELETE` | **200 / 201 / 204** | **200 / 201 / 204** | **403 Forbidden** | 401 Unauthorized |
| `/api/students/**` | `GET`, `POST`, `PUT`, `DELETE` | **200 / 201 / 204** | **403 Forbidden** | **200 / 201 / 204** | 401 Unauthorized |

---

## 📡 REST API Specification

### 1. Authentication Endpoints (`/api/auth`)
- `POST /api/auth/login` — Authenticate credentials, create session, return user role.
  - Payload: `{"username": "admin", "password": "admin123"}`
- `POST /api/auth/logout` — Invalidate session and clear security context.
- `GET /api/auth/me` — Return profile of currently authenticated user.
- `GET /api/auth/status` — Return authentication status and granted authorities.

### 2. Admin Resource (`/api/admins`) — *ROLE_ADMIN only*
- `GET /api/admins` — List all administrators (`200 OK`).
- `GET /api/admins/{id}` — Retrieve administrator by ID (`200 OK` or `404 Not Found`).
- `POST /api/admins` — Create a new administrator (`201 Created`).
- `PUT /api/admins/{id}` — Update administrator details (`200 OK`).
- `DELETE /api/admins/{id}` — Delete administrator (`204 No Content`).

### 3. Teacher Resource (`/api/teachers`) — *ROLE_ADMIN & ROLE_TEACHER*
- `GET /api/teachers` — List all teachers (`200 OK`).
- `GET /api/teachers/{id}` — Retrieve teacher by ID (`200 OK`).
- `POST /api/teachers` — Create a new teacher (`201 Created`).
- `PUT /api/teachers/{id}` — Update teacher details (`200 OK`).
- `DELETE /api/teachers/{id}` — Delete teacher (`204 No Content`).

### 4. Student Resource (`/api/students`) — *ROLE_ADMIN & ROLE_STUDENT*
- `GET /api/students` — List all students (`200 OK`).
- `GET /api/students/{id}` — Retrieve student by ID (`200 OK`).
- `POST /api/students` — Create a new student (`201 Created`).
- `PUT /api/students/{id}` — Update student details (`200 OK`).
- `DELETE /api/students/{id}` — Delete student (`204 No Content`).

---

## 🚀 How to Run the Application

### Option A: Running with Docker Compose (Recommended for Containerization Evaluation)
Requirements: Docker & Docker Compose installed.

```bash
# Build the images and start both Spring Boot app and PostgreSQL DB
docker compose up --build
```
- Access Web UI at: `http://localhost:8088/`
- PostgreSQL is accessible at `localhost:5432` (`university_db`)

To stop:
```bash
docker compose down -v
```

---

### Option B: Running Locally (Fastest for Development)
Requirements: Java 17+ installed.

```bash
# Run using the Maven Wrapper (uses in-memory H2 database by default)
./mvnw spring-boot:run

# Or run the packaged JAR directly:
java -jar target/demo-0.0.1-SNAPSHOT.jar
```
- Access Web UI at: `http://localhost:8088/`
- Access H2 Database Console at: `http://localhost:8088/h2-console`
  - JDBC URL: `jdbc:h2:mem:university_db`
  - User: `sa`
  - Password: *(leave blank)*

---

## 🧪 Running Automated Tests

To run the complete automated integration test suite (covering BCrypt hashing, CRUD operations, unauthenticated 401 checks, and RBAC 403 checks):

```bash
./mvnw test
```

Expected result:
```
[INFO] Results:
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🏗️ Architecture & Security Highlights

1. **Joined Table Inheritance**:
   The base `User` entity holds common credentials (`username`, `password`, `email`, `role`). `Admin`, `Teacher`, and `Student` inherit from `User` using `@Inheritance(strategy = InheritanceType.JOINED)`.
2. **Password Hashing**:
   `BCryptPasswordEncoder` ensures passwords are never stored in plain text.
3. **DTO Decoupling & Password Privacy**:
   Input requests are validated using Jakarta Validation (`@NotBlank`, `@Email`, `@Size`, etc.). Response DTOs completely omit password fields to prevent credential leaks.
4. **Global Exception Handling**:
   `GlobalExceptionHandler` converts validation failures into `400 Bad Request`, resource lookup failures into `404 Not Found`, constraint violations into `409 Conflict`, and permission violations into `403 Forbidden`.
