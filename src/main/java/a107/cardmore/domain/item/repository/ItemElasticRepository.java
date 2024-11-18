package a107.cardmore.domain.item.repository;

import a107.cardmore.domain.item.entity.ItemDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemElasticRepository extends ElasticsearchRepository<ItemDocument, String> {
}
