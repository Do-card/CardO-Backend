package a107.cardmore.domain.marker.entity;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.util.base.BaseTimeEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    @Builder.Default
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

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "marker", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    public void updateName(String name){
        this.name = name;
    }

    public void updateLocation(String poiId,Double latitude, Double longitude){
        this.poiId = poiId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateFavorite(Boolean isFavorite){
        this.isFavorite = isFavorite;
    }

}
