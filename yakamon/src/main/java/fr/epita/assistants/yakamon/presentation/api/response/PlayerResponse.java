package fr.epita.assistants.yakamon.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerResponse {
    public UUID uuid;
    public String name;
    public Integer posX;
    public Integer posY;
    public LocalDateTime lastMove;
    public LocalDateTime lastCollect;
    public LocalDateTime lastCatch;
    public LocalDateTime lastFeed;

    public PlayerResponse(UUID uuid, String name, Integer posX, Integer posY,
                          LocalDateTime lastMove, LocalDateTime lastCollect,
                          LocalDateTime lastCatch, LocalDateTime lastFeed) {
        this.uuid = uuid;
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.lastMove = lastMove;
        this.lastCollect = lastCollect;
        this.lastCatch = lastCatch;
        this.lastFeed = lastFeed;
    }
}
