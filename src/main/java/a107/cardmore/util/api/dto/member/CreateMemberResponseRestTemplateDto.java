package a107.cardmore.util.api.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMemberResponseRestTemplateDto {
    private String userId;
    private String userName;
    private String institutionCode;
    private String userKey;
    private String created;
    private String modified;
}
