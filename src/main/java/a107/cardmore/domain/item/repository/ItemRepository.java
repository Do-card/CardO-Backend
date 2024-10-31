package a107.cardmore.domain.item.repository;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"marker", "marker.user"})
    @Query("""
        SELECT i
        FROM Item i
        WHERE i.marker.user = :user AND
            (:keyword is NULL OR
            :keyword = '' OR
            i.name LIKE CONCAT('%', :keyword, '%'))
    """)
    List<Item> getAllItemsByKeyword(@Param("user") User user, @Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"marker", "marker.user"})
    @Query("""
        SELECT i
        FROM Item i
        WHERE i.marker.user = :user AND
            not i.isDone AND
            (:keyword is NULL OR
            :keyword = '' OR
            i.name LIKE CONCAT('%', :keyword, '%'))
    """)
    List<Item> getItemsByKeyword(@Param("user") User user, @Param("keyword") String keyword);

}
