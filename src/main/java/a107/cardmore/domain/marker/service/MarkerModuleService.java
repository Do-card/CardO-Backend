package a107.cardmore.domain.marker.service;

import a107.cardmore.domain.marker.dto.MarkerCreateRequestDto;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.repository.MarkerRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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

    public Marker saveMarker(Marker marker){
        return markerRepository.save(marker);
    }

    public void deleteMarker(Marker marker){
        markerRepository.delete(marker);
    }

    //@CacheEvict(value = "nearbyMarkers", key = "#user.id", allEntries = true)
    public Marker createMarker(User user, MarkerCreateRequestDto requestDto) {
        Marker marker = Marker.builder()
                .user(user)
                .name(requestDto.getName())
                .colorBackground(requestDto.getColorBackground())
                .build();
        return saveMarker(marker);
    }

    public Marker findById(Long markerId){
        return markerRepository.findById(markerId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 마커입니다."));
    }

    // 즐겨찾기된 전체 마커 조회
    public List<Marker> findAllByUserAndIsFavoriteTrue(User user) {
        return markerRepository.findAllByUserAndIsFavoriteTrue(user);
    }

    // 즐겨찾기 안된 전체 마커 무한 스크롤 조회
    public Slice<Marker> findAllByUserAndIsFavoriteFalseAndIdSmallerThan(User user, Long lastId, Pageable pageable){
        return markerRepository.findAllByUserAndIsFavoriteFalseAndIdSmallerThan(user, lastId, pageable);
    }

    // 전체 마커 무한 스크롤 검색 결과 조회
    public Slice<Marker> findAllByUserAndIsFavoriteFalseAndIdSmallerThanAndItemsNameContaining(User user, String keyword, Long lastId, Pageable pageable) {
        Slice<Marker> markers = markerRepository.findAllByUserAndIsFavoriteFalseAndIdSmallerThanAndItemsNameContaining(user, lastId, keyword, pageable);
        markers.forEach(marker -> marker.getItems().removeIf(item -> !item.getName().contains(keyword)));
        return markers;
    }

    //@Cacheable(value = "nearbyMarkers", key = "#user.id")
    public List<Marker> findByUserAndPoiIdNotNullAndHasIncompleteItems(User user){
        return markerRepository.findByUserAndPoiIdNotNullAndHasIncompleteItems(user);
    }

}
