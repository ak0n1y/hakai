package fr.epita.assistants.yakamon.presentation.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartRequest {
    public String mapPath;
    public String playerName;
}
