package a107.cardmore.domain.marker.service;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.service.ItemModuleService;
import a107.cardmore.domain.marker.dto.*;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.mapper.MarkerMapper;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.global.exception.BadRequestException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkerService {

    private final UserModuleService userModuleService;
    private final MarkerModuleService markerModuleService;
    private final ItemModuleService itemModuleService;
    private final MarkerMapper markerMapper;

    public Page<MarkerResponseDto> getAllMarkers(String email, String keyword, int page, int size) {
        User user = userModuleService.getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<MarkerResponseDto> markerResponseDtoPage;

        if (keyword == null || keyword.isEmpty()) {
            List<Marker> markers = markerModuleService.getAllMarkers(user);
            markerResponseDtoPage = markerListToMarkerDtoPage(markers, pageable);
        } else {
            List<Item> items = itemModuleService.getAllItemsByKeyword(user, keyword);
            markerResponseDtoPage = itemListToMarkerDtoPage(items, pageable);
        }
        return markerResponseDtoPage;
    }

    public Page<MarkerResponseDto> getMarkers(String email, String keyword, int page, int size) {
        User user = userModuleService.getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<MarkerResponseDto> markerResponseDtoPage;

        if (keyword == null || keyword.isEmpty()) {
            List<Marker> markers = markerModuleService.getAllMarkers(user);
            markers.forEach(marker -> marker.getItems().removeIf(Item::getIsDone));
            markerResponseDtoPage = markerListToMarkerDtoPage(markers, pageable);
        } else {
            List<Item> items = itemModuleService.getItemsByKeyword(user, keyword);
            markerResponseDtoPage = itemListToMarkerDtoPage(items, pageable);
        }
        return markerResponseDtoPage;
    }

    private Page<MarkerResponseDto> markerListToMarkerDtoPage(List<Marker> markers, Pageable pageable){
        List<MarkerResponseDto> markerDtos = markers.stream()
                .map(markerMapper::toMarkerResponseDto)
                .toList();
        return new PageImpl<>(markerDtos, pageable, markerDtos.size());
    }

    private Page<MarkerResponseDto> itemListToMarkerDtoPage(List<Item> items, Pageable pageable) {
        Map<Marker, List<Item>> groupedItems = items.stream()
                .collect(Collectors.groupingBy(Item::getMarker));
        List<MarkerResponseDto> markerDtos = groupedItems.entrySet().stream()
                .map(entry -> {
                    Marker marker = entry.getKey();
                    marker.getItems().clear(); // 기존 아이템 리스트 초기화
                    marker.getItems().addAll(entry.getValue()); // 새로 그룹화된 아이템 추가
                    return markerMapper.toMarkerResponseDto(marker);
                })
                .toList();

        return new PageImpl<>(markerDtos, pageable, markerDtos.size());
    }

    public MarkerResponseDto createMarker(String email, MarkerCreateRequestDto requestDto) {
        User user = userModuleService.getUserByEmail(email);
        Marker marker = markerModuleService.createMarker(user, requestDto);
        return markerMapper.toMarkerResponseDto(marker);
    }

    public MarkerResponseDto updateMarkerName(String email, long markerId, MarkerNameUpdateRequestDto requestDto){
        Marker marker = getValidatedMarker(email, markerId);
        marker.updateName(requestDto.getName());
        return markerMapper.toMarkerResponseDto(markerModuleService.saveMarker(marker));
    }

    public MarkerResponseDto updateMarkerLocation(String email, long markerId, MarkerLocationUpdateRequestDto requestDto){
        Marker marker = getValidatedMarker(email, markerId);
        marker.updateLocation(requestDto.getPoiId(), requestDto.getLatitude(), requestDto.getLongitude());
        return markerMapper.toMarkerResponseDto(markerModuleService.saveMarker(marker));
    }

    public MarkerResponseDto updateMarkerFavorite(String email, long markerId, MarkerFavoriteUpdateRequestDto requestDto){
        Marker marker = getValidatedMarker(email, markerId);
        marker.updateFavorite(requestDto.getIsFavorite());
        return markerMapper.toMarkerResponseDto(markerModuleService.saveMarker(marker));
    }

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
    public List<MarkerResponseDto> getNearbyMarkers(String email,
        MarkerNearbyRequestDto markerNearbyRequestDto) {
        final int NEARBY_DISTANCE = 100; // 알림 보낼 거리 기준 거리
        User user = userModuleService.getUserByEmail(email);
        List<Marker> userMarkers = markerModuleService.getAllMarkers(user);
        List<MarkerResponseDto> nearbyMarkers = new ArrayList<>();
        for (Marker marker : userMarkers) {
            //장소 등록 안되어있는 경우
            if(marker.getPoiId() == null){
                continue;
            }
            //하나라도 완료하지 않은 item이 있는 마커만 리턴
            if(!hasIncompleteItem(marker)){
                continue;
            }
            double currentDistance = calculateDistance(markerNearbyRequestDto.getLatitude(), markerNearbyRequestDto.getLongitude(),
                marker.getLatitude(), marker.getLongitude());
            if(currentDistance<=NEARBY_DISTANCE){
                nearbyMarkers.add(markerMapper.toMarkerResponseDto(marker));
            }
        }
        return nearbyMarkers;
    }

    private boolean hasIncompleteItem(Marker marker) {
        for (Item item : marker.getItems()) {
            if(!item.getIsDone()){
                return true;
            }
        }
        return false;
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
