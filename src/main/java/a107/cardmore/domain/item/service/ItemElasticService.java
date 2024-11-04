package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.entity.ItemDocument;
import a107.cardmore.domain.item.repository.ItemElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.AggregationContainer;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemElasticService {
    private final ItemElasticRepository itemElasticRepository;
//    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void getTrends(){
        IndexCoordinates index = IndexCoordinates.of("user_log");

        Criteria criteria = new Criteria();
        Query query = new CriteriaQuery(criteria);
        /*
        // 집계 추가
        AggregationBuilder aggregation = AggregationContainer.terms("category_counts")
                .field("category.keyword")  // .keyword 필드를 통해 정확히 일치하는 문자열을 기준으로 집계
                .size(10);  // 상위 10개의 카테고리만 반환

        // 쿼리에 집계 추가
        query.addAggregation(aggregation);

        // Elasticsearch로 쿼리 실행 및 집계 결과 가져오기
        AggregationResults aggregationResults = operations.aggregate(query, IndexCoordinates.of("user_log"));

        // 결과를 Map으로 변환
        Map<String, Long> categoryCounts = new HashMap<>();
        aggregationResults.forEach(bucket -> categoryCounts.put(bucket.getKeyAsString(), bucket.getDocCount()));


        SearchHits<?> hits = operations.search(query, ItemDocument.class, index);
        */
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
