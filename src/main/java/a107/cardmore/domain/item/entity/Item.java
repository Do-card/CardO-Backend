package a107.cardmore.domain.item.entity;

import a107.cardmore.domain.item.dto.ItemRequestDto;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.util.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE item SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "item")
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marker_id", nullable = false)
    private Marker marker;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "is_done", nullable = false)
    @Builder.Default
    private Boolean isDone = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public void update(ItemRequestDto itemRequestDto){
        this.name = itemRequestDto.getName();
        this.category = itemRequestDto.getCategory();
    }

    public void changeState(){
        this.isDone = !this.isDone;
    }

    public void updateMarker(Marker marker) {
        this.marker = marker;
    }
}
