package a107.cardmore.domain.discount.controller;

import a107.cardmore.domain.discount.dto.DiscountDetailDto;
import a107.cardmore.domain.discount.dto.DiscountDto;
import a107.cardmore.domain.discount.service.DiscountService;
import a107.cardmore.util.base.BaseSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@Slf4j
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping("/all")
    public BaseSuccessResponse<Long> getTotalDiscountPrice(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(discountService.getTotalDiscountPrice(userEmail));
    }

    @GetMapping
    public BaseSuccessResponse<DiscountDto> getTotalDiscountCardData(@RequestParam(name = "year") Integer year, @RequestParam(name = "month") Integer month){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new BaseSuccessResponse<>(discountService.getTotalDiscountCardData(userEmail, year, month));
    }
}
