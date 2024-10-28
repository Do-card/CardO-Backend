package a107.cardmore.domain.discount.entity;

import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.util.constant.MerchantCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE discount SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "discount")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private String merchantCategory;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
