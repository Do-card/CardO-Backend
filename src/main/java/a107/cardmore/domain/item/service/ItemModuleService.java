package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemModuleService {

    private final ItemRepository itemRepository;

    public Item findById(Long id){
        return itemRepository.findById(id).orElseThrow();
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
}
