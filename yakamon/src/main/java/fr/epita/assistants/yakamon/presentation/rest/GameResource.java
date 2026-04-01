package fr.epita.assistants.yakamon.presentation.rest;

import fr.epita.assistants.yakamon.converter.GameConverter;
import fr.epita.assistants.yakamon.domain.service.GameService;
import fr.epita.assistants.yakamon.presentation.api.request.*;
import fr.epita.assistants.yakamon.presentation.api.response.*;
import fr.epita.assistants.yakamon.utils.ErrorInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    @Inject GameService gameService;
    @Inject GameConverter gameConverter;

    @POST
    @Path("/start")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response start(StartRequest req) {
        if (req == null || isBlank(req.playerName) || isBlank(req.mapPath)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Invalid `path` or invalid `name` provided."))
                    .build();
        }

        try {
            var game = gameService.startNewGame(req.mapPath.trim(), req.playerName.trim());
            StartResponse out = gameConverter.toStartResponse(game);
            return Response.ok(out).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/move")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response move(MoveRequest req) {
        if (req == null || req.direction == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Invalid `direction` provided."))
                    .build();
        }

        try {
            var entity = gameService.move(req.direction);
            MoveResponse out = gameConverter.toMoveResponse(entity);
            return Response.ok(out).build();
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("recent")) {
                return Response.status(429).entity(new ErrorInfo(e.getMessage())).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo(e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo(e.getMessage())).build();
        }
    }

    @POST
    @Path("/collect")
    public Response collect() {
        try {
            var entity = gameService.collect();
            CollectResponse out = gameConverter.toCollectResponse(entity);
            return Response.ok(out).build();
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("recent")) {
                return Response.status(429).entity(new ErrorInfo(e.getMessage())).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo(e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo(e.getMessage())).build();
        }
    }


    @POST
    @Path("/catch")
    public Response catchYakamon() {
        try {
            var entity = gameService.catchYakamon();
            CatchResponse out = gameConverter.toCatchResponse(entity);
            return Response.ok(out).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/feed")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response feed(FeedRequest req) {
        if (req == null || req.yakamonUuid == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Invalid `yakamonUuid` provided."))
                    .build();
        }

        try {
            var entity = gameService.feed(req.yakamonUuid);
            FeedResponse out = gameConverter.toFeedResponse(entity);
            return Response.ok(out).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo(e.getMessage()))
                    .build();
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
