package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    @OneToMany(mappedBy = "trip",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<TripActivity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "trip",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<TripAccommodation> accommodations = new ArrayList<>();

    public Trip(Object o, @NotBlank(message = "name must not be blank") @Size(max = 200, message = "name length must not exceed 200 characters") String name, @NotNull(message = "startDate must not be null") @FutureOrPresent(message = "startDate must be today or a future date") LocalDate startDate, @NotNull(message = "endDate must not be null") LocalDate endDate) {
    }
}

