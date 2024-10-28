package a107.cardmore.domain.discount.repository;

import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.discount.entity.Discount;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.util.constant.MerchantCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount,Long> {
    Optional<Discount> findByCardId(Long cardId);
    Optional<List<Discount>> findAllByCategoryIdAndCardId(String categoryId, Long cardId);

    @Query(value = "select d.merchantCategory, d.card, sum(d.price) as totalAmount " +
            "from Discount d " +
            "where d.user.id = :userId " +
            "and function('month', d.paymentDate) = :month " +
            "and function('year', d.paymentDate) = :year " +
            "group by d.merchantCategory, d.card.id"
    )
    List<Object[]> findAllByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") Integer year, @Param("month") Integer month);

    @Query(value = "select coalesce(sum(d.price), 0) " +
            "from Discount d " +
            "where d.user = :user " +
            "and  month(d.paymentDate) = :month " +
            "and  year(d.paymentDate) = :year "
    )
    Long totalUserDiscountPrice(@Param("user")User user, Integer year, Integer month);
}
