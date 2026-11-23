package com.porter.porter.booking.entity;

import com.porter.porter.booking.enums.SpecialRequirement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_special_requirements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSpecialRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement", nullable = false)
    private SpecialRequirement requirement;
}
