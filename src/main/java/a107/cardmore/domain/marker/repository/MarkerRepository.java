package a107.cardmore.domain.marker.repository;

import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    
    @EntityGraph(attributePaths = {"items"})
    Slice<Marker> findAllByUserAndIdGreaterThan(User user, Long lastId, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    @Query("""
        SELECT DISTINCT m
        FROM Marker m JOIN m.items i
        WHERE m.user = :user AND
              i.name LIKE %:keyword% AND
              m.id > :lastId
    """)
    Slice<Marker> findAllByUserAndItemsNameContainingAndIdGreaterThan(User user, String keyword, Long lastId, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    @Query("""
        SELECT DISTINCT m
        FROM Marker m JOIN m.items i
        WHERE m.user = :user AND
              i.name LIKE %:keyword% AND
              i.isDone = false AND
              m.id > :lastId
    """)
    Slice<Marker> findByUserAndItemsNameContainingAndIsDoneFalseAndMarkerIdGreaterThan(
            @Param("user") User user,
            @Param("keyword") String keyword,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT m
        FROM Marker m JOIN m.items i
        WHERE m.user = :user AND
              m.poiId IS NOT NULL AND
              i.isDone = false
    """)
    List<Marker> findByUserAndPoiIdNotNullAndHasIncompleteItems(@Param("user") User user);

}
