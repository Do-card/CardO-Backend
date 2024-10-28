package a107.cardmore.util.api.template.response;

import a107.cardmore.util.api.template.header.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RECListResponse<T> {
    @JsonProperty("Header")
    private ResponseHeader HEADER;
    @JsonProperty("REC")
    private List<T> REC;
}
