package fr.epita.assistants.yakamon.domain.entity;

import fr.epita.assistants.yakamon.utils.tile.ItemType;

public class InventoryItemEntity {
    public ItemType type;
    public Integer quantity;

    public InventoryItemEntity() {
    }

    public InventoryItemEntity(ItemType type, Integer quantity) {
        this.type = type;
        this.quantity = quantity;
    }
}
