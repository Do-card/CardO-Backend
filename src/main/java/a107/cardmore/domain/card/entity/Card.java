package a107.cardmore.domain.card.entity;

import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.discount.entity.Discount;
import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.util.base.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false)
    @Builder.Default
    private boolean isSelected = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Discount> discounts = new ArrayList<>();

    public void changeIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void changeLimitRemaining(Long limitRemaining) {
        this.limitRemaining = limitRemaining;
    }
}
