package fr.epita.assistants.yakamon.domain.service;

import fr.epita.assistants.yakamon.data.model.ItemModel;
import fr.epita.assistants.yakamon.data.repository.ItemRepository;
import fr.epita.assistants.yakamon.data.repository.PlayerRepository;
import fr.epita.assistants.yakamon.domain.entity.InventoryEntity;
import fr.epita.assistants.yakamon.domain.entity.InventoryItemEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class InventoryService {

    @Inject PlayerRepository playerRepository;
    @Inject ItemRepository itemRepository;

    public InventoryEntity getInventory() {
        if (playerRepository.listAll().isEmpty()) {
            throw new IllegalStateException("Game not started.");
        }

        List<InventoryItemEntity> items = new ArrayList<>();
        for (ItemModel m : itemRepository.listAll()) {
            if (m == null || m.getType() == null || m.getQuantity() == null) continue;
            items.add(new InventoryItemEntity(m.getType(), m.getQuantity()));
        }

        return new InventoryEntity(items);
    }
}
