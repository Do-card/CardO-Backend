package a107.cardmore.domain.company.repository;

import a107.cardmore.domain.company.entity.Company;
import a107.cardmore.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserAndCompanyNo (User user, String companyNumber);
    List<Company> findAllByUser(User user);
    Optional<Company> findByIdAndUser(Long companyId, User user);
}
