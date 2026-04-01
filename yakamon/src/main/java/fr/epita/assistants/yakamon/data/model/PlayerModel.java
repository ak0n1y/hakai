package fr.epita.assistants.yakamon.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "player")
@Getter
@Setter
public class PlayerModel {
    @Column(length = 20)
    private String name;

    @Column(name = "pos_x")
    private Integer posX;

    @Column(name = "pos_y")
    private Integer posY;

    @Column(name = "last_move")
    private LocalDateTime lastMove;

    @Column(name = "last_catch")
    private LocalDateTime lastCatch;

    @Column(name = "last_collect")
    private LocalDateTime lastCollect;

    @Column(name = "last_feed")
    private LocalDateTime lastFeed;

    @Id
    private UUID uuid;
}
