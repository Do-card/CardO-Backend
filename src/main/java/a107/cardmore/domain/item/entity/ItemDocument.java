package a107.cardmore.domain.item.entity;

import jakarta.persistence.GeneratedValue;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import static org.springframework.data.elasticsearch.annotations.FieldType.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
@Document(indexName = "item_index")
public class ItemDocument {
    @Id
    @GeneratedValue
    @Field(type = Keyword)
    private String id;

    @Field(type = Text)
    private String title;
}
