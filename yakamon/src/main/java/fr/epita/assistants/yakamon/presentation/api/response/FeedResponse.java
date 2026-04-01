package fr.epita.assistants.yakamon.presentation.api.response;

import java.util.UUID;

public class FeedResponse {
    public UUID playerId;
    public UUID yakamonUuid;
    public int newEnergy;
    public int remainingScrooge;

    public FeedResponse(UUID playerId, UUID yakamonUuid, int newEnergy, int remainingScrooge) {
        this.playerId = playerId;
        this.yakamonUuid = yakamonUuid;
        this.newEnergy = newEnergy;
        this.remainingScrooge = remainingScrooge;
    }
}
