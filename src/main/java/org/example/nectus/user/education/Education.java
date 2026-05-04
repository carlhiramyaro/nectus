package org.example.nectus.user.education;

import jakarta.persistence.*;
import lombok.*;
import org.example.nectus.user.User;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String degree;

    private String fieldOfStudy;

    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
