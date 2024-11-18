package a107.cardmore.domain.auth.mapper;

import a107.cardmore.domain.auth.dto.RegisterResponseDto;
import a107.cardmore.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {
    RegisterResponseDto toRegisterResponseDto(User user);
}
