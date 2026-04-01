package fr.epita.assistants.yakamon.domain.entity;

import fr.epita.assistants.yakamon.utils.tile.TileType;
import java.util.UUID;

public class MoveEntity {
    public UUID playerId;
    public int posX;
    public int posY;
    public TileType tile;

    public MoveEntity(UUID playerId, int posX, int posY, TileType tile) {
        this.playerId = playerId;
        this.posX = posX;
        this.posY = posY;
        this.tile = tile;
    }
}
