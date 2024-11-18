package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.dto.ItemLocalTrendResponseDto;
import a107.cardmore.domain.item.dto.ItemTrendResponseDto;
import a107.cardmore.domain.item.dto.ItemTrendSubResponseDto;
import a107.cardmore.domain.item.entity.ItemDocument;
import a107.cardmore.domain.item.repository.ItemElasticRepository;
import a107.cardmore.domain.user.entity.User;
import a107.cardmore.domain.user.service.UserModuleService;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemElasticService {
    private final ItemElasticRepository itemElasticRepository;
    private final ElasticsearchOperations operations;
    private final UserModuleService userModuleService;
    private static final boolean IS_NOT_USER = false;
    private static final boolean IS_USER = true;
    private static final String INDEX_NAME = "user_log";
    private static final Integer CATEGORY_COUNT = 3;

    public ItemTrendResponseDto getTrends(String email) {
        IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
        User user = userModuleService.getUserByEmail(email);

        List<StringTermsBucket> mainBucketList = mainTrendFunc(index, user, IS_NOT_USER);
        List<StringTermsBucket> userBucketList = mainTrendFunc(index, user, IS_USER);

        return ItemTrendResponseDto.builder()
                .mainTrend(convertBucketsToTrendList(mainBucketList))
                .userTrend(convertBucketsToTrendList(userBucketList))
            .build();
    }

    private List<StringTermsBucket> mainTrendFunc(IndexCoordinates index, User user, boolean isUser) {
        Long age = (long) ((LocalDate.now().getYear() - user.getBirthday().getYear()) / 10 * 10);
        Criteria criteria = isUser ? new Criteria("user_id").is(user.getId()) : new Criteria("age").is(age);

        // CriteriaQuery 생성
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        Query query = NativeQuery.builder()
            .withQuery(criteriaQuery)
            .withMaxResults(0)
            .withAggregation("category_counts", Aggregation.of(a -> a
                .terms(t -> t.field("major_category.keyword").size(CATEGORY_COUNT))))
            .build();

        SearchHits<ItemDocument> searchHits = operations.search(query, ItemDocument.class, index);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();

        return aggregations.aggregationsAsMap().get("category_counts")
            .aggregation().getAggregate().sterms().buckets().array();
    }

    private List<ItemTrendSubResponseDto> convertBucketsToTrendList(List<StringTermsBucket> buckets) {
        AtomicLong rankCounter = new AtomicLong(1L);
        List<ItemTrendSubResponseDto> trendList = new ArrayList<>();
        buckets.forEach(bucket -> trendList.add(ItemTrendSubResponseDto.builder()
            .rank(rankCounter.getAndIncrement())
            .category(bucket.key().stringValue())
            .count(bucket.docCount())
            .build()));
        return trendList;
    }


    public List<ItemLocalTrendResponseDto> getLocalTrends(String poiId){
        IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);

        // Criteria 생성: 특정 poiId를 기준으로 조회
        Criteria criteria = new Criteria("poi_id.keyword").is(poiId);

        // CriteriaQuery 생성
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        // Query 빌드: category 필드에 대해 상위 2개 카테고리 집계 수행
        Query query = NativeQuery.builder()
            .withQuery(criteriaQuery) // Criteria를 쿼리에 추가
            .withMaxResults(0)
            .withAggregation("top_categories", Aggregation.of(a -> a
                .terms(t -> t
                    .field("category.keyword")
                    .size(3)))) // 상위 3개의 category만 추출
            .build();

        // Elasticsearch 조회 수행
        SearchHits<ItemDocument> searchHits = operations.search(query, ItemDocument.class, index);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();

        // 집계된 top_categories의 결과를 bucket에서 추출
        List<StringTermsBucket> categoryBuckets = null;
        if (aggregations != null) {
            categoryBuckets = aggregations.aggregationsAsMap()
                .get("top_categories")
                .aggregation().getAggregate().sterms().buckets().array();
        }

        List<ItemLocalTrendResponseDto> itemLocalTrendResponseDtos = new ArrayList<>();
        categoryBuckets.forEach(bucket -> {
            itemLocalTrendResponseDtos.add(new ItemLocalTrendResponseDto(bucket.key().stringValue()));
        });
        return itemLocalTrendResponseDtos;
    }
}
