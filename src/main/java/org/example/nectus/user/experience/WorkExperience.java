package org.example.nectus.user.experience;

import jakarta.persistence.*;
import lombok.*;
import org.example.nectus.user.User;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "work_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String location;

    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private boolean currentRole;


}
