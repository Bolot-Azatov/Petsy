package kg.petsy.petsy.model;

import jakarta.persistence.*;
import kg.petsy.petsy.model.enums.SosPostType;
import kg.petsy.petsy.model.enums.SosStatus;
import kg.petsy.petsy.model.enums.Species;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sos_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    private SosPostType postType;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false, length = 10)
    private Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed breed;

    @Column(name = "incident_location", columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point incidentLocation;

    @Column(name = "address_text")
    private String addressText;

    @Column(name = "reward_amount", precision = 10, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "contact_phone", nullable = false, length = 50)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SosStatus status = SosStatus.ACTIVE;

    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;

    @OneToMany(mappedBy = "sosPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SosPhoto> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}