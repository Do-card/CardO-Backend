package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.entity.ItemDocument;
import a107.cardmore.domain.item.repository.ItemElasticRepository;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch.core.termvectors.Term;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.redis.core.convert.Bucket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemElasticService {
    private final ItemElasticRepository itemElasticRepository;
    private final ElasticsearchOperations operations;

    public void getTrends() {
        IndexCoordinates index = IndexCoordinates.of("user_log");
        Criteria criteria = new Criteria("age").is(20);

        // CriteriaQuery 생성
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        // NativeQuery 빌더로 집계 추가
        Query query = NativeQuery.builder()
                .withQuery(criteriaQuery) // Criteria를 쿼리에 추가
                .withMaxResults(0)
                .withAggregation("category_counts", Aggregation.of(a -> a
                        .terms(t -> t
                                .field("category.keyword")
                                .size(2))))
                .build();

        // 검색 실행 및 집계 결과 가져오기
        SearchHits<ItemDocument> searchHits = operations.search(query, ItemDocument.class, index);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();

        // 집계 결과를 Map에 저장하여 반환
        Map<String, Long> result = new HashMap<>();

        List<StringTermsBucket> bucketList = aggregations.aggregationsAsMap().get("category_counts")
                .aggregation().getAggregate().sterms().buckets().array();
        System.out.println(bucketList.size());
        bucketList.forEach(bucket -> {
            System.out.println(bucket.key().stringValue() + " : " + bucket.docCount());
        });
    }

/*
@Transactional(readOnly = true)
    public Slice<PortfolioSearchResponseDto> searchPortfolios(PortfolioSearchRequestDto request, Long cursor, Pageable pageable) {
        IndexCoordinates index = IndexCoordinates.of("portfolio");

        Criteria criteria = new Criteria("allContent");

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            criteria = criteria.matches(request.getKeyword());
        }

        if (request.getIsEmployed() != null) {
            criteria = criteria.and("isEmployed").is(request.getIsEmployed());
        }

        if (request.getMinGpa() != null && request.getMaxGpa() != null) {
            criteria = criteria.and("totalGpa").between(request.getMinGpa(), request.getMaxGpa());
        }

        if(cursor != null) {
            criteria = criteria.and("portfolioId").lessThan(cursor);
        }

        Pageable pageableWithExtra = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() + 1);

        Query query = new CriteriaQuery(criteria).setPageable(pageableWithExtra);

        // Elasticsearch에서 검색 실행
        SearchHits<PortfolioDocument> hits = operations.search(query, PortfolioDocument.class, index);

        List<Long> documents = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPortfolioId())
                .collect(Collectors.toList());

        boolean hasNext = documents.size() > pageable.getPageSize();

        if (hasNext) {
            documents.remove(documents.size() - 1);
        }

        List<PortfolioSearchResponseDto> responses = new ArrayList<>();

        for(long document : documents) {
            Portfolio portfolio = portfolioRepository.findById(document)
                    .orElseThrow(() -> new RuntimeException("portfolio not found"));

            PortfolioSearchResponseDto responseDto = PortfolioSearchResponseDto.builder()
                    .userName(portfolio.getUser().getName())
                    .universityName(portfolio.getUser().getUniversity().getName())
                    .major(portfolio.getUser().getMajor())
                    .totalGpa(portfolio.getTotalGpa())
                    .majorGpa(portfolio.getMajorGpa())
                    .job(portfolio.getJob())
                    .myKeyword(portfolio.getMyKeyword())
                    .awardCount(awardModuleService.countAllByPortfolioId(portfolio.getId()))
                    .certificationCount(certificationModuleService.countAllByPortfolioId(portfolio.getId()))
                    .projectCount(projectModuleService.countAllByPortfolioId(portfolio.getId()))
                    .build();

            responses.add(responseDto);
        }

         return new SliceImpl<>(responses, pageable, hasNext);
    }
* */
}
