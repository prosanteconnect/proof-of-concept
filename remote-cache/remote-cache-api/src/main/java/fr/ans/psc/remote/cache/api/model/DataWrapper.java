package fr.ans.psc.remote.cache.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// default TimeUnit is seconds
@RedisHash(value = "DataWrapper", timeToLive = 900)
public class DataWrapper {
    @Id
    @JsonProperty("key")
    private String key;

    @JsonProperty(value = "schemaId", required = true)
    private String schemaId;

    // must be static because Jackson ObjectNode doesn't have default constructor,
    // which leads to failures with SpringData
    @JsonProperty(value = "bag", required = true)
    private static JsonNode bag;

    public DataWrapper() {
    }

    public DataWrapper(String key, String schemaId, JsonNode baggie) {
        this.key = key;
        this.schemaId = schemaId;
        bag = baggie;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public JsonNode getBag() {
        return bag;
    }

    public void setBag(JsonNode baggie) {
        bag = baggie;
    }
}
