package fr.epita.assistants.yakamon.domain.entity;

import java.util.List;

public class InventoryEntity {
    public List<InventoryItemEntity> items;

    public InventoryEntity() {
    }

    public InventoryEntity(List<InventoryItemEntity> items) {
        this.items = items;
    }
}
