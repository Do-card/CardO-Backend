package a107.cardmore.domain.marker.service;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.marker.dto.MarkerCreateRequestDto;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.repository.MarkerRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkerModuleService {

    private final MarkerRepository markerRepository;

    @CachePut(value = "marker", key = "#marker.id", condition = "#marker.id != null")
    public Marker saveMarker(Marker marker){
        return markerRepository.save(marker);
    }

    @CacheEvict(value = "marker", key = "#marker.id")
    public void deleteMarker(Marker marker){
        markerRepository.delete(marker);
    }

    @CacheEvict(value = "nearbyMarkers", key = "#user.id", allEntries = true)
    public Marker createMarker(User user, MarkerCreateRequestDto requestDto) {
        Marker marker = Marker.builder()
                .user(user)
                .name(requestDto.getName())
                .colorBackground(requestDto.getColorBackground())
                .build();
        return saveMarker(marker);
    }

    @Cacheable(value = "marker", key = "#markerId")
    public Marker findById(Long markerId){
        return markerRepository.findById(markerId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 마커입니다."));
    }

    // 즐겨찾기된 전체 마커 조회
    public List<Marker> findAllByUserAndIsFavoriteTrue(User user) {
        return markerRepository.findAllByUserAndIsFavoriteTrue(user);
    }

    // 즐겨찾기된 미완료 마커 조회
    public List<Marker> findAllByUserAndIsFavoriteTrueAndItemsIsDoneFalse(User user) {
        List<Marker> markers = markerRepository.findAllByUserAndIsFavoriteTrueAndItemsIsDoneFalse(user);
        markers.forEach(marker -> marker.getItems().removeIf(Item::getIsDone));
        return markers;
    }

    // 즐겨찾기 안된 전체 마커 무한 스크롤 조회
    public Slice<Marker> findAllByUserAndIsFavoriteFalseAndIdGreaterThan(User user, Long lastId, Pageable pageable){
        return markerRepository.findAllByUserAndIsFavoriteFalseAndIdGreaterThan(user, lastId, pageable);
    }

    // 전체 마커 무한 스크롤 검색 결과 조회
    public Slice<Marker> findAllByUserAndIsFavoriteFalseAndIdGreaterThanAndItemsNameContaining(User user, String keyword, Long lastId, Pageable pageable) {
        Slice<Marker> markers = markerRepository.findAllByUserAndIsFavoriteFalseAndIdGreaterThanAndItemsNameContaining(user, lastId, keyword, pageable);
        markers.forEach(marker ->
                marker.getItems().removeIf(item -> item.getIsDone() || !item.getName().contains(keyword)));
        return markers;
    }

    // 즐겨찾기 안된 미완료 마커 무한 스크롤 조회
    public Slice<Marker> findByUserAndIsFavoriteFalseAndItemsIsDoneFalseAndIdGreaterThan(User user, Long lastId, Pageable pageable){
        Slice<Marker> markers = markerRepository.findByUserAndIsFavoriteFalseAndItemsIsDoneFalseAndIdGreaterThan(user, lastId, pageable);
        markers.forEach(marker -> marker.getItems().removeIf(Item::getIsDone));
        return markers;
    }

    // 미완료 마커 무한 스크롤 검색 결과 조회
    public Slice<Marker> findByUserAndIsFavoriteFalseAndItemsIsDoneFalseAndIdGreaterThanAndItemsNameContaining(User user, String keyword, Long lastId, Pageable pageable){
        Slice<Marker> markers = markerRepository.findByUserAndIsFavoriteFalseAndItemsIsDoneFalseAndIdGreaterThanAndItemsNameContaining(user, lastId, keyword, pageable);
        markers.forEach(marker ->
                marker.getItems().removeIf(item -> item.getIsDone() || !item.getName().contains(keyword)));
        return markers;
    }

    @Cacheable(value = "nearbyMarkers", key = "#user.id")
    public List<Marker> findByUserAndPoiIdNotNullAndHasIncompleteItems(User user){
        return markerRepository.findByUserAndPoiIdNotNullAndHasIncompleteItems(user);
    }

}
