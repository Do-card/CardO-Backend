package a107.cardmore.domain.marker.repository;

import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    List<Marker> findAllByUser(User user);
}
