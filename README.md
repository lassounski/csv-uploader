# CSV Uploader Application

This Spring Boot application provides REST endpoints to upload, download, and manage CSV data in an H2 database.

## Prerequisites

- Java 17
- Maven

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

- POST `/api/csv/upload` - Upload a CSV file
- GET `/api/csv/code/{code}` - Get a record by code
- GET `/api/csv/download` - Download all data as CSV
- DELETE `/api/csv` - Delete all data

You can use Postman to test those endpoints.

## H2 Console

The H2 console is available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/csvdb`
- Username: sa
- Password: password

## Running Tests

To run the tests:
```bash
mvn test
```

This implementation provides:
1. A complete Spring Boot application with all requested functionality
2. Entity class with Lombok annotations
3. Repository interface extending JpaRepository
4. Service layer with CSV processing logic
5. REST controller with all required endpoints
6. Unit tests for the service layer
7. Integration tests using RestAssured
8. H2 database configuration

The application allows you to:
- Upload CSV files
- Retrieve records by code
- Download all data as CSV
- Delete all records
- Persist data in H2 database

The code includes proper error handling, follows Spring Boot best practice

Further steps:
- Add proper loggin with Logback
- Have a Global Exception handler to handle various exceptional scenarios
- Add a Validator for the incoming CSV file
- Use the target database running in TestContainers for integration testing