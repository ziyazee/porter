-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone_number (phone_number)
);

-- Create otps table
CREATE TABLE IF NOT EXISTS otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    last_attempt_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_otp_code (otp_code)
);

-- Create pricing_zones table
CREATE TABLE IF NOT EXISTS pricing_zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_name VARCHAR(200) UNIQUE NOT NULL,
    zone_code VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_district_name (district_name),
    INDEX idx_zone_code (zone_code)
);

-- Create pricing_matrix table
CREATE TABLE IF NOT EXISTS pricing_matrix (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_zone VARCHAR(50) NOT NULL,
    to_zone VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    base_price DOUBLE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_route (from_zone, to_zone),
    INDEX idx_vehicle_type (vehicle_type)
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(50) UNIQUE NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    capacity_kg DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    wialon_unit_id VARCHAR(100),
    current_driver_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vehicle_number (vehicle_number),
    INDEX idx_status (status),
    INDEX idx_active (active)
);

-- Create drivers table
CREATE TABLE IF NOT EXISTS drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    iqama_number VARCHAR(50) UNIQUE NOT NULL,
    driving_license_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone_number (phone_number),
    INDEX idx_iqama_number (iqama_number),
    INDEX idx_status (status),
    INDEX idx_active (active)
);

-- Create vehicle_driver_mapping table
CREATE TABLE IF NOT EXISTS vehicle_driver_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_active (active)
);

-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    pickup_address VARCHAR(500) NOT NULL,
    pickup_latitude DOUBLE NOT NULL,
    pickup_longitude DOUBLE NOT NULL,
    pickup_district VARCHAR(200) NOT NULL,
    pickup_zone VARCHAR(50) NOT NULL,
    drop_address VARCHAR(500) NOT NULL,
    drop_latitude DOUBLE NOT NULL,
    drop_longitude DOUBLE NOT NULL,
    drop_district VARCHAR(200) NOT NULL,
    drop_zone VARCHAR(50) NOT NULL,
    cargo_type VARCHAR(50) NOT NULL,
    cargo_description TEXT,
    weight_range VARCHAR(50) NOT NULL,
    selected_vehicle_type VARCHAR(50) NOT NULL,
    base_price DOUBLE NOT NULL,
    additional_charges DOUBLE NOT NULL,
    total_price DOUBLE NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'SAR',
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_ASSIGNMENT',
    tracking_token VARCHAR(100) UNIQUE NOT NULL,
    pickup_contact_name VARCHAR(200) NOT NULL,
    pickup_contact_phone VARCHAR(20) NOT NULL,
    drop_contact_name VARCHAR(200) NOT NULL,
    drop_contact_phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_booking_number (booking_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_tracking_token (tracking_token),
    INDEX idx_status (status),
    INDEX idx_pickup_zone (pickup_zone),
    INDEX idx_drop_zone (drop_zone)
);

-- Create booking_special_requirements table
CREATE TABLE IF NOT EXISTS booking_special_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    requirement VARCHAR(50) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_booking_id (booking_id)
);

-- Create booking_assignments table
CREATE TABLE IF NOT EXISTS booking_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    assigned_by BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,
    INDEX idx_booking_id (booking_id),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_driver_id (driver_id)
);
