package a107.cardmore.util.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.LocalDateTime;

import static a107.cardmore.util.base.ResponseEnum.SUCCESS;

@Getter
@JsonPropertyOrder({"success", "statusCode", "httpStatus", "message", "timestamp", "result"})
public class BaseSuccessResponse<T> {

    private final boolean success;
    private final int statusCode;
    private final int httpStatus;
    private final String message;
    private final LocalDateTime timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL) //  JSON 출력에서 null이 아닌 속성만 포함.
    private final T result;

    public BaseSuccessResponse(T result) {
        this.success = true;
        this.statusCode = SUCCESS.getStatusCode();
        this.httpStatus = SUCCESS.getHttpStatus();
        this.message = SUCCESS.getMessage();
        this.result = result;
        this.timestamp = LocalDateTime.now().withNano(0);
    }
}
