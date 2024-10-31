package a107.cardmore.domain.marker.service;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.service.ItemModuleService;
import a107.cardmore.domain.marker.dto.*;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.mapper.MarkerMapper;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import a107.cardmore.global.exception.BadRequestException;
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

}
