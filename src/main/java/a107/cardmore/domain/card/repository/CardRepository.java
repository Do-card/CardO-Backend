package a107.cardmore.domain.card.repository;

import a107.cardmore.domain.card.entity.Card;
import a107.cardmore.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByCompanyAndIsSelectedTrue(Company company);
    List<Card> findAllByCompany(Company company);

    @Query("SELECT c FROM Card c WHERE c.company.user.id = :userId AND c.isSelected = true AND c.limitRemaining > 0")
    List<Card> findCardsByUserId(@Param("userId") Long userId);

    Optional<Card> findByCardNo(String cardNo);
}
