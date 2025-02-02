# JWT Test Kit 🔐

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Micronaut](https://img.shields.io/badge/Micronaut-4.7-green.svg)](https://micronaut.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A powerful Micronaut-based toolkit for generating and managing JWT tokens for testing purposes. Perfect for developers who need to simulate identity provider tokens during development and testing phases.

## ✨ Features

- 🎯 Generate JWT tokens with custom claims
- 🔄 Support for common identity providers:
  - Okta
  - AWS Cognito
- 📚 Interactive Swagger UI documentation
- 🏥 Health check endpoints for Kubernetes
- 🐳 Docker support
- ⚡ Native image support with GraalVM

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.x (included via wrapper)

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/rjaros87/JWTTestKit.git
   cd JWTTestKit
   ```

2. Build and run using Gradle:
   ```bash
   ./gradlew run
   ```

   Or build a native image:
   ```bash
   ./gradlew nativeCompile
   ```

3. The application will start on port 8080 by default

### 🐳 Docker Support

Build Docker image:
```bash
./gradlew dockerBuild
```

Build native Docker image:
```bash
./gradlew dockerBuildNative
```

## 🔧 API Endpoints

### JWT Token Generation

#### Create Okta Token
- **URL:** `/JWTTestKit/token/okta`
- **Method:** `POST`
- **Request Body Example:**
  ```json
  {
    "sub": "user123",
    "email": "user@example.com",
    "groups": ["admin", "user"]
  }
  ```

#### Create Custom Token
- **URL:** `/JWTTestKit/token/custom`
- **Method:** `POST`
- **Request Body:** Flexible JSON object with custom claims

### Management

#### Health Check
- **URL:** `/health`
- **Method:** `GET`
- **Port:** 8082 (configurable via `MANAGEMENT_PORT`)

## 📚 Documentation

- **Swagger UI:** Access the interactive API documentation at http://localhost:8080/api
- **OpenAPI Spec:** Available at http://localhost:8080/swagger/api.yml

## ⚙️ Configuration

Key configurations can be adjusted in `application.yml`:
- Server ports
- JWT signing keys
- Token expiration times
- Management endpoints

## 🛠️ Building from Source

```bash
./gradlew clean build
```

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.