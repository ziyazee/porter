# Customer Booking API Documentation

## Overview
This API provides customer booking functionality for the CargoGo logistics application, including price preview, booking creation, and booking management.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
All booking endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <access_token>
```

The customer ID is extracted from the JWT token and used to associate bookings with the authenticated customer.

---

## API Endpoints

### 1. Price Preview

Calculates pricing and available vehicle types for a potential booking.

**Endpoint:** `POST /api/v1/bookings/price-preview`

**Authentication:** Not required (public endpoint)

**Request Body:**
```json
{
  "pickupAddress": "King Fahd Road, Dammam",
  "pickupLatitude": 26.4207,
  "pickupLongitude": 50.0888,
  "pickupDistrict": "Al Faisaliyah",
  "dropAddress": "Al Shati, Dammam",
  "dropLatitude": 26.4512,
  "dropLongitude": 50.1122,
  "dropDistrict": "Al Shati",
  "cargoType": "CONSTRUCTION_MATERIAL",
  "weightRange": "KG_500_1000",
  "specialRequirements": ["LOADING_ASSISTANCE"]
}
```

**Validation Rules:**
- `pickupAddress`: Required, string
- `pickupLatitude`: Required, decimal
- `pickupLongitude`: Required, decimal
- `pickupDistrict`: Required, string (must be serviceable)
- `dropAddress`: Required, string
- `dropLatitude`: Required, decimal
- `dropLongitude`: Required, decimal
- `dropDistrict`: Required, string (must be serviceable)
- `cargoType`: Required, enum value
- `weightRange`: Required, enum value
- `specialRequirements`: Optional, array of enum values

**Success Response (200 OK):**
```json
{
  "pickupZone": "ZONE_A",
  "dropZone": "ZONE_B",
  "basePrice": 120,
  "additionalCharges": 20,
  "totalPrice": 140,
  "currency": "SAR",
  "availableVehicleTypes": [
    {
      "vehicleType": "MINI_TRUCK",
      "capacity": "Up to 500 KG",
      "estimatedAvailability": "Available Now",
      "price": 120
    },
    {
      "vehicleType": "PICKUP_TRUCK",
      "capacity": "Up to 1 Ton",
      "estimatedAvailability": "Available Now",
      "price": 140
    },
    {
      "vehicleType": "FLATBED_TRUCK",
      "capacity": "Up to 3 Tons",
      "estimatedAvailability": "Available Now",
      "price": 170
    },
    {
      "vehicleType": "HEAVY_TRUCK",
      "capacity": "Up to 10 Tons",
      "estimatedAvailability": "Available Now",
      "price": 220
    }
  ]
}
```

**Error Responses:**

**400 Bad Request - District not serviceable:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "District Not Serviceable",
  "message": "Pickup district 'Unknown District' is not serviceable",
  "path": "/api/v1/bookings/price-preview"
}
```

**400 Bad Request - No vehicles available:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "Pricing Not Available",
  "message": "No vehicle types available for this route",
  "path": "/api/v1/bookings/price-preview"
}
```

---

### 2. Create Booking

Creates a new booking with the provided details.

**Endpoint:** `POST /api/v1/bookings`

**Authentication:** Required

**Request Body:**
```json
{
  "pickupAddress": "King Fahd Road, Dammam",
  "pickupLatitude": 26.4207,
  "pickupLongitude": 50.0888,
  "pickupDistrict": "Al Faisaliyah",
  "dropAddress": "Al Shati, Dammam",
  "dropLatitude": 26.4512,
  "dropLongitude": 50.1122,
  "dropDistrict": "Al Shati",
  "cargoType": "CONSTRUCTION_MATERIAL",
  "cargoDescription": "100 cement bags",
  "weightRange": "KG_500_1000",
  "specialRequirements": ["LOADING_ASSISTANCE"],
  "pickupContactName": "Ahmed",
  "pickupContactPhone": "+966501234567",
  "dropContactName": "Mohammed",
  "dropContactPhone": "+966509999999",
  "selectedVehicleType": "PICKUP_TRUCK",
  "paymentMethod": "CASH_TO_DRIVER"
}
```

**Validation Rules:**
- All fields from price preview request are required
- `cargoDescription`: Optional, string
- `pickupContactName`: Required, string
- `pickupContactPhone`: Required, Saudi format (+966XXXXXXXXX)
- `dropContactName`: Required, string
- `dropContactPhone`: Required, Saudi format (+966XXXXXXXXX)
- `selectedVehicleType`: Required, enum value
- `paymentMethod`: Required, enum value (BANK_TRANSFER, CASH_TO_DRIVER)

**Success Response (200 OK):**
```json
{
  "bookingId": 1001,
  "bookingNumber": "CG-2026-1234",
  "status": "PENDING_ASSIGNMENT",
  "pickupZone": "ZONE_A",
  "dropZone": "ZONE_B",
  "totalPrice": 140,
  "currency": "SAR",
  "paymentMethod": "CASH_TO_DRIVER",
  "trackingToken": "abc123xyz789",
  "trackingUrl": "https://cargogo.sa/t/abc123xyz789"
}
```

**Error Responses:**

**400 Bad Request - District not serviceable:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 400,
  "error": "District Not Serviceable",
  "message": "Pickup district 'Unknown District' is not serviceable",
  "path": "/api/v1/bookings"
}
```

**401 Unauthorized:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

---

### 3. Get My Bookings

Retrieves all bookings for the authenticated customer.

**Endpoint:** `GET /api/v1/bookings/my`

**Authentication:** Required

**Request Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1001,
    "bookingNumber": "CG-2026-1234",
    "customerId": 1,
    "pickupAddress": "King Fahd Road, Dammam",
    "pickupLatitude": 26.4207,
    "pickupLongitude": 50.0888,
    "pickupDistrict": "Al Faisaliyah",
    "pickupZone": "ZONE_A",
    "dropAddress": "Al Shati, Dammam",
    "dropLatitude": 26.4512,
    "dropLongitude": 50.1122,
    "dropDistrict": "Al Shati",
    "dropZone": "ZONE_B",
    "cargoType": "CONSTRUCTION_MATERIAL",
    "cargoDescription": "100 cement bags",
    "weightRange": "KG_500_1000",
    "selectedVehicleType": "PICKUP_TRUCK",
    "basePrice": 120,
    "additionalCharges": 20,
    "totalPrice": 140,
    "currency": "SAR",
    "paymentMethod": "CASH_TO_DRIVER",
    "status": "PENDING_ASSIGNMENT",
    "trackingToken": "abc123xyz789",
    "trackingUrl": "https://cargogo.sa/t/abc123xyz789",
    "pickupContactName": "Ahmed",
    "pickupContactPhone": "+966501234567",
    "dropContactName": "Mohammed",
    "dropContactPhone": "+966509999999",
    "specialRequirements": ["LOADING_ASSISTANCE"],
    "createdAt": "2024-06-20T10:00:00",
    "updatedAt": "2024-06-20T10:00:00"
  }
]
```

**Error Responses:**

**401 Unauthorized:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

---

### 4. Get Booking by ID

Retrieves details of a specific booking by ID.

**Endpoint:** `GET /api/v1/bookings/{bookingId}`

**Authentication:** Required

**Request Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "id": 1001,
  "bookingNumber": "CG-2026-1234",
  "customerId": 1,
  "pickupAddress": "King Fahd Road, Dammam",
  "pickupLatitude": 26.4207,
  "pickupLongitude": 50.0888,
  "pickupDistrict": "Al Faisaliyah",
  "pickupZone": "ZONE_A",
  "dropAddress": "Al Shati, Dammam",
  "dropLatitude": 26.4512,
  "dropLongitude": 50.1122,
  "dropDistrict": "Al Shati",
  "dropZone": "ZONE_B",
  "cargoType": "CONSTRUCTION_MATERIAL",
  "cargoDescription": "100 cement bags",
  "weightRange": "KG_500_1000",
  "selectedVehicleType": "PICKUP_TRUCK",
  "basePrice": 120,
  "additionalCharges": 20,
  "totalPrice": 140,
  "currency": "SAR",
  "paymentMethod": "CASH_TO_DRIVER",
  "status": "PENDING_ASSIGNMENT",
  "trackingToken": "abc123xyz789",
  "trackingUrl": "https://cargogo.sa/t/abc123xyz789",
  "pickupContactName": "Ahmed",
  "pickupContactPhone": "+966501234567",
  "dropContactName": "Mohammed",
  "dropContactPhone": "+966509999999",
  "specialRequirements": ["LOADING_ASSISTANCE"],
  "createdAt": "2024-06-20T10:00:00",
  "updatedAt": "2024-06-20T10:00:00"
}
```

**Error Responses:**

**404 Not Found - Booking not found:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 404,
  "error": "Booking Not Found",
  "message": "Booking not found",
  "path": "/api/v1/bookings/9999"
}
```

**403 Forbidden - Access denied:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 403,
  "error": "Access Denied",
  "message": "Access denied: You can only view your own bookings",
  "path": "/api/v1/bookings/1002"
}
```

**401 Unauthorized:**
```json
{
  "timestamp": "2024-06-20T03:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

---

## Enum Values

### BookingStatus
- `PENDING_ASSIGNMENT`: Booking created, waiting for driver assignment
- `DRIVER_ASSIGNED`: Driver assigned to the booking
- `AT_PICKUP`: Driver at pickup location
- `IN_TRANSIT`: Cargo in transit
- `DELIVERED`: Booking completed
- `CANCELLED`: Booking cancelled

### CargoType
- `CONSTRUCTION_MATERIAL`
- `CEMENT`
- `STEEL`
- `TILES`
- `SAND`
- `FURNITURE`
- `EVENT_EQUIPMENT`
- `INDUSTRIAL_EQUIPMENT`
- `APPLIANCES`
- `OTHER`

### WeightRange
- `UP_TO_50_KG`
- `KG_50_100`
- `KG_100_500`
- `KG_500_1000`
- `KG_1000_2000`
- `KG_2000_5000`
- `KG_5000_PLUS`

### VehicleType
- `MINI_TRUCK`: Up to 500 KG
- `PICKUP_TRUCK`: Up to 1 Ton
- `FLATBED_TRUCK`: Up to 3 Tons
- `HEAVY_TRUCK`: Up to 10 Tons

### PaymentMethod
- `BANK_TRANSFER`
- `CASH_TO_DRIVER`

### SpecialRequirement
- `LOADING_ASSISTANCE`: +10 SAR
- `UNLOADING_ASSISTANCE`: +10 SAR
- `FRAGILE_GOODS`: +15 SAR
- `URGENT_DELIVERY`: +25 SAR
- `CRANE_REQUIRED`: +100 SAR
- `TAIL_LIFT_REQUIRED`: +30 SAR

---

## Pricing Logic

### Zone Mapping
Districts are mapped to pricing zones for calculation:
- `Al Faisaliyah` → `ZONE_A`
- `Al Manar` → `ZONE_A`
- `Al Shati` → `ZONE_B`
- `Al Khobar` → `ZONE_C`

### Base Pricing
Base prices are determined by:
- Pickup zone
- Drop zone
- Vehicle type

### Additional Charges
Special requirements add to the base price:
- Loading/Unloading assistance: 10 SAR each
- Fragile goods: 15 SAR
- Urgent delivery: 25 SAR
- Crane required: 100 SAR
- Tail lift required: 30 SAR

### Total Price Calculation
```
Total Price = Base Price + Special Requirement Charges
```

---

## Security

### JWT Authentication
All booking endpoints (except price preview) require JWT authentication. The token must be included in the Authorization header:
```
Authorization: Bearer <access_token>
```

### Customer Isolation
Customers can only access their own bookings. The system automatically extracts the customer ID from the JWT token and ensures customers cannot view other customers' bookings.

### Access Control
- Price preview: Public (no authentication required)
- Create booking: Requires authentication
- Get my bookings: Requires authentication
- Get booking by ID: Requires authentication + ownership verification

---

## Testing with cURL

### Price Preview
```bash
curl -X POST http://localhost:8080/api/v1/bookings/price-preview \
  -H "Content-Type: application/json" \
  -d '{
    "pickupAddress": "King Fahd Road, Dammam",
    "pickupLatitude": 26.4207,
    "pickupLongitude": 50.0888,
    "pickupDistrict": "Al Faisaliyah",
    "dropAddress": "Al Shati, Dammam",
    "dropLatitude": 26.4512,
    "dropLongitude": 50.1122,
    "dropDistrict": "Al Shati",
    "cargoType": "CONSTRUCTION_MATERIAL",
    "weightRange": "KG_500_1000",
    "specialRequirements": ["LOADING_ASSISTANCE"]
  }'
```

### Create Booking
```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "pickupAddress": "King Fahd Road, Dammam",
    "pickupLatitude": 26.4207,
    "pickupLongitude": 50.0888,
    "pickupDistrict": "Al Faisaliyah",
    "dropAddress": "Al Shati, Dammam",
    "dropLatitude": 26.4512,
    "dropLongitude": 50.1122,
    "dropDistrict": "Al Shati",
    "cargoType": "CONSTRUCTION_MATERIAL",
    "cargoDescription": "100 cement bags",
    "weightRange": "KG_500_1000",
    "specialRequirements": ["LOADING_ASSISTANCE"],
    "pickupContactName": "Ahmed",
    "pickupContactPhone": "+966501234567",
    "dropContactName": "Mohammed",
    "dropContactPhone": "+966509999999",
    "selectedVehicleType": "PICKUP_TRUCK",
    "paymentMethod": "CASH_TO_DRIVER"
  }'
```

### Get My Bookings
```bash
curl -X GET http://localhost:8080/api/v1/bookings/my \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Get Booking by ID
```bash
curl -X GET http://localhost:8080/api/v1/bookings/1001 \
  -H "Authorization: Bearer <your_jwt_token>"
```

---

## Notes

- Price preview is public to allow customers to check pricing before registration
- All other booking endpoints require JWT authentication
- Customer ID is extracted from JWT token, not from request body
- Customers can only view their own bookings
- Tracking URL is generated securely and can be shared for real-time tracking
- Pricing zones and matrix are initialized with sample data for MVP
- In production, pricing data would be managed through admin interface
- Special requirement charges are fixed amounts for MVP
- Booking numbers follow format: CG-YYYY-RandomNumber
- Tracking tokens are secure random strings
