package a107.cardmore.domain.item.service;

import a107.cardmore.domain.item.entity.Item;
import a107.cardmore.domain.item.repository.ItemRepository;
import a107.cardmore.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemModuleService {

    private final ItemRepository itemRepository;

    public List<Item> getAllItemsByKeyword(User user, String keyword) {
        return itemRepository.getAllItemsByKeyword(user, keyword);
    }

    public List<Item> getItemsByKeyword(User user, String keyword) {
        return itemRepository.getItemsByKeyword(user, keyword);
    }

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
