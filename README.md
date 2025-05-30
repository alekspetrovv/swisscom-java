# Swisscom Service Management - Spring Boot Backend

This is the Spring Boot backend application for the Swisscom Service CRUD task. It provides a REST API to manage Services, their nested Resources, and their nested Owners, persisting data in MongoDB.

## Requirements

* **Java JDK:** Version 24 or later (as specified in `pom.xml`).
* **Maven:** Apache Maven 3.6+ (or use the included Maven Wrapper `./mvnw`).
* **MongoDB:** A running MongoDB instance.
    * The application can be configured to connect to your MongoDB instance.
    * A `docker-compose.yml` file is provided in the project root to easily start a MongoDB container pre-configured with a `test` user and `swiss` database, which matches the default `application.yaml`. To use it:
        ```bash
        docker compose up -d or 
        docker-compose up -d 
        ```
      To stop it:
        ```bash
        docker compose down or 
        docker-compose down
        ```

## Setup and Building

1.  **Clone the repository:**
    ```bash
    git clone <your-backend-repo-url>
    cd <your-backend-project-directory>
    ```
2.  **Setup MongoDB Instance**
    ```bash
    docker compose up -d or 
    docker-compose up -d 
    ```

3. **Build the project using Maven:**
    (Using the Maven Wrapper is recommended as it uses the project-defined version)
    ```bash
    ./mvnw clean install
    ```
    Or, if you have Maven installed globally:
    ```bash
    mvn clean install
    ```
    This will generate a JAR file in the `target/` directory (e.g., `crud-0.0.1-SNAPSHOT.jar`).

## Configuration

The application uses YAML for configuration (`src/main/resources/application.yaml`).

### Database Configuration
* **Default (application.yaml):** Configured to connect to `mongodb://test:test@localhost/swiss?authSource=admin`. This matches the `docker-compose.yml` setup.
* **Profiles:** The application supports Spring Profiles for different configurations.
    * `application-staging.yaml` is provided as an example, configured to use `mongodb://test:test@localhost/swiss-stage?authSource=admin`.
    * You can create other profile-specific files like `application-prod.yml`.

### Activating Profiles
You can activate a specific profile in several ways:
* **Environment Variable:**
    ```bash
    export SPRING_PROFILES_ACTIVE=staging
    java -jar target/*.jar
    ```
* **JVM System Property:**
    ```bash
    java -jar -Dspring.profiles.active=staging target/*.jar
    ```
* **In `application.yaml` (for a default profile):**
    ```yaml
    spring:
      profiles:
        active: dev # (if you create an application-dev.yaml)
    ```
  The default `application.yaml` implies a "DEV" setup by its `custom.profileInfo` message.

## Running the Application

1.  **Ensure MongoDB is running** and accessible as per your active profile's configuration. (Using the provided `docker-compose.yml` is recommended for local setup).
2.  **Build the application** (if not already done):
    ```bash
    ./mvnw clean install -DskipTests
    ```
3.  **Run the JAR file:**
    Navigate to the project's root directory and run:
    ```bash
    java -jar target/crud-0.0.1-SNAPSHOT.jar
    ```
    (Replace `crud-0.0.1-SNAPSHOT.jar` with the actual JAR file name in your `target` directory).

    To run with a specific profile (e.g., `staging`):
    ```bash
    java -jar target/crud-0.0.1-SNAPSHOT.jar --spring.profiles.active=staging
    ```
4.  **Using the Bash Script:**
    A simple bash script `run-backend.sh` is provided to build and run the application.
    ```bash
    chmod +x run-backend.sh
    ./run-backend.sh          # Runs with default profile
    ./run-backend.sh staging  # Runs with 'staging' profile
    ```

The application will start, typically on `http://localhost:4005`.

## Frontend Application

This backend application provides the REST API. To interact with the service management system through a graphical user interface, a companion frontend application is available.

* **Frontend Repository:** [Swisscom Angular Frontend](https://github.com/alekspetrovv/swisscom-frontend)
* **Setup Instructions:** Please refer to the `README.md` file within the frontend repository for detailed instructions on how to set up and run the frontend application. It is designed to connect to this backend.


## Key Features Implemented

* REST API for full CRUD operations on `Service`, `Resource`, and `Owner` entities.
* Nested data structure: `Service` -> `Resource`s -> `Owner`s.
* Persistence in MongoDB.
* Optimistic locking for `Service` updates (`@Version`).
* Local in-memory caching for `Service` GET operations.
* Spring Profile configuration for different environments.
* Global exception handling.
* DTOs for API contracts and validation for incoming data.