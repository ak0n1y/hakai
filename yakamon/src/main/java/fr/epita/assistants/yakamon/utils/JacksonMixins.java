package fr.epita.assistants.yakamon.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.epita.assistants.yakamon.utils.tile.Collectible;
import fr.epita.assistants.yakamon.utils.tile.CollectibleType;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class JacksonMixins implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        mapper.addMixIn(Collectible.class, CollectibleMixin.class);
    }

    // MixIn : on "cache" getCollectibleType() pour éviter le doublon avec JsonTypeInfo(property="type")
    public interface CollectibleMixin {
        @JsonIgnore
        CollectibleType getCollectibleType();
    }
}
