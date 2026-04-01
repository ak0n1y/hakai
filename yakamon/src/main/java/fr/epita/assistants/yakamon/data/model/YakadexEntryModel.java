
package fr.epita.assistants.yakamon.data.model;

import fr.epita.assistants.yakamon.utils.ElementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "yakadex_entry")
public class YakadexEntryModel {

    @Id
    public Integer id;

    @Column(length = 20, nullable = false)
    public String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "first_type")
    public ElementType firstType;

    @Enumerated(EnumType.STRING)
    @Column(name = "second_type")
    public ElementType secondType;

    @Column
    public String description;

    @Column(name = "evolution_id")
    public Integer evolution;

    @Column(name = "evolve_threshold")
    public Integer evolveThreshold;

    @Column
    public Boolean caught;
}
