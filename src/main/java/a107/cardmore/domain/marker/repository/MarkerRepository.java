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

    // 즐겨찾기된 전체 마커 조회
    @Query("""
        SELECT DISTINCT m
        FROM Marker m
        WHERE m.user = :user AND
              m.isFavorite = true
    """)
    List<Marker> findAllByUserAndIsFavoriteTrue(@Param("user") User user);

    // 즐겨찾기 안된 전체 마커 무한 스크롤 조회
    @Query("""
        SELECT DISTINCT m
        FROM Marker m
        WHERE m.user = :user AND
              m.isFavorite = false AND
              (:lastId = 0 OR m.id < :lastId)
        ORDER BY m.id DESC
    """)
    Slice<Marker> findAllByUserAndIsFavoriteFalseAndIdSmallerThan(
            @Param("user") User user,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    // 전체 마커 무한 스크롤 검색 결과 조회
    @EntityGraph(attributePaths = {"items"})
    @Query("""
        SELECT DISTINCT m
        FROM Marker m LEFT JOIN m.items i
        WHERE m.user = :user AND
              (:lastId = 0 OR m.id < :lastId) AND
              (i IS NULL OR i.name LIKE %:keyword%)
        ORDER BY m.id DESC
    """)
    Slice<Marker> findAllByUserAndIsFavoriteFalseAndIdSmallerThanAndItemsNameContaining(
            @Param("user") User user,
            @Param("lastId") Long lastId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"items"})
    @Query("""
        SELECT DISTINCT m
        FROM Marker m JOIN m.items i
        WHERE m.user = :user AND
              m.poiId IS NOT NULL AND
              i.isDone = false
    """)
    List<Marker> findByUserAndPoiIdNotNullAndHasIncompleteItems(@Param("user") User user);

}
