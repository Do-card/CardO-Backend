package a107.cardmore.domain.item.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
@Document(indexName = "user_log")
public class ItemDocument {
    @Id
    @Field(type = Keyword)
    private String id;

    @Field(type = Text)
    private String category;

    @Field(type = Text)
    private String majorCategory;

    @Field(type=Text)
    private String poiId;

    @Field(type = Long)
    private long userId;

    @Field(type = Long)
    private long markerId;

    @Field(type = Long)
    private long itemId;

    @Field(type = Text)
    private String itemName;

    @Field(type = Float)
    private float latitude;

    @Field(type = Float)
    private float longitude;

    @Field(type = Long)
    private long age;

    @Field(type = Date)
    private Date userBirth;

}
