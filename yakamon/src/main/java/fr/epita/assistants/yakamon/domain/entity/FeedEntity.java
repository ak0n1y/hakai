package fr.epita.assistants.yakamon.domain.entity;

import java.util.UUID;

public class FeedEntity {
    public UUID playerId;
    public UUID yakamonUuid;
    public int newEnergy;
    public int remainingScrooge;

    public FeedEntity(UUID playerId, UUID yakamonUuid, int newEnergy, int remainingScrooge) {
        this.playerId = playerId;
        this.yakamonUuid = yakamonUuid;
        this.newEnergy = newEnergy;
        this.remainingScrooge = remainingScrooge;
    }
}
