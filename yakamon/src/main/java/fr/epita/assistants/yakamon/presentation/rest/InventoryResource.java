package fr.epita.assistants.yakamon.presentation.rest;

import fr.epita.assistants.yakamon.converter.InventoryConverter;
import fr.epita.assistants.yakamon.domain.service.InventoryService;
import fr.epita.assistants.yakamon.utils.ErrorInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @Inject InventoryService inventoryService;
    @Inject InventoryConverter inventoryConverter;

    @GET
    public Response inventory() {
        try {
            var entity = inventoryService.getInventory();
            var out = inventoryConverter.toInventoryResponse(entity);
            return Response.ok(out).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo(e.getMessage()))
                    .build();
        }
    }
}
