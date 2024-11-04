package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.repository.ItemElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemElasticService {
    private final ItemElasticRepository itemElasticRepository;

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
