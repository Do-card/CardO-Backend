package a107.cardmore.util.api.template.response;

import a107.cardmore.util.api.template.header.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class RECResponse<T> {
    @JsonProperty("Header")
    private ResponseHeader Header;
    @JsonProperty("REC")
    private T REC;
}
