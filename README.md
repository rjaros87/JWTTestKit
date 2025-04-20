# JWT Test Kit 🔐

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Micronaut](https://img.shields.io/badge/Micronaut-4.8-green.svg)](https://micronaut.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

<a href="https://www.buymeacoffee.com/rjaros87" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" height="40px"></a>

A powerful Micronaut-based toolkit for generating and managing JWT tokens for testing purposes. Perfect for developers 
who need to simulate identity provider tokens during development and testing phases using **JWKS** endpoint.

## ✨ Features

- 🎯 Generate JWT tokens with custom claims
- 🔑 JWKS endpoint for key management. See [JWKS Configuration](#jwks-configuration)
- 🔍 Decode and inspect JWT tokens (signed and unsigned)
- 🔄 Support for common identity providers:
  - AWS Cognito
  - Okta
  - Custom
- 📚 Interactive Swagger UI documentation
- 🏥 Health check endpoints for Kubernetes
- 🐳 Docker support
- ⚡ Native image support with GraalVM
- ⎈ Helm chart for easy deployment on Kubernetes

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

### ⎈ Helm chart
To deploy the JWT Test Kit on Kubernetes, you can use the provided Helm chart. Follow these steps:
- Add the Helm repository:
```bash
helm repo add jwt-testkit https://rjaros87.github.io/JWTTestKit
```
- Update the Helm repository:
```bash
helm repo update
```
- Install the JWT Test Kit chart:
```bash
helm install jwt-testkit jwt-testkit/jwt-testkit
```
- Access the application using the service name `jwt-test-kit` and the port default port 80 for http in your Kubernetes cluster.

## 🔧 API Endpoints

[<img src="https://fetch.usebruno.com/button.svg" alt="Fetch in Bruno" style="width: 130px; height: 30px;" width="128" height="32">](https://fetch.usebruno.com?url=https%3A%2F%2Fgithub.com%2Frjaros87%2FJWTTestKit.git "target=_blank rel=noopener noreferrer")

### 📚 JWT Token Generation
- **Swagger UI:** Access the interactive API documentation at http://localhost:8080/api
- **OpenAPI Spec:** Available at http://localhost:8080/swagger/api.yml

### Management

#### Health Check
- **URL:** `/health`
- **Method:** `GET`
- **Port:** 8082 (configurable via `MANAGEMENT_PORT`)

## ⚙️ Configuration

Key configurations can be adjusted in `application.yml`:
- Server ports
- JWT signing keys
- Token expiration times
- Management endpoints

### JWKS Configuration
To configure JWKS in your Micronaut application, add the following properties to your `application.yml` file:

```yaml
micronaut:
  security:
    enabled: true
    token:
      jwt:
        signatures:
          jwks:
            custom:
              url: http://localhost:8080/JWTTestKit/jwks
```
#### Exposed Environment Variables

You can configure the application using the following environment variables:

- `APPLICATION_PORT` – The port on which the JWT Test Kit application runs (default: `8080`).
- `APPLICATION_HOST` – The host address where the JWT Test Kit application runs (default: `localhost`).
- `APPLICATION_SCHEME` – The protocol used by the JWT Test Kit application (default: `http`).

## 🛠️ Building from Source

```bash
./gradlew clean build
```

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.