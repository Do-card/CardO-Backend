package a107.cardmore.domain.marker.mapper;

import a107.cardmore.domain.item.mapper.ItemMapper;
import a107.cardmore.domain.marker.dto.MarkerResponseDto;
import a107.cardmore.domain.marker.entity.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ItemMapper.class})
public interface MarkerMapper {
    MarkerResponseDto toMarkerResponseDto(Marker marker);
}
