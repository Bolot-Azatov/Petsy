package kg.petsy.petsy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sos_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sos_post_id", nullable = false)
    private SosPost sosPost;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;
}