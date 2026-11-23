# Customer Registration and Login API Documentation

## Overview
This API provides customer registration, login, and OTP verification functionality for the Porter logistics application.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
- Customer registration, login, and OTP verification endpoints are public
- Future booking APIs will require JWT authentication
- JWT token is returned after successful OTP verification

---

## API Endpoints

### 1. Customer Registration

Registers a new customer and sends OTP to their phone number.

**Endpoint:** `POST /api/v1/customers/register`

**Request Body:**
```json
{
  "fullName": "Ahmed Ali",
  "phoneNumber": "+966501234567",
  "email": "ahmed@example.com",
  "companyName": "ABC Contracting"
}
```

**Validation Rules:**
- `fullName`: Required, string
- `phoneNumber`: Required, Saudi format (+966XXXXXXXXX)
- `email`: Optional, valid email format if provided
- `companyName`: Optional, string

**Success Response (200 OK):**
```json
{
  "message": "OTP sent successfully",
  "phoneNumber": "+966501234567",
  "otpExpiresInSeconds": 300
}
```

**Error Responses:**

**400 Bad Request - Phone number already active:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Customer Already Exists",
  "message": "Phone number already registered and active",
  "path": "/api/v1/customers/register"
}
```

**400 Bad Request - Validation error:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid request parameters",
  "validationErrors": {
    "phoneNumber": "Phone number must be in Saudi format +966XXXXXXXXX",
    "fullName": "Full name is required"
  }
}
```

---

### 2. Customer Login

Initiates login process by sending OTP to the customer's phone number.

**Endpoint:** `POST /api/v1/customers/login`

**Request Body:**
```json
{
  "phoneNumber": "+966501234567"
}
```

**Validation Rules:**
- `phoneNumber`: Required, Saudi format (+966XXXXXXXXX)

**Success Response (200 OK):**
```json
{
  "message": "OTP sent successfully",
  "phoneNumber": "+966501234567",
  "otpExpiresInSeconds": 300
}
```

**Error Responses:**

**404 Not Found - Customer not found:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 404,
  "error": "Customer Not Found",
  "message": "Customer not found. Please register first.",
  "path": "/api/v1/customers/login"
}
```

**400 Bad Request - Account blocked:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Internal Server Error",
  "message": "Your account has been blocked. Please contact support.",
  "path": "/api/v1/customers/login"
}
```

---

### 3. Verify OTP

Verifies the OTP and returns JWT token upon successful verification.

**Endpoint:** `POST /api/v1/customers/verify-otp`

**Request Body:**
```json
{
  "phoneNumber": "+966501234567",
  "otp": "123456"
}
```

**Validation Rules:**
- `phoneNumber`: Required, Saudi format (+966XXXXXXXXX)
- `otp`: Required, exactly 6 digits

**Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIrOTY2NTAxMjM0NTY3IiwicGhvbmVOdW1iZXIiOiIrOTY2NTAxMjM0NTY3IiwiY3VzdG9tZXJJZCI6MSwicm9sZSI6IkNVU1RPTUVSIiwiaWF0IjoxNzE4ODQ4MDAwLCJleHAiOjE3MTg5MzQ0MDB9.example",
  "tokenType": "Bearer",
  "customer": {
    "id": 1,
    "fullName": "Ahmed Ali",
    "phoneNumber": "+966501234567",
    "email": "ahmed@example.com",
    "companyName": "ABC Contracting",
    "status": "ACTIVE"
  }
}
```

**Error Responses:**

**400 Bad Request - Invalid OTP:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Invalid OTP",
  "message": "Invalid OTP or OTP expired",
  "path": "/api/v1/customers/verify-otp"
}
```

**400 Bad Request - OTP expired:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "OTP Expired",
  "message": "OTP has expired. Please request a new OTP.",
  "path": "/api/v1/customers/verify-otp"
}
```

**400 Bad Request - Max attempts exceeded:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Max OTP Attempts Exceeded",
  "message": "Maximum OTP attempts exceeded. Please request a new OTP.",
  "path": "/api/v1/customers/verify-otp"
}
```

**404 Not Found - Customer not found:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 404,
  "error": "Customer Not Found",
  "message": "Customer not found",
  "path": "/api/v1/customers/verify-otp"
}
```

---

### 4. Resend OTP

Resends OTP to the customer's phone number.

**Endpoint:** `POST /api/v1/customers/resend-otp`

**Request Body:**
```json
{
  "phoneNumber": "+966501234567"
}
```

**Validation Rules:**
- `phoneNumber`: Required, Saudi format (+966XXXXXXXXX)

**Success Response (200 OK):**
```json
{
  "message": "OTP resent successfully",
  "phoneNumber": "+966501234567",
  "otpExpiresInSeconds": 300
}
```

**Error Responses:**

**400 Bad Request - Resend cooldown:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Resend Cooldown",
  "message": "Please wait before resending OTP",
  "path": "/api/v1/customers/resend-otp"
}
```

**404 Not Found - Customer not found:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 404,
  "error": "Customer Not Found",
  "message": "Customer not found. Please register first.",
  "path": "/api/v1/customers/resend-otp"
}
```

---

## OTP Logic Details

### OTP Configuration
- **Length**: 6 digits
- **Expiry**: 5 minutes (300 seconds)
- **Max Attempts**: 3 verification attempts
- **Resend Cooldown**: 60 seconds

### OTP Storage
- OTP is stored as SHA-256 hash (never stored in plain text)
- OTP is logged in console for local development
- In production, integrate with SMS provider (Twilio, etc.)

### OTP Flow
1. Customer requests OTP (registration/login)
2. System generates 6-digit OTP
3. OTP is hashed and stored with expiry time
4. OTP is sent to customer's phone number
5. Customer verifies OTP within 5 minutes
6. After successful verification, JWT token is issued
7. OTP record is deleted after successful verification

---

## Customer Status Flow

### Registration Flow
```
PENDING_VERIFICATION → (OTP Verified) → ACTIVE
```

### Login Flow
```
ACTIVE → (OTP Verified) → ACTIVE (with updated last_login_at)
```

### Status Values
- **PENDING_VERIFICATION**: Customer registered but OTP not verified
- **ACTIVE**: Customer fully active and can use the system
- **BLOCKED**: Customer account blocked (manual intervention required)

---

## JWT Token Details

### Token Payload
```json
{
  "sub": "+966501234567",
  "phoneNumber": "+966501234567",
  "customerId": 1,
  "role": "CUSTOMER",
  "iat": 1718848000,
  "exp": 1718934400
}
```

### Token Configuration
- **Algorithm**: HS256
- **Expiration**: 24 hours (86400000 ms)
- **Secret**: Configured in application.properties

### Using JWT Token
Include the token in the Authorization header for protected endpoints:
```
Authorization: Bearer <access_token>
```

---

## Error Response Format

All error responses follow this standard format:

```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Error Type",
  "message": "Detailed error message",
  "path": "/api/v1/endpoint"
}
```

For validation errors, additional `validationErrors` field is included:
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid request parameters",
  "validationErrors": {
    "fieldName": "Error message"
  }
}
```

---

## Testing with cURL

### Register Customer
```bash
curl -X POST http://localhost:8080/api/v1/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Ahmed Ali",
    "phoneNumber": "+966501234567",
    "email": "ahmed@example.com",
    "companyName": "ABC Contracting"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/customers/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+966501234567"
  }'
```

### Verify OTP
```bash
curl -X POST http://localhost:8080/api/v1/customers/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+966501234567",
    "otp": "123456"
  }'
```

### Resend OTP
```bash
curl -X POST http://localhost:8080/api/v1/customers/resend-otp \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+966501234567"
  }'
```

---

## Notes

- For local development, OTP is logged in the console (check application logs)
- Phone numbers must be in Saudi format: +966 followed by 9 digits
- Email is optional but must be valid if provided
- Company name is optional
- The system uses in-memory data storage for MVP (can be replaced with database)
- JWT tokens expire after 24 hours
- OTP expires after 5 minutes
- Maximum 3 OTP verification attempts allowed
- OTP resend cooldown is 60 seconds
