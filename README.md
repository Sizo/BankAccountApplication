# Bank Account Application

## Overview
Enhanced banking application demonstrating modern Spring Boot 3.5.5 architecture a good separation of concerns/modularization, observability via logs, and business rule validation. This project showcases enterprise-grade development practices through robust design patterns and cutting-edge technology integration.

### The approach taken
**OpenAPI-First Approach with Code Generation**:
- Contract-first development where API specification drives implementation, ensuring consistency across the application:
- Automatic generation of DTOs and interfaces using OpenAPI Generator 7.0.1
- Swagger UI integration for interactive API documentation
- Schema-based request/response validation with Bean Validation 2.0
- Type-safe client/server contract enforcement


**Modern Tech Stack**: Built on Spring Boot 3.5.5 with Java 21, leveraging the latest enterprise framework capabilities for enhanced performance and security. The application uses Gradle for modern build management and dependency resolution with advanced build optimization.

**Clean Architecture with Proper Separation of Concerns**: Implements Spring MVC pattern with clear layering:
- **Controller layer** for RESTful endpoints with proper HTTP status handling
- **Service layer** for business logic encapsulation with comprehensive transaction management
- **Repository layer** for data access abstraction using Spring Data JPA
- **Rich Domain models** with business validation and type safety


**Comprehensive Exception Handling via GlobalExceptionHandler**: Centralized exception management with:
- Proper HTTP status mapping for different error scenarios
- Custom business exceptions with meaningful error codes
- Bean Validation 2.0 integration for request validation
- RFC 7807 Problem Details format for standardized error responses

**MapStruct for Efficient Mapping**: Compile-time mapping generation that:
- Eliminates runtime reflection overhead for optimal performance
- Provides type-safe transformations with zero runtime performance impact
- Supports complex business rule mappings with custom conversion logic
- Integrates seamlessly with Lombok for reduced boilerplate

**Checkstyle Integration for Code Quality**: Google Java Style Guide enforcement with:
- Custom rules tailored for enterprise development standards
- Automated code quality checks integrated into the build pipeline
- Real-time IDE feedback for consistent code standards
- Configurable severity levels and suppressions for flexibility

## Technology Stack

### Core Framework
- **Spring Boot 3.5.5** - Latest enterprise framework with enhanced security and performance
- **Java 21** - Modern JVM with advanced language features and optimizations
- **Gradle 8.x** - Advanced build system with dependency management and optimization

### Persistence & Database
- **Spring Data JPA** - Advanced ORM with repository pattern implementation
- **H2 Database** - In-memory database for development and testing
- **Hibernate** - Advanced JPA implementation with performance optimizations

### API & Documentation
- **OpenAPI 3.0.3** - Modern API specification standard
- **OpenAPI Generator 7.0.1** - Code generation for type-safe contracts
- **Swagger UI** - Interactive API documentation and testing interface

### Fault Tolerance & Resilience
- **Resilience4j 2.2.0** - Modern fault tolerance library with comprehensive patterns:
  - **Circuit Breaker**: Prevents cascade failures when downstream services are unavailable
  - **Retry**: Intelligent retry mechanisms with exponential backoff for transient failures
  - **Rate Limiter**: Protects system resources from overload and abuse
  - **Bulkhead**: Thread pool isolation for different operations
  - **Metrics Integration**: Comprehensive monitoring via Micrometer
- **Spring Retry** - Method-level retry annotations with AOP support
- **Spring AOP** - Aspect-oriented programming for cross-cutting concerns

### Code Quality & Mapping
- **MapStruct 1.5.5** - Compile-time bean mapping with performance optimization
- **Lombok** - Code generation for reducing boilerplate
- **Checkstyle 10.20.0** - Static code analysis with Google style enforcement

### Observability & Monitoring
- **Spring Boot Actuator** - Production-ready monitoring and management endpoints
- **SLF4J + Logback** - Structured logging with configurable appenders
- **Distributed Tracing**: Request correlation with trace IDs

## Architecture Highlights

### Clean Architecture Implementation
```
┌─ Controller Layer (REST Endpoints)
│  ├─ BankAccountController - Withdrawal operations
│  └─ Global Exception Handler - Centralized error handling
│
├─ Service Layer (Business Logic)
│  ├─ AccountService - Core banking operations with fault tolerance
│  └─ TransactionEventService - Event publishing with resilience patterns
│
├─ Repository Layer (Data Access)
│  └─ AccountRepository - JPA-based data persistence
│
└─ Domain Layer (Business Models)
   ├─ Entities - JPA entity mappings
   ├─ DTOs - API contract definitions
   └─ Domain Objects - Business logic encapsulation
```

### Comprehensive Fault Tolerance Implementation

The application implements enterprise-grade fault tolerance patterns using both **Spring Retry** and **Resilience4j** to ensure robust operation under various failure scenarios:

#### Multi-Layer Retry Strategy
```java
@Transactional(isolation = Isolation.READ_COMMITTED, timeout = 30)
@Retry(name = "accountService", fallbackMethod = "fallbackProcessWithdrawal")
@CircuitBreaker(name = "accountService", fallbackMethod = "fallbackProcessWithdrawal")
@RateLimiter(name = "accountService")
@Retryable(
    retryFor = {OptimisticLockException.class, Exception.class},
    noRetryFor = {IllegalArgumentException.class, EntityNotFoundException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 500, multiplier = 2.0, maxDelay = 2000)
)
```

#### Fault Tolerance Patterns Implemented

1. **Optimistic Locking with Retry**
   - Handles concurrent database updates using JPA `@Version` annotation
   - Automatic retry on `OptimisticLockException` with exponential backoff
   - Prevents lost updates while maintaining high performance

2. **Circuit Breaker Pattern**
   - Monitors failure rates and opens circuit when threshold is exceeded
   - Prevents cascade failures by failing fast when services are down
   - Automatic recovery with half-open state testing

3. **Rate Limiting**
   - Prevents system overload by limiting requests per time window
   - Configurable limits per service instance
   - Graceful degradation under high load

4. **Bulkhead Isolation**
   - Separates thread pools for different operations
   - Prevents resource exhaustion from affecting critical operations
   - Enhanced system stability under stress

5. **Intelligent Retry Logic**
   - **Exponential Backoff**: Progressively increases delay between retries
   - **Selective Retry**: Only retries transient failures, not business logic errors
   - **Maximum Attempts**: Prevents infinite retry loops

#### Concurrency Management
- **Transaction Isolation**: Uses `READ_COMMITTED` for optimal balance of consistency and performance
- **Optimistic Locking**: Prevents lost updates in high-concurrency scenarios
- **Version-based Conflict Resolution**: Automatic handling of concurrent account updates

#### Monitoring and Observability
- **Health Endpoints**: Real-time circuit breaker and rate limiter status
- **Metrics Collection**: Detailed statistics on retries, failures, and recovery
- **Structured Logging**: Comprehensive audit trail of all fault tolerance actions

### Configuration Example
```properties
# Retry Configuration
resilience4j.retry.instances.accountService.max-attempts=3
resilience4j.retry.instances.accountService.wait-duration=500ms
resilience4j.retry.instances.accountService.exponential-backoff-multiplier=2
resilience4j.retry.instances.accountService.retry-exceptions=java.lang.Exception,jakarta.persistence.OptimisticLockException

# Circuit Breaker Configuration
resilience4j.circuitbreaker.instances.accountService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.accountService.wait-duration-in-open-state=30s
resilience4j.circuitbreaker.instances.accountService.sliding-window-size=10

# Rate Limiter Configuration
resilience4j.ratelimiter.instances.accountService.limit-for-period=10
resilience4j.ratelimiter.instances.accountService.limit-refresh-period=1s
```

## Configuration Management

### Environment-Specific Configurations
- **`application.yaml`** - Base configuration with common settings shared across all environments
- **`application-dev.yaml`** - Development environment settings with enhanced logging and debugging features
- **`application-prod.yaml`** - Production environment optimizations with performance tuning and security hardening

The application supports Spring profiles for environment-specific configurations:
```bash
# Development environment
./gradlew bootRun --args='--spring.profiles.active=dev'

# Production environment
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Development Configuration (`application-dev.yaml`)
- Enhanced SQL logging with query formatting
- H2 console enabled for database inspection
- Detailed debug logging for troubleshooting
- Relaxed security settings for easier development

### Production Configuration (`application-prod.yaml`)
- Optimized connection pooling for high throughput
- Minimal logging to reduce overhead
- Security hardening with proper authentication
- Performance monitoring and metrics collection

## Future/Potential improvements
### Observability Strategy
- **Metrics Collection**: Performance and business metrics via Micrometer
- **Structured Logging**: JSON-formatted logs for centralized log management

### Resilience4j Configuration
```properties

## Getting Started

### Prerequisites
- **Java 21 or higher** - Required for Spring Boot 3.5.5 compatibility
- **Git** - For cloning the repository
- **IDE** (Optional) - IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Gradle 8.x** (Optional) - The project includes Gradle wrapper

### Installation & Setup

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd BankAccountApplication
```

#### 2. Verify Java Installation
```bash
java -version
# Should show Java 21 or higher
```

#### 3. Build the Application
```bash
# On Windows
.\gradlew build

# On Linux/Mac
./gradlew build
```

This command will:
- Download all dependencies
- Generate OpenAPI interfaces from the specification
- Compile the application
- Run Checkstyle code quality checks
- Execute all tests

### Running the Application

#### Quick Start (Development Mode)
```bash
# On Windows
.\gradlew bootRun

# On Linux/Mac
./gradlew bootRun
```

#### Environment-Specific Startup

**Development Environment** (Enhanced logging, H2 console enabled):
```bash
# On Windows
.\gradlew bootRun --args="--spring.profiles.active=dev"

# On Linux/Mac
./gradlew bootRun --args="--spring.profiles.active=dev"
```

**Production Environment** (Optimized settings, minimal logging):
```bash
# On Windows
.\gradlew bootRun --args="--spring.profiles.active=prod"

# On Linux/Mac
./gradlew bootRun --args="--spring.profiles.active=prod"
```

#### Alternative Running Methods

**Using Java directly:**
```bash
# Build the JAR first
.\gradlew bootJar

# Run the JAR
java -jar build/libs/BankAccountApplication-0.0.1-SNAPSHOT.jar

# With specific profile
java -jar build/libs/BankAccountApplication-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

**Using your IDE:**
1. Import the project as a Gradle project
2. Run the `BankAccountApplication.java` main class
3. Add VM options for profiles: `-Dspring.profiles.active=dev`

### Application Endpoints

Once started, the application will be available at:
- **Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **H2 Console** (dev profile only): http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

### Database Access (Development)

**H2 Console Access** (when using dev profile):
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:bankdb`
- Username: `sa`
- Password: (leave blank)

### Configuration Files

The application uses YAML configuration files:
- **`application.yaml`** - Base configuration with common settings
- **`application-dev.yaml`** - Development environment with enhanced logging and debugging
- **`application-prod.yaml`** - Production environment with performance optimizations

### Troubleshooting

#### Common Issues

**Port Already in Use:**
```bash
# Change the port in application.yaml or use command line
.\gradlew bootRun --args="--server.port=8081"
```

**Java Version Issues:**
```bash
# Check Java version
java -version
# Ensure JAVA_HOME points to Java 21+
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME% # Windows
```

**Build Failures:**
```bash
# Clean and rebuild
.\gradlew clean build
```

#### Logs and Debugging

**Development Environment** provides detailed logging:
- SQL queries are logged and formatted
- Transaction details are visible
- Debug-level logging for application packages

**Check Application Status:**
```bash
curl http://localhost:8080/actuator/health
```

## Design Patterns & Architecture

This application demonstrates several enterprise design patterns and architectural principles that showcase senior-level software engineering expertise:

### 1. **Repository Pattern**
**Location**: `AccountRepository extends JpaRepository`
**Purpose**: Abstracts data access logic and provides a consistent interface for data operations
**Benefits**: 
- Decouples business logic from data access technology
- Enables easy testing with mock repositories
- Supports different data sources without changing business logic

### 2. **Strategy Pattern**
**Location**: Configuration classes and service layer
**Purpose**: Provides flexible configuration strategies for different environments and use cases
**Implementation**:
```java
// Environment-specific database configurations
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
public class DevelopmentDatabaseConfig {
    // Enhanced logging and H2 console enabled
}

@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod") 
public class ProductionDatabaseConfig {
    // Optimized performance settings
}
```
**Benefits**: Seamlessly switch between development and production configurations without code changes

### 3. **Template Method Pattern**
**Location**: Connection pool configuration and service layer operations
**Purpose**: Defines algorithm structure while allowing subclasses to override specific steps
**Benefits**: Consistent setup patterns with environment-specific optimizations

### 4. **Circuit Breaker Pattern**
**Location**: `@CircuitBreaker` annotations in `AccountService`
**Purpose**: Prevents cascade failures and provides fallback mechanisms
**Implementation**: Resilience4j with configurable failure thresholds and recovery times

### 5. **Retry Pattern**
**Location**: `@Retry` and `@Retryable` annotations
**Purpose**: Handles transient failures with exponential backoff
**Benefits**: Improves system resilience against temporary infrastructure issues

### 6. **Bulkhead Pattern**
**Location**: Resilience4j bulkhead configuration
**Purpose**: Isolates resources to prevent complete system failure
**Implementation**: Separate thread pools for different operations

### 7. **Observer Pattern**
**Location**: Event publishing system (`TransactionEventService`, `SnsEventPublisher`)
**Purpose**: Decouples event production from consumption
**Benefits**: Enables asynchronous processing and loose coupling

### 8. **Factory Pattern**
**Location**: OpenAPI code generation and DTO creation
**Purpose**: Creates objects without specifying exact classes
**Benefits**: Consistent object creation and easy extensibility

### 9. **Facade Pattern**
**Location**: `BankAccountController` and service layer
**Purpose**: Provides simplified interface to complex subsystems
**Benefits**: Hides complexity and provides clean API interface

### 10. **Dependency Injection Pattern**
**Location**: Throughout the application with `@RequiredArgsConstructor`
**Purpose**: Inverts control and manages dependencies
**Benefits**: Loose coupling, easier testing, and better maintainability

### 11. **Command Pattern**
**Location**: `WithdrawalDo` domain objects and service methods
**Purpose**: Encapsulates requests as objects
**Benefits**: Enables queuing, logging, and undo operations

### 12. **Adapter Pattern**
**Location**: `MapStruct` mappers between DTOs and domain objects
**Purpose**: Converts interfaces between incompatible classes
**Benefits**: Clean separation between API contracts and internal models

## Containerization & Deployment

### Docker Support with JIB
**Multi-Platform Container Images**: Supports both AMD64 and ARM64 architectures
**Optimized JVM Settings**: Configured for container environments with memory limits
**Production-Ready Configuration**: Uses Eclipse Temurin JRE 21 with G1GC

### Build Commands:
```bash
# Build container image
./gradlew jibDockerBuild

# Build and push to registry
./gradlew jib

# Build for local Docker daemon
./gradlew jibDockerBuild --image=bankapp:latest
```

## API Documentation

### Swagger UI Integration
- **Interactive Documentation**: Available at `/swagger-ui.html`
- **OpenAPI Specification**: Available at `/v3/api-docs`
- **Try It Out**: Test endpoints directly from the documentation
- **Multiple Environments**: Configured for development, staging, and production

### API Endpoints
Once started, access the interactive documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
