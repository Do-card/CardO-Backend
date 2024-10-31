package a107.cardmore.domain.marker.controller;

import a107.cardmore.domain.marker.dto.*;
import a107.cardmore.domain.marker.service.MarkerService;
import a107.cardmore.util.base.BaseSuccessResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;

    @GetMapping("/all")
    public BaseSuccessResponse<Page<MarkerResponseDto>> getAllMarkers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int size) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.getAllMarkers(userEmail, keyword, page, size));
    }

    @GetMapping
    public BaseSuccessResponse<Page<MarkerResponseDto>> getMarkers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int size) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.getMarkers(userEmail, keyword, page, size));
    }

    @PostMapping
    public BaseSuccessResponse<MarkerResponseDto> createMarker(@RequestBody MarkerCreateRequestDto requestDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.createMarker(userEmail, requestDto));
    }

    @PatchMapping("/{id}/name")
    public BaseSuccessResponse<MarkerResponseDto> updateMarkerName(@PathVariable("id") long markerId, @RequestBody MarkerNameUpdateRequestDto requestDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.updateMarkerName(userEmail, markerId, requestDto));
    }

    @PatchMapping("/{id}/location")
    public BaseSuccessResponse<MarkerResponseDto> updateMarkerLocation(@PathVariable("id") long markerId, @RequestBody MarkerLocationUpdateRequestDto requestDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.updateMarkerLocation(userEmail, markerId, requestDto));
    }

    @PatchMapping("/{id}/favorite")
    public BaseSuccessResponse<MarkerResponseDto> updateMarkerFavorite(@PathVariable("id") long markerId, @RequestBody MarkerFavoriteUpdateRequestDto requestDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.updateMarkerFavorite(userEmail, markerId, requestDto));
    }

    @DeleteMapping("/{id}")
    public BaseSuccessResponse<Void> deleteMarker(@PathVariable("id") long markerId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        markerService.deleteMarker(userEmail, markerId);
        return new BaseSuccessResponse<>(null);
    }

    @PostMapping("/nearby")
    public BaseSuccessResponse<List<MarkerResponseDto>> getNearbyMarkers(@RequestBody MarkerNearbyRequestDto markerNearbyRequestDto){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(markerService.getNearbyMarkers(email, markerNearbyRequestDto));
    }

}
