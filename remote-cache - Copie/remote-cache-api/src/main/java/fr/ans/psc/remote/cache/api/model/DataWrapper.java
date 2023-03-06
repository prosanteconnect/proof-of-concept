package fr.ans.psc.remote.cache.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

// default TimeUnit is seconds
//@RedisHash(value = "DataWrapper", timeToLive = 900) 
@RedisHash(value = "DataWrapper")
public class DataWrapper {
    @Id
    @JsonProperty("key")
    private String key;

    @JsonProperty(value = "schemaId", required = true)
    private String schemaId;

    // must be static because Jackson ObjectNode doesn't have default constructor,
    // which leads to failures with SpringData
    @JsonProperty(value = "bag", required = true)
    private JsonNode bag;
    
    @JsonIgnore 
    @TimeToLive
    private Long ttl;

    public DataWrapper() {
    }

    public DataWrapper(String key, String schemaId, JsonNode baggie, Long timeToLive) {
        this.key = key;
        this.schemaId = schemaId;
        bag = baggie;
        ttl = timeToLive;
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

	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
    
    
}
