package a107.cardmore.domain.marker.repository;

import a107.cardmore.domain.marker.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkerRepository extends JpaRepository<Marker, Long> {

}
