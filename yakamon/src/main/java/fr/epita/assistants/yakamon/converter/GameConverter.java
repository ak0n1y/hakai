package fr.epita.assistants.yakamon.converter;

import fr.epita.assistants.yakamon.domain.entity.CatchEntity;
import fr.epita.assistants.yakamon.domain.entity.CollectEntity;
import fr.epita.assistants.yakamon.domain.entity.FeedEntity;
import fr.epita.assistants.yakamon.domain.entity.GameEntity;
import fr.epita.assistants.yakamon.domain.entity.MoveEntity;
import fr.epita.assistants.yakamon.presentation.api.response.CatchResponse;
import fr.epita.assistants.yakamon.presentation.api.response.CollectResponse;
import fr.epita.assistants.yakamon.presentation.api.response.FeedResponse;
import fr.epita.assistants.yakamon.presentation.api.response.MoveResponse;
import fr.epita.assistants.yakamon.presentation.api.response.StartResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameConverter {

    public StartResponse toStartResponse(GameEntity e) {
        return new StartResponse(e.tiles);
    }

    public MoveResponse toMoveResponse(MoveEntity entity) {
        if (entity == null) return new MoveResponse();
        return new MoveResponse(entity.posX, entity.posY);
    }

    public CollectResponse toCollectResponse(CollectEntity e) {
        if (e == null) return new CollectResponse();
        return new CollectResponse(e.tileType);
    }


    public CatchResponse toCatchResponse(CatchEntity e) {
        return new CatchResponse(e.playerId, e.posX, e.posY, e.caught, e.remainingYakaballs);
    }

    public FeedResponse toFeedResponse(FeedEntity e) {
        return new FeedResponse(e.playerId, e.yakamonUuid, e.newEnergy, e.remainingScrooge);
    }
}
