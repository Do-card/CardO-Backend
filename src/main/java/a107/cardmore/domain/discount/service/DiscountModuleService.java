package a107.cardmore.domain.discount.service;

import a107.cardmore.domain.discount.entity.Discount;
import a107.cardmore.domain.discount.repository.DiscountRepository;
import a107.cardmore.global.exception.BadRequestException;
import a107.cardmore.util.constant.MerchantCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountModuleService {
    private final DiscountRepository discountRepository;

    public Discount findDiscountByCardId(Long cardId) {
        return discountRepository.findByCardId(cardId).orElseThrow(()->new BadRequestException("존재하지 않는 카드입니다."));
    }

    public Long findPriceByCategoryAndCardId(String categoryId, Long cardId) {

        return discountRepository.findAllByCategoryIdAndCardId(categoryId,cardId).orElse(new ArrayList<Discount>()).stream().mapToLong(Discount::getPrice).sum();
    }

    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }
}
