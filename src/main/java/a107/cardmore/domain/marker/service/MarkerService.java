package a107.cardmore.domain.marker.service;

import a107.cardmore.domain.marker.dto.MarkerCreateRequestDto;
import a107.cardmore.domain.marker.dto.MarkerFavoriteUpdateRequestDto;
import a107.cardmore.domain.marker.dto.MarkerLocationUpdateRequestDto;
import a107.cardmore.domain.marker.dto.MarkerNameUpdateRequestDto;
import a107.cardmore.domain.marker.dto.MarkerNearbyRequestDto;
import a107.cardmore.domain.marker.dto.MarkerResponseDto;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.mapper.MarkerMapper;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.global.exception.BadRequestException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkerService {

    private final UserModuleService userModuleService;
    private final MarkerModuleService markerModuleService;
    private final MarkerMapper markerMapper;

    public List<MarkerResponseDto> getAllFavoriteMarkers(String email){
        User user = userModuleService.getUserByEmail(email);
        return markerModuleService.findAllByUserAndIsFavoriteTrue(user)
                .stream()
                .map(markerMapper::toMarkerResponseDto)
                .toList();
    }

    public Slice<MarkerResponseDto> getAllMarkersByKeyword(String email, String keyword, Long lastId, int limit) {
        User user = userModuleService.getUserByEmail(email);
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Order.desc("isFavorite"), Sort.Order.desc("id"))
        );
        Slice<Marker> markers;

        if (keyword == null || keyword.isEmpty()) {
            markers = markerModuleService.findAllByUserAndIsFavoriteFalseAndIdSmallerThan(user, lastId, pageable);
        } else {
            markers = markerModuleService.findAllByUserAndIsFavoriteFalseAndIdSmallerThanAndItemsNameContaining(user, keyword, lastId, pageable);
        }
        return markers.map(marker -> {
            MarkerResponseDto responseDto = markerMapper.toMarkerResponseDto(marker);
            responseDto.getItems().sort(Comparator.comparingInt(item -> item.getId().intValue()));
            return responseDto;
        });
    }

    @Transactional
    public MarkerResponseDto createMarker(String email, MarkerCreateRequestDto requestDto) {
        User user = userModuleService.getUserByEmail(email);
        Marker marker = markerModuleService.createMarker(user, requestDto);
        return markerMapper.toMarkerResponseDto(marker);
    }

    @Transactional
    public MarkerResponseDto updateMarkerName(String email, long markerId, MarkerNameUpdateRequestDto requestDto){
        Marker marker = getValidatedMarker(email, markerId);
        marker.updateName(requestDto.getName());
        return markerMapper.toMarkerResponseDto(markerModuleService.saveMarker(marker));
    }

    @Transactional
    public MarkerResponseDto updateMarkerLocation(String email, long markerId, MarkerLocationUpdateRequestDto requestDto){
        Marker marker = getValidatedMarker(email, markerId);
        marker.updateLocation(requestDto);
        return markerMapper.toMarkerResponseDto(markerModuleService.saveMarker(marker));
    }

    @Transactional
    public MarkerResponseDto updateMarkerFavorite(String email, long markerId, MarkerFavoriteUpdateRequestDto requestDto){
        Marker marker = getValidatedMarker(email, markerId);
        marker.updateFavorite(requestDto.getIsFavorite());
        return markerMapper.toMarkerResponseDto(markerModuleService.saveMarker(marker));
    }

    @Transactional
    public void deleteMarker(String email, long markerId) {
        Marker marker = getValidatedMarker(email, markerId);
        markerModuleService.deleteMarker(marker);
    }

    private Marker getValidatedMarker(String email, long markerId){
        User user = userModuleService.getUserByEmail(email);
        Marker marker = markerModuleService.findById(markerId);

        if (marker == null){
            throw new BadRequestException("마커가 존재하지 않습니다.");
        }

        if (user.getId() != marker.getUser().getId()){
            throw new BadRequestException("유효하지 않은 마커입니다.");
        }
        return marker;
    }

    //TODO: Redis 캐싱 로직 추가, FCM 알림 추가
    public List<MarkerResponseDto> getNearbyMarkers(String email, MarkerNearbyRequestDto markerNearbyRequestDto) {
        final int NEARBY_DISTANCE = 500; // 알림 보낼 거리 기준 거리
        User user = userModuleService.getUserByEmail(email);
        List<Marker> userMarkers = markerModuleService.findByUserAndPoiIdNotNullAndHasIncompleteItems(user);
        List<MarkerResponseDto> nearbyMarkers = new ArrayList<>();

        for (Marker marker : userMarkers) {
            double currentDistance = calculateDistance(
                    markerNearbyRequestDto.getLatitude(),
                    markerNearbyRequestDto.getLongitude(),
                    marker.getLatitude(),
                    marker.getLongitude()
            );
            if(currentDistance <= NEARBY_DISTANCE){
                nearbyMarkers.add(markerMapper.toMarkerResponseDto(marker));
            }
        }
        return nearbyMarkers;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_M = 6371000; // 지구 반지름 (단위: m)

        // 위도 및 경도를 라디안으로 변환
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine 공식을 이용하여 두 지점 간의 거리 계산
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리를 m 단위로 반환
        return EARTH_RADIUS_M * c;
    }
}
