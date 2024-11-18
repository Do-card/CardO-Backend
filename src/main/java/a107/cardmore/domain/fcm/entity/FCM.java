package a107.cardmore.domain.fcm.entity;

import a107.cardmore.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE fcm SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "fcm")
public class FCM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
