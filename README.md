# JobTrackr - Job Application Tracking System (Java Servlet)

## Overview
JobTrackr is a RESTful job application tracking service built with Java Servlets, designed to help users organize and manage their job search process. Deployed locally using Apache Tomcat.

## Features
- **Job Application Tracking**: CRUD operations for job applications
- **Task Management**: Create and manage follow-up tasks
- **Document Storage**: Upload and manage application documents
- **User Authentication**: Secure user accounts and sessions
- **RESTful API**: Standard HTTP endpoints for all operations

## Technology Stack
- **Backend**: Java Servlets
- **Database**: MySQL (or your chosen database)
- **Server**: Apache Tomcat
- **Authentication**: Session-based
- **Document Storage**: Local file system or cloud storage integration

## Database Schema

### Tables Structure
```sql
-- User Table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    picture VARCHAR(255),
    job_title_target VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password VARCHAR(255) NOT NULL,
    isActivated BOOLEAN DEFAULT FALSE
);

-- Job Table
CREATE TABLE jobs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title VARCHAR(100) NOT NULL,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    status ENUM('Applied', 'Interview', 'Offer', 'Rejected', 'Accepted') DEFAULT 'Applied',
    applied_at DATE,
    job_url VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Document Table
CREATE TABLE documents (
    id VARCHAR(36) PRIMARY KEY,
    job_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type ENUM('PDF', 'DOCX', 'MP4', 'JPG', 'PNG') NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Task Table
CREATE TABLE tasks (
    id VARCHAR(36) PRIMARY KEY,
    job_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    description TEXT NOT NULL,
    due_date DATETIME NOT NULL,
    priority ENUM('Low', 'Medium', 'High') DEFAULT 'Medium',
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
# API Endpoints

## Authentication

### POST /api/auth/register
- **Description**: Registers a new user.
- **Request**: Form parameters (`name`, `email`, `password`, `job_title_target`)
- **Response**: JSON with success message and `userId`
- **Status Codes**:
  - `200 (OK)`: User registered successfully
  - `500 (Internal Server Error)`: Registration failure

### POST /api/auth/login
- **Description**: Logs in a user.
- **Request**: Form parameters (`email`, `password`)
- **Response**: JSON with success message and `jwt` 
- **Status Codes**:
  - `200 (OK)`: Login successful
  - `401 (Unauthorized)`: Invalid credentials or account not activated
  - `500 (Internal Server Error)`: Login failure

### POST /api/auth/verify
- **Description**: Verifies a token (e.g., for email verification).
- **Request**: Query parameter (`token`)
- **Response**: JSON with success message
- **Status Codes**:
  - `200 (OK)`: Account verified successfully
  - `400 (Bad Request)`: Invalid token
  - `500 (Internal Server Error)`: Verification failure

### POST /api/auth/resend-token
- **Description**: Resends a verification email.
- **Response**: JSON with success message
- **Status Codes**:
  - `200 (OK)`: Verification email resent
  - `500 (Internal Server Error)`: Email sending failure

### GET /api/auth/me
- **Description**: Retrieves the current user's profile.
- **Response**: JSON with user details (`name`, `email`, `job_title_target`)
- **Status Codes**:
  - `200 (OK)`: Profile retrieved successfully
  - `401 (Unauthorized)`: Not authenticated
  - `500 (Internal Server Error)`: Retrieval failure

## Jobs

### POST /api/jobs/create-job
- **Description**: Creates a new job application.
- **Request**: Form parameters (`title`, `company`, `location`, `status`, `applied_at`, `job_url`, `notes`)
- **Response**: JSON with success message and `jobId`
- **Status Codes**:
  - `200 (OK)`: Job created successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `500 (Internal Server Error)`: Creation failure

### GET /api/jobs/
- **Description**: Retrieves all job applications for the authenticated user.
- **Response**: JSON array of job objects (`id`, `title`, `company`, `location`, `status`, `applied_at`, `job_url`, `notes`, `created_at`)
- **Status Codes**:
  - `200 (OK)`: Jobs retrieved successfully
  - `401 (Unauthorized)`: User not authenticated
  - `500 (Internal Server Error)`: Retrieval failure

### GET /api/jobs/{id}
- **Description**: Retrieves details of a specific job application.
- **Response**: JSON object with job details
- **Status Codes**:
  - `200 (OK)`: Job retrieved successfully
  - `401 (Unauthorized)`: User not authenticated
  - `404 (Not Found)`: Job not found or access denied
  - `500 (Internal Server Error)`: Retrieval failure

### PUT /api/jobs/update-job/{id}
- **Description**: Updates a specific job application.
- **Request**: Form parameters (optional: `title`, `company`, `location`, `status`, `applied_at`, `job_url`, `notes`)
- **Response**: JSON with success message
- **Status Codes**:
  - `200 (OK)`: Job updated successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `404 (Not Found)`: Job not found or access denied
  - `500 (Internal Server Error)`: Update failure

### DELETE /api/jobs/delete-job/{id}
- **Description**: Deletes a specific job application.
- **Response**: JSON with success message
- **Status Codes**:
  - `200 (OK)`: Job deleted successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `404 (Not Found)`: Job not found or access denied
  - `500 (Internal Server Error)`: Deletion failure

## Documents

### POST /api/documents/upload/{jobId}
- **Description**: Uploads a document (e.g., resume, cover letter) for a specific job.
- **Request**: Multipart form-data with a `file` part
- **Response**: JSON with success message and `documentId`
- **Status Codes**:
  - `200 (OK)`: Upload successful
  - `400 (Bad Request)`: Invalid URL or unsupported file type
  - `401 (Unauthorized)`: User not authenticated
  - `403 (Forbidden)`: Job not found or access denied
  - `500 (Internal Server Error)`: Upload failure

### GET /api/documents/download/{documentId}
- **Description**: Downloads a specific document by its ID.
- **Response**: File stream with `Content-Disposition: attachment`
- **Status Codes**:
  - `200 (OK)`: File downloaded successfully
  - `404 (Not Found)`: Document or file not found
  - `500 (Internal Server Error)`: Download failure

### GET /api/documents/{jobId}
- **Description**: Retrieves metadata for all documents associated with a specific job.
- **Response**: JSON array of document objects (`id`, `file_name`, `file_type`, `file_url`, `created_at`)
- **Status Codes**:
  - `200 (OK)`: Documents retrieved successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `403 (Forbidden)`: Job not found or access denied
  - `500 (Internal Server Error)`: Retrieval failure

## Tasks

### POST /api/tasks/create-task/{jobId}
- **Description**: Creates a new task for a specific job.
- **Request**: Form parameters (`description`, `due_date`, `priority`)
- **Response**: JSON with success message and `taskId`
- **Status Codes**:
  - `200 (OK)`: Task created successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `403 (Forbidden)`: Job not found or access denied
  - `500 (Internal Server Error)`: Creation failure

### POST /api/tasks/{taskId}
- **Description**: Marks a specific task as completed.
- **Response**: JSON with success message
- **Status Codes**:
  - `200 (OK)`: Task updated successfully
  - `400 (Bad Request)`: Invalid URL
  - `500 (Internal Server Error)`: Update failure

### GET /api/tasks/job/{jobId}
- **Description**: Retrieves all tasks for a specific job.
- **Response**: JSON array of task objects (`id`, `description`, `due_date`, `priority`, `completed`, `created_at`)
- **Status Codes**:
  - `200 (OK)`: Tasks retrieved successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `403 (Forbidden)`: Job not found or access denied
  - `500 (Internal Server Error)`: Retrieval failure

### GET /api/tasks/{taskId}
- **Description**: Retrieves details of a specific task.
- **Response**: JSON object with task details (`id`, `job_id`, `description`, `due_date`, `priority`, `completed`, `created_at`)
- **Status Codes**:
  - `200 (OK)`: Task retrieved successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `404 (Not Found)`: Task not found or access denied
  - `500 (Internal Server Error)`: Retrieval failure

### PUT /api/tasks/update-task/{taskId}
- **Description**: Updates a specific task.
- **Request**: Form parameters (optional: `description`, `due_date`, `priority`, `completed`)
- **Response**: JSON with success message (assumed from servlet pattern)
- **Status Codes**:
  - `200 (OK)`: Task updated successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated
  - `404 (Not Found)`: Task not found or access denied (assumed)
  - `500 (Internal Server Error)`: Update failure

### DELETE /api/tasks/delete-task/{taskId}
- **Description**: Deletes a specific task.
- **Response**: JSON with success message (assumed from servlet pattern)
- **Status Codes**:
  - `200 (OK)`: Task deleted successfully
  - `400 (Bad Request)`: Invalid URL
  - `401 (Unauthorized)`: User not authenticated (assumed)
  - `404 (Not Found)`: Task not found or access denied (assumed)
  - `500 (Internal Server Error)`: Deletion failure
## An overhead is that i failed to conceal environmental variables. Adjust variables like mail password, user name and so to match your credentials
## This is a work in progress. More features will be added.
