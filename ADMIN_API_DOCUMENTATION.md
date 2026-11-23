# Admin API Documentation

## Overview
This API provides admin functionality for managing vehicles, drivers, and booking assignments in the CargoGo logistics application.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
All admin endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <access_token>
```

The admin ID is extracted from the JWT token and used for tracking assignment actions.

---

## API Endpoints

### 1. Create Vehicle

Creates a new vehicle in the system.

**Endpoint:** `POST /api/v1/admin/vehicles`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "vehicleNumber": "KSA1234",
  "vehicleType": "PICKUP_TRUCK",
  "capacityKg": 1000,
  "wialonUnitId": "unit_12345"
}
```

**Validation Rules:**
- `vehicleNumber`: Required, string, unique
- `vehicleType`: Required, enum (MINI_TRUCK, PICKUP_TRUCK, FLATBED_TRUCK, HEAVY_TRUCK)
- `capacityKg`: Required, positive decimal
- `wialonUnitId`: Optional, string

**Success Response (200 OK):**
```json
{
  "id": 1,
  "vehicleNumber": "KSA1234",
  "vehicleType": "PICKUP_TRUCK",
  "capacityKg": 1000,
  "status": "AVAILABLE",
  "active": true,
  "wialonUnitId": "unit_12345",
  "currentDriverId": null,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T10:00:00"
}
```

**Error Responses:**

**401 Unauthorized:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

**400 Bad Request (Validation Error):**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid request parameters",
  "validationErrors": {
    "vehicleNumber": "Vehicle number is required"
  }
}
```

---

### 2. Get All Vehicles

Retrieves all vehicles in the system.

**Endpoint:** `GET /api/v1/admin/vehicles`

**Authentication:** Required (Admin)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "vehicleNumber": "KSA1234",
    "vehicleType": "PICKUP_TRUCK",
    "capacityKg": 1000,
    "status": "AVAILABLE",
    "active": true,
    "wialonUnitId": "unit_12345",
    "currentDriverId": null,
    "createdAt": "2024-06-20T10:00:00",
    "updatedAt": "2024-06-20T10:00:00"
  },
  {
    "id": 2,
    "vehicleNumber": "KSA5678",
    "vehicleType": "MINI_TRUCK",
    "capacityKg": 500,
    "status": "ASSIGNED",
    "active": true,
    "wialonUnitId": "unit_67890",
    "currentDriverId": 5,
    "createdAt": "2024-06-20T11:00:00",
    "updatedAt": "2024-06-20T12:00:00"
  }
]
```

---

### 3. Get Vehicle by ID

Retrieves details of a specific vehicle by ID.

**Endpoint:** `GET /api/v1/admin/vehicles/{id}`

**Authentication:** Required (Admin)

**Success Response (200 OK):**
```json
{
  "id": 1,
  "vehicleNumber": "KSA1234",
  "vehicleType": "PICKUP_TRUCK",
  "capacityKg": 1000,
  "status": "AVAILABLE",
  "active": true,
  "wialonUnitId": "unit_12345",
  "currentDriverId": null,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T10:00:00"
}
```

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Vehicle Not Found",
  "message": "Vehicle not found"
}
```

---

### 4. Update Vehicle

Updates an existing vehicle's information.

**Endpoint:** `PUT /api/v1/admin/vehicles/{id}`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "vehicleNumber": "KSA1234",
  "vehicleType": "PICKUP_TRUCK",
  "capacityKg": 1200,
  "wialonUnitId": "unit_12345"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "vehicleNumber": "KSA1234",
  "vehicleType": "PICKUP_TRUCK",
  "capacityKg": 1200,
  "status": "AVAILABLE",
  "active": true,
  "wialonUnitId": "unit_12345",
  "currentDriverId": null,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T13:00:00"
}
```

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Vehicle Not Found",
  "message": "Vehicle not found"
}
```

---

### 5. Update Vehicle Status

Updates the status of a vehicle.

**Endpoint:** `PATCH /api/v1/admin/vehicles/{id}/status`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "status": "MAINTENANCE"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "vehicleNumber": "KSA1234",
  "vehicleType": "PICKUP_TRUCK",
  "capacityKg": 1000,
  "status": "MAINTENANCE",
  "active": true,
  "wialonUnitId": "unit_12345",
  "currentDriverId": null,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T14:00:00"
}
```

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Vehicle Not Found",
  "message": "Vehicle not found"
}
```

---

### 6. Delete Vehicle

Deletes a vehicle from the system.

**Endpoint:** `DELETE /api/v1/admin/vehicles/{id}`

**Authentication:** Required (Admin)

**Success Response (204 No Content):**

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Vehicle Not Found",
  "message": "Vehicle not found"
}
```

---

### 7. Create Driver

Creates a new driver in the system.

**Endpoint:** `POST /api/v1/admin/drivers`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "fullName": "Ahmed Ali",
  "phoneNumber": "+966501234567",
  "iqamaNumber": "1234567890",
  "drivingLicenseNumber": "DL123456"
}
```

**Validation Rules:**
- `fullName`: Required, string
- `phoneNumber`: Required, Saudi format (+966XXXXXXXXX)
- `iqamaNumber`: Required, string
- `drivingLicenseNumber`: Required, string

**Success Response (200 OK):**
```json
{
  "id": 1,
  "fullName": "Ahmed Ali",
  "phoneNumber": "+966501234567",
  "iqamaNumber": "1234567890",
  "drivingLicenseNumber": "DL123456",
  "status": "AVAILABLE",
  "active": true,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T10:00:00"
}
```

**Error Responses:**

**401 Unauthorized:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

**400 Bad Request (Validation Error):**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid request parameters",
  "validationErrors": {
    "phoneNumber": "Phone number must be in Saudi format (+966XXXXXXXXX)"
  }
}
```

---

### 8. Get All Drivers

Retrieves all drivers in the system.

**Endpoint:** `GET /api/v1/admin/drivers`

**Authentication:** Required (Admin)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "fullName": "Ahmed Ali",
    "phoneNumber": "+966501234567",
    "iqamaNumber": "1234567890",
    "drivingLicenseNumber": "DL123456",
    "status": "AVAILABLE",
    "active": true,
    "createdAt": "2024-06-20T10:00:00",
    "updatedAt": "2024-06-20T10:00:00"
  },
  {
    "id": 2,
    "fullName": "Mohammed Hassan",
    "phoneNumber": "+966509999999",
    "iqamaNumber": "0987654321",
    "drivingLicenseNumber": "DL789012",
    "status": "ASSIGNED",
    "active": true,
    "createdAt": "2024-06-20T11:00:00",
    "updatedAt": "2024-06-20T12:00:00"
  }
]
```

---

### 9. Get Driver by ID

Retrieves details of a specific driver by ID.

**Endpoint:** `GET /api/v1/admin/drivers/{id}`

**Authentication:** Required (Admin)

**Success Response (200 OK):**
```json
{
  "id": 1,
  "fullName": "Ahmed Ali",
  "phoneNumber": "+966501234567",
  "iqamaNumber": "1234567890",
  "drivingLicenseNumber": "DL123456",
  "status": "AVAILABLE",
  "active": true,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T10:00:00"
}
```

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Driver Not Found",
  "message": "Driver not found"
}
```

---

### 10. Update Driver

Updates an existing driver's information.

**Endpoint:** `PUT /api/v1/admin/drivers/{id}`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "fullName": "Ahmed Ali Mohammed",
  "phoneNumber": "+966501234567",
  "iqamaNumber": "1234567890",
  "drivingLicenseNumber": "DL123456"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "fullName": "Ahmed Ali Mohammed",
  "phoneNumber": "+966501234567",
  "iqamaNumber": "1234567890",
  "drivingLicenseNumber": "DL123456",
  "status": "AVAILABLE",
  "active": true,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T13:00:00"
}
```

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Driver Not Found",
  "message": "Driver not found"
}
```

---

### 11. Update Driver Status

Updates the status of a driver.

**Endpoint:** `PATCH /api/v1/admin/drivers/{id}/status`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "status": "OFFLINE"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "fullName": "Ahmed Ali",
  "phoneNumber": "+966501234567",
  "iqamaNumber": "1234567890",
  "drivingLicenseNumber": "DL123456",
  "status": "OFFLINE",
  "active": true,
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T14:00:00"
}
```

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Driver Not Found",
  "message": "Driver not found"
}
```

---

### 12. Delete Driver

Deletes a driver from the system.

**Endpoint:** `DELETE /api/v1/admin/drivers/{id}`

**Authentication:** Required (Admin)

**Success Response (204 No Content):**

**Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Driver Not Found",
  "message": "Driver not found"
}
```

---

### 13. Assign Booking

Assigns a vehicle and driver to a booking.

**Endpoint:** `POST /api/v1/admin/bookings/{bookingId}/assign`

**Authentication:** Required (Admin)

**Request Body:**
```json
{
  "vehicleId": 10,
  "driverId": 5
}
```

**Validation Rules:**
- `vehicleId`: Required, must exist and be ACTIVE and AVAILABLE
- `driverId`: Required, must exist and be ACTIVE and AVAILABLE
- Booking must be in PENDING_ASSIGNMENT status
- Vehicle cannot be assigned to multiple active bookings
- Driver cannot be assigned to multiple active bookings

**Success Response (200 OK):**
```json
{
  "bookingId": 1001,
  "bookingNumber": "CG-2026-1001",
  "status": "DRIVER_ASSIGNED",
  "vehicle": {
    "id": 10,
    "vehicleNumber": "KSA1234"
  },
  "driver": {
    "id": 5,
    "fullName": "Ahmed Ali"
  }
}
```

**Error Responses:**

**404 Not Found - Booking not found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Booking Not Found",
  "message": "Booking not found"
}
```

**404 Not Found - Vehicle not found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Vehicle Not Found",
  "message": "Vehicle not found"
}
```

**404 Not Found - Driver not found:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 404,
  "error": "Driver Not Found",
  "message": "Driver not found"
}
```

**400 Bad Request - Booking already assigned:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Booking Already Assigned",
  "message": "Booking is not in PENDING_ASSIGNMENT status"
}
```

**400 Bad Request - Vehicle unavailable:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Vehicle Unavailable",
  "message": "Vehicle is not active"
}
```

**400 Bad Request - Driver unavailable:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Driver Unavailable",
  "message": "Driver is not available"
}
```

**400 Bad Request - Vehicle already assigned:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Vehicle Unavailable",
  "message": "Vehicle is already assigned to an active booking"
}
```

**400 Bad Request - Driver already assigned:**
```json
{
  "timestamp": "2024-06-20T10:00:00",
  "status": 400,
  "error": "Driver Unavailable",
  "message": "Driver is already assigned to an active booking"
}
```

---

## Enum Values

### VehicleStatus
- `AVAILABLE`: Vehicle is available for assignment
- `ASSIGNED`: Vehicle is currently assigned to a booking
- `IN_TRANSIT`: Vehicle is in transit with cargo
- `MAINTENANCE`: Vehicle is under maintenance
- `INACTIVE`: Vehicle is inactive

### DriverStatus
- `AVAILABLE`: Driver is available for assignment
- `ASSIGNED`: Driver is currently assigned to a booking
- `OFFLINE`: Driver is offline
- `BLOCKED`: Driver is blocked from assignments

### VehicleType
- `MINI_TRUCK`: Up to 500 KG
- `PICKUP_TRUCK`: Up to 1 Ton
- `FLATBED_TRUCK`: Up to 3 Tons
- `HEAVY_TRUCK`: Up to 10 Tons

---

## Business Rules

### Vehicle Assignment Rules
1. Vehicle must be ACTIVE
2. Vehicle must be AVAILABLE
3. Cannot assign same vehicle to multiple active bookings
4. After assignment: Vehicle status → ASSIGNED

### Driver Assignment Rules
1. Driver must be ACTIVE
2. Driver must be AVAILABLE
3. Cannot assign same driver to multiple active bookings
4. After assignment: Driver status → ASSIGNED

### Booking Assignment Rules
1. Booking must be in PENDING_ASSIGNMENT status
2. After assignment: Booking status → DRIVER_ASSIGNED
3. Vehicle-Driver mapping is created
4. Assignment is tracked with admin ID

### Status Transitions

**Vehicle:**
- AVAILABLE → ASSIGNED (when assigned to booking)
- ASSIGNED → AVAILABLE (when booking completed)
- AVAILABLE → MAINTENANCE (when sent for maintenance)
- MAINTENANCE → AVAILABLE (when maintenance complete)
- AVAILABLE → INACTIVE (when decommissioned)

**Driver:**
- AVAILABLE → ASSIGNED (when assigned to booking)
- ASSIGNED → AVAILABLE (when booking completed)
- AVAILABLE → OFFLINE (when goes offline)
- OFFLINE → AVAILABLE (when comes online)
- AVAILABLE → BLOCKED (when blocked)
- BLOCKED → AVAILABLE (when unblocked)

---

## Security

### JWT Authentication
All admin endpoints require JWT authentication. The token must be included in the Authorization header:
```
Authorization: Bearer <access_token>
```

### Role-Based Access
- All admin endpoints require authentication
- Admin ID is extracted from JWT token for tracking assignment actions
- In production, role-based authorization (ROLE_ADMIN) should be enforced

---

## Testing with cURL

### Create Vehicle
```bash
curl -X POST http://localhost:8080/api/v1/admin/vehicles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "vehicleNumber": "KSA1234",
    "vehicleType": "PICKUP_TRUCK",
    "capacityKg": 1000,
    "wialonUnitId": "unit_12345"
  }'
```

### Get All Vehicles
```bash
curl -X GET http://localhost:8080/api/v1/admin/vehicles \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Get Vehicle by ID
```bash
curl -X GET http://localhost:8080/api/v1/admin/vehicles/1 \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Update Vehicle Status
```bash
curl -X PATCH http://localhost:8080/api/v1/admin/vehicles/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "status": "MAINTENANCE"
  }'
```

### Create Driver
```bash
curl -X POST http://localhost:8080/api/v1/admin/drivers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "fullName": "Ahmed Ali",
    "phoneNumber": "+966501234567",
    "iqamaNumber": "1234567890",
    "drivingLicenseNumber": "DL123456"
  }'
```

### Get All Drivers
```bash
curl -X GET http://localhost:8080/api/v1/admin/drivers \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Update Driver Status
```bash
curl -X PATCH http://localhost:8080/api/v1/admin/drivers/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "status": "OFFLINE"
  }'
```

### Assign Booking
```bash
curl -X POST http://localhost:8080/api/v1/admin/bookings/1001/assign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "vehicleId": 10,
    "driverId": 5
  }'
```

---

## Notes

- All admin endpoints require JWT authentication
- Vehicle and driver status updates are tracked with timestamps
- Assignment actions are tracked with admin ID from JWT token
- In-memory storage is used for MVP (easily replaceable with database)
- Wialon integration is not implemented yet (placeholder for future)
- Role-based authorization (ROLE_ADMIN) should be added in production
- Phone numbers must be in Saudi format (+966XXXXXXXXX)
- Vehicle numbers must be unique
- Iqama numbers must be unique for drivers
- Business rules prevent double assignment of vehicles and drivers
