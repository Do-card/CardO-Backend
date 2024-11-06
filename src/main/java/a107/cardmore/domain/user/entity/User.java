package a107.cardmore.domain.user.entity;

import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.util.base.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE user SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "user")
@ToString
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String accountNo;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false)
    private String userKey;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public User(String username, String password, String role) {
        this.password = password;
        this.email = username;
        this.role = role;
    }
}
