package a107.cardmore.domain.marker.service;

import a107.cardmore.domain.marker.dto.MarkerCreateRequestDto;
import a107.cardmore.domain.marker.entity.Marker;
import a107.cardmore.domain.marker.repository.MarkerRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Marker createMarker(User user, MarkerCreateRequestDto requestDto) {
        Marker marker = Marker.builder()
                .user(user)
                .name(requestDto.getName())
                .build();
        return saveMarker(marker);
    }

    public Marker findById(Long markerId){
        return markerRepository.findById(markerId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 마커입니다."));
    }

    public List<Marker> getAllMarkers(User user){
        return markerRepository.findAllByUser(user);
    }

}
