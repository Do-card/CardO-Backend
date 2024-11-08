package a107.cardmore.util.api.template.header;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class FCMHeader {
    String Authorization;
}
