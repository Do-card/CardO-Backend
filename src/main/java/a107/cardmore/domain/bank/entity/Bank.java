package a107.cardmore.domain.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE bank SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "bank")
@ToString
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String userKey;

    @Column(nullable = false)
    private String accountNo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
