package fr.epita.assistants.yakamon.presentation.api.response;

import fr.epita.assistants.yakamon.utils.tile.Collectible;

import java.util.UUID;

public class CatchResponse {
    public UUID playerId;
    public int posX;
    public int posY;

    public Collectible caught;
    public int remainingYakaballs;

    public CatchResponse(UUID playerId, int posX, int posY, Collectible caught, int remainingYakaballs) {
        this.playerId = playerId;
        this.posX = posX;
        this.posY = posY;
        this.caught = caught;
        this.remainingYakaballs = remainingYakaballs;
    }
}
