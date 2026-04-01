package fr.epita.assistants.yakamon.converter;

import fr.epita.assistants.yakamon.domain.entity.PlayerEntity;
import fr.epita.assistants.yakamon.presentation.api.response.PlayerResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlayerConverter {

    public PlayerResponse toPlayerResponse(PlayerEntity e) {
        return new PlayerResponse(
                e.uuid,
                e.name,
                e.posX,
                e.posY,
                e.lastMove,
                e.lastCollect,
                e.lastCatch,
                e.lastFeed
        );
    }
}
