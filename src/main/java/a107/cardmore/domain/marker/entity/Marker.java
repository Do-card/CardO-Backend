package a107.cardmore.domain.marker.entity;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.marker.dto.MarkerLocationUpdateRequestDto;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.util.base.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE marker SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "marker")
public class Marker extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(name = "name", nullable = false, length = 100)
    private String name = "";

    @Column(name = "poi_id", length = 100)
    private String poiId;

    @Column(name = "poi_name", length = 100)
    private String poiName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "color_background")
    private String colorBackground;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;

    @Column(name = "is_complete", nullable = false)
    @Builder.Default
    private Boolean isComplete = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "marker", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    public void updateName(String name){
        this.name = name;
    }

    public void updateLocation(MarkerLocationUpdateRequestDto requestDto){
        this.poiId = requestDto.getPoiId();
        this.poiName = requestDto.getPoiName();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
    }

    public void updateFavorite(Boolean isFavorite){
        this.isFavorite = isFavorite;
    }

    public void updateComplete(Boolean isComplete){
        this.isComplete = isComplete;
    }

    public void addItem(Item item){
        this.getItems().add(item);
    }
}
