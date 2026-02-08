package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "machines")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Machine {

    @Id
    @Column(length = 26)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String type;

    @Column(length = 100)
    private String model;

    @Column(name = "serial_number", length = 100, unique = true)
    private String serialNumber;

    @Column(name = "power_rating", length = 50)
    private String powerRating;

    @Column(length = 20)
    @Builder.Default
    private String status = "AVAILABLE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
