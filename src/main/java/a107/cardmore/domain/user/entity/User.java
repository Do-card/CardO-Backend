package a107.cardmore.domain.user.entity;

import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.util.base.BaseTimeEntity;
import jakarta.persistence.*;
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

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100)
    private String accountNo;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false)
    private String userKey;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Marker> markers = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Company> companies = new ArrayList<>();



    public User(String username, String password, String role) {
        this.password = password;
        this.email = username;
        this.role = role;
    }
}
