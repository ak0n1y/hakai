package fr.epita.assistants.yakamon.domain.service;

import fr.epita.assistants.yakamon.data.model.PlayerModel;
import fr.epita.assistants.yakamon.data.repository.PlayerRepository;
import fr.epita.assistants.yakamon.domain.entity.PlayerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerService {

    @Inject PlayerRepository playerRepository;

    public PlayerEntity getPlayer() {
        PlayerModel p = playerRepository.listAll().stream().findFirst().orElse(null);
        if (p == null) {
            throw new IllegalStateException("Game not started.");
        }
        return new PlayerEntity(
                p.getUuid(),
                p.getName(),
                p.getPosX(),
                p.getPosY(),
                p.getLastMove(),
                p.getLastCollect(),
                p.getLastCatch(),
                p.getLastFeed()
        );
    }
}
