package fr.epita.assistants.yakamon.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity
@Table(name = "yakamon")
@Getter
@Setter
public class YakamonModel {
    @Column(length = 20)
    private String nickname;

    @Column(name = "energy_points")
    private Integer energyPoints;

    @Column(name = "yakadex_entry_id")
    private Integer yakadexEntryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yakadex_entry_id", insertable = false, updatable = false)
    private YakadexEntryModel yakadexEntry;



    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

}
