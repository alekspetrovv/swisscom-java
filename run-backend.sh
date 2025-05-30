#!/bin/bash

echo "====================================================="
echo " Swisscom Service Management - Spring Boot Backend "
echo "====================================================="
echo ""

# Check for Java
echo "Checking for Java..."
if ! command -v java > /dev/null; then
  echo "ERROR: Java is not installed. Please install Java JDK (e.g., version 24 as per pom.xml)."
  exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | awk -F '\\\"' '/version/ {print $2}')
echo "Java found: Version $JAVA_VERSION"
echo ""

# Check for Maven Wrapper or Maven
MVN_CMD="./mvnw"
if [ ! -f "$MVN_CMD" ]; then
    if ! command -v mvn > /dev/null; then
        echo "ERROR: Maven (mvn) is not installed, and Maven wrapper (mvnw) not found."
        echo "Please install Maven or ensure your project has the Maven wrapper."
        exit 1
    fi
    MVN_CMD="mvn" # Fallback to system Maven
fi
echo "Using Maven command: $MVN_CMD"
echo ""

echo "Building the Spring Boot application with Maven (skipping tests)..."
"$MVN_CMD" clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Maven build failed. Please check for errors above."
    exit 1
fi
echo "Build successful."
echo ""

# Find the JAR file in the target directory (handles varying snapshot versions)
JAR_FILE=$(find target -maxdepth 1 -name "crud-*.jar" -type f -print -quit)

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: No JAR file matching crud-*.jar found in target directory after build."
    exit 1
fi

echo "Found application JAR: $JAR_FILE"
echo ""

PROFILE_ARG=""
ACTIVE_PROFILE_MESSAGE="default profile(s) from application.yaml"

# Check if a profile argument is provided to the script
if [ ! -z "$1" ]; then
    PROFILE_ARG="--spring.profiles.active=$1"
    ACTIVE_PROFILE_MESSAGE="profile: $1"
    echo "Attempting to run with $ACTIVE_PROFILE_MESSAGE"
else
    echo "No profile specified as argument, running with $ACTIVE_PROFILE_MESSAGE."
fi

echo ""
echo "Starting Spring Boot application..."
echo "Ensure MongoDB is running and accessible as per the active profile's configuration."
echo "API documentation (Swagger UI) should be available at http://localhost:8080/docs (default)."
echo "Press Ctrl+C to stop the application."
echo ""

# Run the JAR file
java -jar "$JAR_FILE" $PROFILE_ARG

EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ] && [ $EXIT_CODE -ne 130 ]; then # 130 is Ctrl+C
    echo ""
    echo "ERROR: Failed to run the Spring Boot application (Exit code: $EXIT_CODE)."
    exit 1
fi

echo ""
echo "Spring Boot application stopped."