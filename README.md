# TeamFlow – Project Management Backend

TeamFlow is a project management SaaS backend built using Java and Spring Boot. It provides secure workspace and project management, task assignment, collaboration, and notification features.

## Tech Stack

- **Language:** Java
- **Backend:** Spring Boot
- **Security:** Spring Security, JWT Authentication
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA, Hibernate
- **Build Tool:** Maven
- **API Documentation:** Springdoc OpenAPI / Swagger
- **Version Control:** Git and GitHub

## Features

### Authentication and Security
- User registration and login
- JWT-based authentication
- Password encryption using BCrypt
- Protected REST APIs

### Workspace Management
- Create and view workspaces
- Workspace membership and roles
- Role-based access control

### Project Management
- Create and manage projects
- Add and remove project members
- Project-level authorization
- Workspace and project ownership rules

### Task Management
- Create, update, and delete tasks
- Assign tasks to project members
- Task priorities and statuses
- Due dates
- Pagination and sorting

### Task Collaboration
- Add, view, update, and delete task comments
- Restrict comment modification to the comment author

### Notifications
- Notifications when tasks are assigned
- Notifications when comments are added
- Notifications when task statuses change
- View personal notifications
- Mark notifications as read

## Architecture

The application follows a layered architecture:

- **Controller Layer:** Handles HTTP requests and responses.
- **Service Layer:** Contains business logic and authorization checks.
- **Repository Layer:** Handles database operations using Spring Data JPA.
- **Entity Layer:** Defines database entities and relationships.
- **DTO Layer:** Handles request and response data.

## Security and Authorization

- JWT authentication for protected endpoints.
- BCrypt password hashing.
- Workspace and project membership validation.
- Role-based permissions for workspace and project operations.
- Task operations restricted according to project membership and ownership.

## Running the Project Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/harishbabur-1/teamflow.git
   ```

2. Open the project in IntelliJ IDEA.

3. Configure your PostgreSQL database and update `application.properties`.

4. Configure your JWT secret and other required environment variables.

5. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

6. Access the API documentation through the Swagger UI endpoint if enabled:

   ```text
   http://localhost:8080/swagger-ui/index.html
   ```

## Future Improvements

- Dashboard and project analytics
- Automated integration testing
- Cloud deployment and CI/CD

## Author

Harish Babu R

GitHub: https://github.com/harishbabur-1