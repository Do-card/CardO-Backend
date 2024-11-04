package a107.cardmore.domain.item.repository;

import a107.cardmore.domain.item.entity.ItemDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemElasticRepository extends ElasticsearchRepository<ItemDocument, Long> {
}
