package fr.epita.assistants.yakamon.data.repository;

import fr.epita.assistants.yakamon.data.model.YakamonModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class YakamonRepository implements PanacheRepository<YakamonModel> {
    public YakamonModel findByUuid(UUID uuid) {
        return find("uuid", uuid).firstResult();
    }
}
