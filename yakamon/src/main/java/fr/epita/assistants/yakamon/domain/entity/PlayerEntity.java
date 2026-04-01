package fr.epita.assistants.yakamon.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlayerEntity {
    public UUID uuid;
    public String name;
    public Integer posX;
    public Integer posY;
    public LocalDateTime lastMove;
    public LocalDateTime lastCollect;
    public LocalDateTime lastCatch;
    public LocalDateTime lastFeed;

    public PlayerEntity(UUID uuid, String name, Integer posX, Integer posY,
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
