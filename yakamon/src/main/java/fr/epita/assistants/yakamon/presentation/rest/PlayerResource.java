package fr.epita.assistants.yakamon.presentation.rest;

import fr.epita.assistants.yakamon.converter.PlayerConverter;
import fr.epita.assistants.yakamon.domain.entity.PlayerEntity;
import fr.epita.assistants.yakamon.domain.service.PlayerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/player")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {

    @Inject PlayerService playerService;
    @Inject PlayerConverter playerConverter;

    @GET
    public Response getPlayer() {
        try {
            PlayerEntity p = playerService.getPlayer();
            return Response.ok(playerConverter.toPlayerResponse(p)).build();
        } catch (IllegalStateException e) {
            return Response.status(400).entity(Map.of("message", e.getMessage())).build();
        }
    }
}
