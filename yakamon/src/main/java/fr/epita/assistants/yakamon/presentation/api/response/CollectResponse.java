package fr.epita.assistants.yakamon.presentation.api.response;

import fr.epita.assistants.yakamon.utils.tile.TileType;

public class CollectResponse {
    public TileType tileType;

    public CollectResponse() {}

    public CollectResponse(TileType tileType) {
        this.tileType = tileType;
    }
}
