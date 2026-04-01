package fr.epita.assistants.yakamon.presentation.api.request;

import fr.epita.assistants.yakamon.utils.Direction;

public class MoveRequest {
    public Direction direction;

    public MoveRequest() {
    }

    public MoveRequest(Direction direction) {
        this.direction = direction;
    }
}
