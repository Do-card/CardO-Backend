package a107.cardmore.util.api.dto.FCM;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@lombok.Data
@ToString
public class FCMData {
    String title;
    String message;
    String url;
}
