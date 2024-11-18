package a107.cardmore.domain.card.entity;

import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.util.base.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE card SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "card")
public class Card extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

    @Column(nullable = false)
    private String cardNo; // user 카드 식별

    @Column(nullable = false)
    private String cardUniqueNo; // 상품 식별

    @Column(nullable = false)
    private String cvc;

    @Column(nullable = false)
    private String cardExpiryDate;

    @Column(nullable = false)
    private Long limitRemaining;

    @Column(nullable = false)
    private Long performanceRemaining;

    @Column
    private String colorBackground;

    @Column
    private String colorTitle;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRepresentativeSelected = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isSelected = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public void changeIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void changeLimitRemaining(Long limitRemaining) {
        this.limitRemaining = limitRemaining;
    }

    public void changeIsRepresentativeSelected(Boolean isRepresentativeSelected) {
        this.isRepresentativeSelected = isRepresentativeSelected;
    }
}
