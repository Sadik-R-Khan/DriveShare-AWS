# DriveShare

DriveShare is a car rental backend application built with Spring Boot. It provides REST APIs for user registration, login, renter profile management, car listing management, car search, and booking management. The project is designed as a layered backend system where controllers handle HTTP requests, services contain business logic, repositories communicate with the database, and models represent database tables.

## Project Purpose

The purpose of DriveShare is to support a car rental platform where:

- Customers can register, log in, browse available cars, search for cars, and create bookings.
- Renters can create renter profiles and list cars for rent.
- Admin users can manage wider car listing operations.
- The backend protects private actions using JWT-based authentication.
- The system prevents invalid bookings, such as bookings with wrong dates or overlapping date ranges.

## Technology Stack

DriveShare uses the following backend technologies:

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- H2 database for tests
- JWT authentication
- Lombok
- Maven

## Project Structure

The main backend source code is inside:

```text
src/main/java/com/example/demo
```

The project is organized into these packages:

```text
com.example.demo
|
|-- config
|-- controller
|-- dto
|-- model
|-- repository
|-- service
|-- DemoApplication.java
```

Each package has a specific responsibility:

- `config` contains security, JWT, CORS, request filters, and database repair logic.
- `controller` contains REST API endpoints.
- `dto` contains request and response objects used by the frontend.
- `model` contains JPA entity classes mapped to database tables.
- `repository` contains Spring Data JPA interfaces for database access.
- `service` contains business logic.

## Backend Architecture

DriveShare follows a common layered backend architecture:

```text
Frontend
   |
   | HTTP request with JSON
   v
Controller
   |
   | DTO object
   v
Service
   |
   | Entity object
   v
Repository
   |
   | JPA / Hibernate
   v
Database
```

This structure keeps the project easier to understand and maintain.

The controller layer should stay small. It receives requests and sends responses.

The service layer contains decisions and validations, such as checking whether a car is available before booking.

The repository layer handles database communication through Spring Data JPA.

The model layer defines the actual database structure.

The DTO layer defines clean request and response shapes for the frontend.

## Main Features

DriveShare currently supports:

- User registration
- User login
- JWT token generation
- Password hashing with BCrypt
- Role-based access control
- Renter profile creation
- Automatic role upgrade from `USER` to `RENTER`
- Car creation, update, deletion, listing, and search
- Public access for browsing cars
- Authenticated booking creation
- User booking history
- Renter booking view
- Booking status updates
- Booking cancellation
- Booking date validation
- Booking conflict detection
- Clean JSON error responses
- MySQL schema repair for the `cars.id` auto-increment issue
- H2-based test configuration

## User Roles

The application has three user roles:

```text
USER
RENTER
ADMIN
```

### USER

A `USER` is a normal customer. This user can:

- Register
- Log in
- View available cars
- Search cars
- Book cars
- View their own bookings
- Cancel their own bookings
- Create a renter profile if they want to become a renter

### RENTER

A `RENTER` can do everything a normal user can do, plus:

- Create a renter profile
- Add cars
- Update cars
- Delete cars
- View bookings for renter-owned cars

If a user registers directly as a renter, the backend creates a renter profile automatically.

### ADMIN

An `ADMIN` has broader access. In the current backend, admin users can access renter and car management endpoints. Admin users can also assign cars to a specific renter when creating car listings.

## Domain Models

Domain models are the main entities in the application. These are mapped to database tables using JPA annotations.

## User Entity

File:

```text
src/main/java/com/example/demo/model/User.java
```

The `User` entity stores account information.

Important fields:

- `id`
- `name`
- `email`
- `password`
- `phone`
- `role`
- `createdAt`

The email is unique, so two users cannot register with the same email address.

Passwords are not stored as plain text. During registration, the password is encoded with BCrypt before saving.

The `createdAt` field is set automatically before the user is saved.

## Renter Entity

File:

```text
src/main/java/com/example/demo/model/Renter.java
```

The `Renter` entity stores renter profile information.

Important fields:

- `id`
- `user`
- `businessName`
- `address`
- `licenseNumber`
- `createdAt`

Relationship:

```text
One User -> One Renter Profile
```

The renter table has a one-to-one relationship with the user table. Each renter profile belongs to one user, and one user can have only one renter profile.

## Car Entity

File:

```text
src/main/java/com/example/demo/model/Car.java
```

The `Car` entity stores car listing details.

Important fields:

- `id`
- `renter`
- `make`
- `model`
- `year`
- `color`
- `licensePlate`
- `pricePerDay`
- `imageData`
- `description`
- `location`
- `seatingCapacity`
- `fuelType`
- `transmission`
- `status`
- `createdAt`

Relationship:

```text
One Renter -> Many Cars
```

Each car belongs to one renter, and each renter can list many cars.

The Java field `year` is mapped to the database column `manufacture_year`:

```java
@Column(name = "manufacture_year")
private Integer year;
```

This avoids possible database issues because `year` can behave like a special word in some SQL systems.

Car enums:

```text
FuelType: PETROL, DIESEL, ELECTRIC, HYBRID
TransmissionType: MANUAL, AUTOMATIC
CarStatus: AVAILABLE, RENTED, MAINTENANCE
```

## Booking Entity

File:

```text
src/main/java/com/example/demo/model/Booking.java
```

The `Booking` entity stores rental booking information.

Important fields:

- `id`
- `user`
- `car`
- `startDate`
- `endDate`
- `totalAmount`
- `status`
- `pickupLocation`
- `createdAt`

Relationships:

```text
One User -> Many Bookings
One Car -> Many Bookings
```

Booking statuses:

```text
PENDING
CONFIRMED
CANCELLED
COMPLETED
```

The booking total is calculated automatically before saving:

```text
totalAmount = number of rental days * car price per day
```

The system uses at least one day when calculating the price.

## DTO Layer

DTO means Data Transfer Object.

DTOs are used to transfer data between the frontend and backend. They are not database tables.

DTOs are useful because they:

- Keep API request and response bodies simple.
- Avoid exposing full database entities.
- Prevent nested JSON problems.
- Allow validation rules.
- Add frontend-friendly fields such as `renterName`, `userName`, `carMake`, and `carModel`.

The DTO flow looks like this:

```text
Frontend JSON
   |
   v
Controller receives DTO
   |
   v
Service converts DTO to Entity
   |
   v
Repository saves Entity
   |
   v
Service converts Entity back to DTO
   |
   v
Controller returns DTO as JSON
```

Main DTO files:

- `AuthRequest.java`
- `AuthResponse.java`
- `RegisterRequest.java`
- `CarDto.java`
- `RenterDto.java`
- `BookingDto.java`

## Authentication DTOs

`AuthRequest` is used for login.

Fields:

- `email`
- `password`

Validation:

- Email must be present and valid.
- Password must be present and at least 6 characters.

`RegisterRequest` is used for registration.

Fields:

- `name`
- `email`
- `password`
- `phone`
- `role`

`AuthResponse` is returned after successful login or registration.

Fields:

- `token`
- `role`
- `userId`
- `name`
- `email`

## Car DTO

`CarDto` is used for car creation, update, and response data.

Important fields:

- `id`
- `make`
- `model`
- `year`
- `color`
- `licensePlate`
- `pricePerDay`
- `imageData`
- `description`
- `location`
- `seatingCapacity`
- `fuelType`
- `transmission`
- `status`
- `renterId`
- `renterName`

Validation:

- `make` is required.
- `model` is required.
- `pricePerDay` is required and must be positive.

## Booking DTO

`BookingDto` is used for booking requests and responses.

Important fields:

- `id`
- `carId`
- `startDate`
- `endDate`
- `totalAmount`
- `status`
- `pickupLocation`
- `userId`
- `userName`
- `carMake`
- `carModel`
- `createdAt`

Validation:

- `carId` is required.
- `startDate` is required.
- `endDate` is required.

The backend gets the real logged-in user ID from the JWT token, not from the frontend request body.

## Renter DTO

`RenterDto` is used for renter profile requests and responses.

Important fields:

- `id`
- `userId`
- `userName`
- `userEmail`
- `businessName`
- `address`
- `licenseNumber`

## Controller Layer

Controllers define the API endpoints. They receive HTTP requests and return HTTP responses.

## Auth Controller

Base path:

```text
/api/auth
```

Endpoints:

```text
POST /api/auth/register
POST /api/auth/login
```

Register flow:

```text
Frontend sends registration details
AuthController receives RegisterRequest
AuthService checks duplicate email
Password is hashed
User is saved
Renter profile is created if role is RENTER
JWT token is generated
AuthResponse is returned
```

Login flow:

```text
Frontend sends email and password
AuthController receives AuthRequest
AuthenticationManager validates credentials
User is loaded from database
JWT token is generated
AuthResponse is returned
```

## Car Controller

Base path:

```text
/api/cars
```

Endpoints:

```text
GET    /api/cars
GET    /api/cars/all
GET    /api/cars/{id}
GET    /api/cars/search?query=value
GET    /api/cars/renter/{renterId}
POST   /api/cars
PUT    /api/cars/{id}
DELETE /api/cars/{id}
```

Purpose:

- Get available cars.
- Get all cars.
- Get one car by ID.
- Search cars by make or model.
- Get cars by renter.
- Create a car.
- Update a car.
- Delete a car.

Public endpoints:

- `GET /api/cars`
- `GET /api/cars/**`

Protected car management endpoints require `RENTER` or `ADMIN` role.

When creating a car, the controller uses:

```java
@RequestAttribute("userId") Long userId
```

That user ID is inserted by the backend after reading the JWT token. This is safer than trusting a user ID sent from the frontend.

## Renter Controller

Base path:

```text
/api/renters
```

Endpoints:

```text
POST /api/renters
GET  /api/renters/me
PUT  /api/renters/{id}
```

Purpose:

- Create a renter profile.
- Get the logged-in renter profile.
- Update renter profile details.

Important behavior:

If a normal `USER` creates a renter profile, the backend upgrades their role:

```text
USER -> RENTER
```

## Booking Controller

Base path:

```text
/api/bookings
```

Endpoints:

```text
POST /api/bookings
GET  /api/bookings/my
GET  /api/bookings/renter/{renterId}
PUT  /api/bookings/{id}/status
PUT  /api/bookings/{id}/cancel
```

Purpose:

- Create a booking.
- View the logged-in user's bookings.
- View bookings for cars owned by a renter.
- Update booking status.
- Cancel a booking.

Booking endpoints require authentication.

## Service Layer

Services contain business logic. This is where most decisions happen.

## Auth Service

File:

```text
src/main/java/com/example/demo/service/AuthService.java
```

Responsibilities:

- Register users.
- Check duplicate email addresses.
- Hash passwords.
- Save new users.
- Create renter profiles for renter registrations.
- Authenticate login requests.
- Generate JWT tokens.

Registration behavior:

```text
If role is missing, default to USER.
If role is RENTER, create a renter profile automatically.
```

This helps avoid a common problem where a user has the `RENTER` role but no renter profile exists.

## Car Service

File:

```text
src/main/java/com/example/demo/service/CarService.java
```

Responsibilities:

- Get available cars.
- Get all cars.
- Get car by ID.
- Get cars by renter.
- Search cars.
- Create cars.
- Update cars.
- Delete cars.
- Convert `Car` entities to `CarDto` objects.

Create car behavior:

```text
Find logged-in user by userId.
Resolve the renter profile.
If admin provides renterId, use that renter.
If renter profile is missing for a RENTER or ADMIN, create a default profile.
Create the car with status AVAILABLE.
Save and return CarDto.
```

Only renters and admins should create cars.

## Renter Service

File:

```text
src/main/java/com/example/demo/service/RenterService.java
```

Responsibilities:

- Create renter profiles.
- Get renter profile by logged-in user ID.
- Update renter profile.
- Convert `Renter` entities to `RenterDto` objects.

Important behavior:

```text
If a USER creates a renter profile, their role changes to RENTER.
```

The service also prevents duplicate renter profiles for the same user.

## Booking Service

File:

```text
src/main/java/com/example/demo/service/BookingService.java
```

Responsibilities:

- Create bookings.
- Validate booking dates.
- Check car availability.
- Detect conflicting bookings.
- Get bookings by user.
- Get bookings by renter.
- Update booking status.
- Cancel bookings.
- Convert `Booking` entities to `BookingDto` objects.

Booking creation checks:

```text
startDate must be before endDate.
Car must exist.
Car status must be AVAILABLE.
Requested dates must not overlap with existing PENDING or CONFIRMED bookings.
```

If all checks pass, the booking is created with:

```text
status = PENDING
```

## Repository Layer

Repositories communicate with the database.

All main repositories extend:

```java
JpaRepository<Entity, Long>
```

This gives built-in database methods such as:

```text
findAll()
findById()
save()
deleteById()
```

## User Repository

File:

```text
src/main/java/com/example/demo/repository/UserRepository.java
```

Methods:

```java
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
```

Used for:

- Registration duplicate checks
- Login
- JWT authentication
- User lookup

## Renter Repository

File:

```text
src/main/java/com/example/demo/repository/RenterRepository.java
```

Method:

```java
Optional<Renter> findByUserId(Long userId);
```

Used to find the renter profile linked to a logged-in user.

## Car Repository

File:

```text
src/main/java/com/example/demo/repository/CarRepository.java
```

Methods:

```java
List<Car> findByStatus(Car.CarStatus status);
List<Car> findByRenterId(Long renterId);
List<Car> findByMakeContainingIgnoreCaseOrModelContainingIgnoreCase(String make, String model);
```

Used for:

- Showing available cars
- Showing renter cars
- Searching by make or model

## Booking Repository

File:

```text
src/main/java/com/example/demo/repository/BookingRepository.java
```

Methods:

```java
List<Booking> findByUserId(Long userId);
List<Booking> findByRenterId(Long renterId);
List<Booking> findConflictingBookings(Long carId, LocalDate startDate, LocalDate endDate);
```

The custom conflict query checks whether a car is already booked in an overlapping date range.

## Search Logic

Car search is handled by:

```text
GET /api/cars/search?query=value
```

The backend searches:

- Car make
- Car model

Examples:

```text
query = swift
matches model = Swift

query = honda
matches make = Honda

query = creta
matches model = Creta
```

The search is case-insensitive.

Current search does not include:

- Location
- Fuel type
- Transmission type
- Price range
- Seating capacity
- Year

Those can be added later with custom repository queries or filtering logic.

## Booking Conflict Logic

DriveShare prevents double booking by checking existing bookings before creating a new one.

The repository checks bookings with status:

```text
PENDING
CONFIRMED
```

The overlap condition is:

```text
existing.startDate < requested.endDate
AND
existing.endDate > requested.startDate
```

Example conflict:

```text
Existing booking: 10 Aug to 15 Aug
New booking:      12 Aug to 18 Aug
Result: conflict
```

Example non-conflict:

```text
Existing booking: 10 Aug to 15 Aug
New booking:      15 Aug to 20 Aug
Result: allowed
```

This means the end date behaves like a checkout/return date.

## JWT Authentication

DriveShare uses JWT tokens to authenticate protected requests.

JWT means JSON Web Token.

After successful registration or login, the backend returns a token:

```json
{
  "token": "jwt-token-value",
  "role": "RENTER",
  "userId": 1,
  "name": "Example User",
  "email": "user@example.com"
}
```

The frontend should send the token in future protected requests:

```text
Authorization: Bearer jwt-token-value
```

## JWT Utility

File:

```text
src/main/java/com/example/demo/config/JwtUtil.java
```

Responsibilities:

- Generate JWT tokens.
- Store email as the token subject.
- Store role as a token claim.
- Extract email from token.
- Validate token signature and expiration.

Important token data:

- Subject: user email
- Claim: user role
- Issued date
- Expiration date

## JWT Filter

File:

```text
src/main/java/com/example/demo/config/JwtFilter.java
```

The JWT filter runs before controller methods.

Flow:

```text
Read Authorization header.
Check if it starts with Bearer.
Extract token.
Validate token.
Extract email.
Load user details.
Create Spring Security authentication.
Store authentication in SecurityContext.
Continue request.
```

If the token belongs to a deleted or missing user, the backend returns:

```json
{
  "message": "Your session is no longer valid. Please log in again."
}
```

Status:

```text
401 Unauthorized
```

## User ID Injector

File:

```text
src/main/java/com/example/demo/config/UserIdInjector.java
```

This filter runs after JWT authentication.

Purpose:

```text
Read authenticated email.
Find matching user in database.
Attach user ID to the request.
```

Controllers can then use:

```java
@RequestAttribute("userId") Long userId
```

This is important because the backend should trust the token, not a user ID sent by the frontend.

## Security Rules

File:

```text
src/main/java/com/example/demo/config/SecurityConfig.java
```

Access rules:

```text
/api/auth/**              public
GET /api/cars/**          public
POST /api/renters         authenticated
/api/cars/**              RENTER or ADMIN
/api/renters/**           RENTER or ADMIN
/api/bookings/**          authenticated
anything else             authenticated
```

CORS allows the Angular development server:

```text
http://localhost:4200
```

Sessions are stateless:

```text
SessionCreationPolicy.STATELESS
```

That means the backend does not store login sessions. JWT tokens handle authentication state.

## Error Handling

File:

```text
src/main/java/com/example/demo/controller/ApiExceptionHandler.java
```

This class catches common exceptions and returns simple JSON responses.

Example runtime error:

```json
{
  "message": "Car not found"
}
```

Example validation error:

```json
{
  "message": "email must be a well-formed email address"
}
```

Example database error:

```json
{
  "message": "The database rejected the request: details here"
}
```

There is also a special message for the MySQL auto-increment problem:

```json
{
  "message": "The cars.id database column is missing AUTO_INCREMENT. Restart the backend so the schema repair can run."
}
```

## Database Configuration

Main configuration file:

```text
src/main/resources/application.properties
```

Important settings:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/carrental_db
spring.datasource.username=root
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
jwt.expiration=86400000
```

The backend runs on:

```text
http://localhost:8080
```

The database is:

```text
carrental_db
```

Hibernate is set to:

```text
ddl-auto=update
```

This lets Hibernate update the database schema based on entity changes.

## Test Configuration

Test configuration file:

```text
src/test/resources/application.properties
```

Tests use H2:

```properties
spring.datasource.url=jdbc:h2:mem:carrental_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=create-drop
```

This allows tests to run without connecting to the local MySQL database.

## Database Schema Repair

File:

```text
src/main/java/com/example/demo/config/DatabaseSchemaRepair.java
```

This class fixes an existing MySQL schema issue where the `cars.id` column may not have `AUTO_INCREMENT`.

Why this matters:

```text
JPA expects car IDs to be generated automatically.
If MySQL does not auto-generate the ID, creating a car fails.
```

The repair runs when the backend starts.

It checks:

```text
Is the database MySQL?
Does cars.id miss auto_increment?
```

If needed, it runs:

```sql
ALTER TABLE cars MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
```

## Important API Flows

## Register Flow

Endpoint:

```text
POST /api/auth/register
```

Steps:

```text
Frontend sends name, email, password, phone, and optional role.
Backend checks whether email already exists.
Backend hashes password.
Backend saves user.
If role is RENTER, backend creates renter profile.
Backend generates JWT token.
Backend returns AuthResponse.
```

## Login Flow

Endpoint:

```text
POST /api/auth/login
```

Steps:

```text
Frontend sends email and password.
Backend validates credentials.
Backend loads user details.
Backend generates JWT token.
Backend returns AuthResponse.
```

## Create Renter Profile Flow

Endpoint:

```text
POST /api/renters
```

Steps:

```text
Frontend sends renter profile details with JWT token.
JWT filter authenticates user.
UserIdInjector attaches userId to request.
RenterService checks duplicate profile.
If user role is USER, role changes to RENTER.
Renter profile is saved.
Backend returns RenterDto.
```

## Create Car Flow

Endpoint:

```text
POST /api/cars
```

Required:

```text
Authorization: Bearer token
Role: RENTER or ADMIN
```

Steps:

```text
Frontend sends car details.
JWT filter authenticates user.
UserIdInjector attaches userId to request.
CarService finds logged-in user.
CarService resolves renter profile.
Car is created with status AVAILABLE.
Car is saved.
Backend returns CarDto.
```

## Search Car Flow

Endpoint:

```text
GET /api/cars/search?query=value
```

Steps:

```text
Frontend sends search query.
CarController receives query.
CarService passes query to repository.
Repository searches make and model.
Backend returns matching CarDto list.
```

## Create Booking Flow

Endpoint:

```text
POST /api/bookings
```

Required:

```text
Authorization: Bearer token
```

Steps:

```text
Frontend sends carId, startDate, endDate, and pickupLocation.
JWT filter authenticates user.
UserIdInjector attaches userId to request.
BookingService validates dates.
BookingService checks car exists.
BookingService checks car status is AVAILABLE.
BookingService checks overlapping bookings.
Booking is saved as PENDING.
Backend returns BookingDto.
```

## Cancel Booking Flow

Endpoint:

```text
PUT /api/bookings/{id}/cancel
```

Steps:

```text
Backend finds booking.
Backend checks that the logged-in user owns the booking.
Booking status changes to CANCELLED.
Booking is saved.
Backend returns 204 No Content.
```

## Example API Requests

## Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Aarav Sharma",
  "email": "aarav@example.com",
  "password": "password123",
  "phone": "9876543210",
  "role": "USER"
}
```

## Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "aarav@example.com",
  "password": "password123"
}
```

## Create Car

```http
POST /api/cars
Authorization: Bearer jwt-token-value
Content-Type: application/json

{
  "make": "Hyundai",
  "model": "Creta",
  "year": 2023,
  "color": "White",
  "licensePlate": "MH12AB1234",
  "pricePerDay": 2500,
  "description": "Comfortable SUV for city and highway trips",
  "location": "Pune",
  "seatingCapacity": 5,
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC"
}
```

## Create Booking

```http
POST /api/bookings
Authorization: Bearer jwt-token-value
Content-Type: application/json

{
  "carId": 1,
  "startDate": "2026-08-22",
  "endDate": "2026-08-25",
  "pickupLocation": "Pune Airport"
}
```

## How to Run the Project

Before running the project, make sure:

- Java 17 is installed.
- MySQL is running.
- Database `carrental_db` exists.
- Database username and password match `application.properties`.

Create the database if needed:

```sql
CREATE DATABASE carrental_db;
```

Run the application with Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend starts at:

```text
http://localhost:8080
```

## How to Run Tests

Run tests with:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Tests use the H2 in-memory database, so they do not require the local MySQL database.

## Current Limitations

The current backend is functional, but these areas can be improved:

- Search only checks make and model.
- Booking status changes do not automatically update car status.
- Most errors use `RuntimeException`; custom exception classes would be cleaner.
- There is only a basic context loading test.
- Renter update endpoints do not currently verify ownership.
- Booking status update endpoint should be restricted more carefully for renter or admin use.
- Image data is stored as text; a production system may prefer cloud/object storage.
- Payment handling is not included.
- Admin-specific endpoints are limited.

## Suggested Future Improvements

Useful future additions:

- Add price, location, fuel type, transmission, seating, and date filters to car search.
- Add pagination for car lists and booking lists.
- Add ownership checks for renter profile and car updates.
- Add admin dashboard APIs.
- Add payment integration.
- Add email notifications for booking confirmation and cancellation.
- Add review and rating system.
- Add frontend image upload support.
- Store images in cloud storage instead of database text.
- Add tests for authentication, car creation, search, bookings, and conflict detection.
- Add refresh tokens for better login session management.

## Summary

DriveShare is a Spring Boot car rental backend with JWT security, role-based access, renter profiles, car listings, search, and booking management. The code is organized into controllers, services, repositories, DTOs, models, and configuration classes. The backend protects private actions with JWT tokens, keeps request data clean with DTOs, uses JPA for database work, and validates important booking rules before saving rental requests.

In simple terms:

```text
Users register and log in.
Renters list cars.
Customers browse and book cars.
JWT protects private actions.
Services validate business rules.
Repositories save and read data.
MySQL stores the application data.
```

