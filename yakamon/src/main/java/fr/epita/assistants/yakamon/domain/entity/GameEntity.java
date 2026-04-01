
package fr.epita.assistants.yakamon.domain.entity;

import fr.epita.assistants.yakamon.utils.tile.TileType;

import java.util.List;
import java.util.UUID;

public class GameEntity {
    public UUID playerId;
    public List<List<TileType>> tiles;

    public GameEntity(UUID playerId, List<List<TileType>> tiles) {
        this.playerId = playerId;
        this.tiles = tiles;
    }
}
