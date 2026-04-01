package fr.epita.assistants.yakamon.presentation.api.response;

public class MoveResponse {
    public Integer posX;
    public Integer posY;

    public MoveResponse() {}

    public MoveResponse(Integer posX, Integer posY) {
        this.posX = posX;
        this.posY = posY;
    }
}
