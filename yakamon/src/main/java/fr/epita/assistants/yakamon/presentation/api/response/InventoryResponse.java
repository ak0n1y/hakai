package fr.epita.assistants.yakamon.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.epita.assistants.yakamon.utils.tile.ItemType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryResponse {
    public List<InventoryItemResponse> items;

    public InventoryResponse() {}

    public InventoryResponse(List<InventoryItemResponse> items) {
        this.items = items;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InventoryItemResponse {
        public ItemType itemType;
        public Integer quantity;

        public InventoryItemResponse() {}

        public InventoryItemResponse(ItemType itemType, Integer quantity) {
            this.itemType = itemType;
            this.quantity = quantity;
        }
    }
}
