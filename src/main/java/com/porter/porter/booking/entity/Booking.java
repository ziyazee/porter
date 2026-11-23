package com.porter.porter.booking.entity;

import com.porter.porter.booking.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_number", unique = true, nullable = false)
    private String bookingNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "pickup_latitude", nullable = false)
    private Double pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false)
    private Double pickupLongitude;

    @Column(name = "pickup_district", nullable = false)
    private String pickupDistrict;

    @Column(name = "pickup_zone", nullable = false)
    private String pickupZone;

    @Column(name = "drop_address", nullable = false)
    private String dropAddress;

    @Column(name = "drop_latitude", nullable = false)
    private Double dropLatitude;

    @Column(name = "drop_longitude", nullable = false)
    private Double dropLongitude;

    @Column(name = "drop_district", nullable = false)
    private String dropDistrict;

    @Column(name = "drop_zone", nullable = false)
    private String dropZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_type", nullable = false)
    private CargoType cargoType;

    @Column(name = "cargo_description")
    private String cargoDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_range", nullable = false)
    private WeightRange weightRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_vehicle_type", nullable = false)
    private VehicleType selectedVehicleType;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @Column(name = "additional_charges", nullable = false)
    private Double additionalCharges;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "tracking_token", unique = true, nullable = false)
    private String trackingToken;

    @Column(name = "pickup_contact_name", nullable = false)
    private String pickupContactName;

    @Column(name = "pickup_contact_phone", nullable = false)
    private String pickupContactPhone;

    @Column(name = "drop_contact_name", nullable = false)
    private String dropContactName;

    @Column(name = "drop_contact_phone", nullable = false)
    private String dropContactPhone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookingSpecialRequirement> specialRequirements = List.of();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
