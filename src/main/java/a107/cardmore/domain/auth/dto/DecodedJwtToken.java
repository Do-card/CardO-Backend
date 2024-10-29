package a107.cardmore.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DecodedJwtToken {
    private Long userId;
    private String role;
    private String type;
}
