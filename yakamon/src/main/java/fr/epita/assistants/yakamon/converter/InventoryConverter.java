package fr.epita.assistants.yakamon.converter;

import fr.epita.assistants.yakamon.domain.entity.InventoryEntity;
import fr.epita.assistants.yakamon.domain.entity.InventoryItemEntity;
import fr.epita.assistants.yakamon.presentation.api.response.InventoryResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class InventoryConverter {

    public InventoryResponse toInventoryResponse(InventoryEntity entity) {
        List<InventoryResponse.InventoryItemResponse> out = new ArrayList<>();

        if (entity != null && entity.items != null) {
            for (InventoryItemEntity it : entity.items) {
                if (it == null) continue;
                out.add(new InventoryResponse.InventoryItemResponse(it.type, it.quantity));
            }
        }
        return new InventoryResponse(out);
    }
}
