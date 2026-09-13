# DocWallet: Secure Encrypted Document Vault

## Overview
DocWallet is a full-stack, enterprise-grade secure document management system. It allows users to register, authenticate, and manage their personal files in a highly secure environment. Every file uploaded to the platform is symmetrically encrypted at rest using AES-256 before being stored, ensuring that unauthorized parties or database administrators cannot read the raw data. 

## Technology Stack

| Layer | Technology | Key Implementations |
| :--- | :--- | :--- |
| **Frontend** | Angular (v17+), TypeScript, HTML5, CSS3 | Standalone Components, Reactive Forms, RxJS Observables, HTTP Interceptors, Route Guards |
| **Backend** | Java, Spring Boot, Spring Security | RESTful APIs, JWT Authentication, Custom Filter Chains, AES-256 Cryptography, Blob handling |
| **Database** | MySQL, Hibernate / Spring Data JPA | Relational mapping, Data isolation, Entity management |

---

## Core Features

*   **Stateless Authentication:** Implements JSON Web Tokens (JWT) for secure, stateless user sessions. Passwords are cryptographically hashed using BCrypt prior to database persistence.
*   **Cryptographic File Security:** Utilizes the Advanced Encryption Standard (AES) to encrypt file byte streams during the upload process and decrypt them on the fly during download requests.
*   **Full CRUD Operations:** Users can create (upload), read (download/view metadata), update (session state), and delete their documents.
*   **Data Isolation (IDOR Protection):** Strict backend validation ensures users can only query, download, or delete documents explicitly tied to their authenticated JWT session identity.
*   **Responsive UI:** A clean, professional user interface featuring proactive error handling, loading states, and programmatic browser-native file downloads.

---

## Security Architecture

The application implements a zero-trust model between the client and server:
1.  **CORS Configuration:** Explicitly restricts API access to the trusted Angular origin (`localhost:4200`).
2.  **HTTP Interceptors:** An Angular interceptor automatically attaches the JWT Bearer token to all outgoing authenticated HTTP requests.
3.  **Filter Chain:** Spring Security intercepts incoming requests, validates the JWT signature, and extracts the user principal before allowing access to the Document Controllers.
4.  **Binary Handling:** Files are processed as raw byte arrays and `MultipartFile` streams, preventing JSON parsing vulnerabilities with large payloads.

---

## API Endpoints Reference

### Authentication Controller (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/register` | Creates a new user account and returns a JWT | No |
| `POST` | `/login` | Authenticates existing credentials and returns a JWT | No |

### Document Controller (`/api/documents`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Retrieves a metadata list of all files owned by the user | Yes |
| `POST` | `/upload` | Accepts a `multipart/form-data` file, encrypts, and saves it | Yes |
| `GET` | `/{id}/download` | Decrypts the specified file and returns raw binary bytes | Yes |
| `DELETE`| `/{id}` | Permanently deletes a file from the server and database | Yes |

---

## Local Development Setup

### 1. Backend (Spring Boot)
1. Ensure MySQL is running on port `3306`.
2. Create a database named `docwallet`.
3. Update the `src/main/resources/application.properties` file with your database credentials and secure cryptographic keys:
```properties
spring.datasource.username=root
spring.datasource.password=your_password

# Must be a Base64-compliant string
jwt.secret=4z6B8E9F1G2H3J4K5L6M7N8P9Q0R1S2T3U4V5W6X7Y8Z9A0B1C2D3E4F5G6H7J8K

# Must be exactly 32 bytes (characters) for AES-256
encryption.secret=DocWalletAESEncryptionKey32Bytes
```
4. Run the application via your IDE or Maven wrapper: `./mvnw spring-boot:run`. The server will start on port `8080`.

### 2. Frontend (Angular)
1. Navigate to the frontend directory in your terminal.
2. Install dependencies: `npm install`
3. Start the development server: `ng serve`
4. Open a browser and navigate to `http://localhost:4200`.

---

## Future Enhancements
*   Implement refresh tokens to extend user sessions securely.
*   Migrate physical file storage from the local database/file system to an AWS S3 bucket.
*   Add file type validation to restrict uploads to specific formats (e.g., PDFs and images only).
