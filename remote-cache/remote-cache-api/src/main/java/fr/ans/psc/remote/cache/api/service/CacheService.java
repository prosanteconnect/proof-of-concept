package fr.ans.psc.remote.cache.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import fr.ans.psc.remote.cache.api.exception.PscMissingCacheKeyException;
import fr.ans.psc.remote.cache.api.exception.PscSchemaException;
import fr.ans.psc.remote.cache.api.model.DataWrapper;
import fr.ans.psc.remote.cache.api.repository.PscContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class CacheService {

    @Value("${schemas.file.repository}")
    private String schemasFileRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PscContextRepository pscContextRepository;

    public DataWrapper putInCache(DataWrapper wrapper) throws PscMissingCacheKeyException, PscSchemaException {
        validateSchemaConformity(wrapper);
        DataWrapper saved;
        try {
            log.debug("Trying to save entry for key {} in Redis server...", wrapper.getKey());
   //         log.debug("bag class after : {}", );
            log.debug("bag class before : {}", wrapper.getBag().getClass());
            saved = pscContextRepository.save(wrapper);
            log.debug("Entry for key {} successfully saved", wrapper.getKey());
            log.debug("bag class after : {}", wrapper.getBag().getClass());
        } catch (Exception e) {
            log.error("Error occurred while requesting Redis server", e);
            throw new PscMissingCacheKeyException();
        }
        return saved;
    }

    public DataWrapper getCachedData(String key) throws PscMissingCacheKeyException {
        Optional<DataWrapper> optionalContext;

        try {
            log.debug("requesting Redis server for key {}...", key);
            optionalContext = pscContextRepository.findById(key);
            log.debug("response received from Redis server for key {}, isReponse: {}", key, optionalContext.isPresent());
            if (optionalContext.isPresent()) {
            log.debug("schemaId: {}, TTL: {}, bag: {}", optionalContext.get().getSchemaId(), optionalContext.get().getTtl(),optionalContext.get().getBag().asText() );
            }
            
        } catch (Exception e) {
            log.error("Error occurred while requesting Redis server", e);
            throw new PscMissingCacheKeyException();
        }

        if (optionalContext.isEmpty()) {
            log.debug("No entry for key {}", key);
            throw new PscMissingCacheKeyException(HttpStatus.NOT_FOUND);
        }
        return optionalContext.get();
    }

    private void validateSchemaConformity(DataWrapper wrapper) throws PscSchemaException {
        try {
        	log.debug("appel cache service validateSchemaConformity");
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            File jsonSchemaFile = new File(schemasFileRepository, wrapper.getSchemaId() + ".json");
            InputStream inputStream = new FileInputStream(jsonSchemaFile);
            JsonSchema jsonSchema = factory.getSchema(inputStream);

            Set<ValidationMessage> errors = jsonSchema.validate(wrapper.getBag());
            if (!errors.isEmpty()) {
            	 log.error("Json-schema validation failed");
                for (ValidationMessage validationMessage : errors) {
                	 log.error(" -->  arguments {}, message {}", Arrays.toString(validationMessage.getArguments()), validationMessage.getMessage());
				}
                throw new PscSchemaException();
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            log.error("Unknown schema submitted. schemaId: {} , path: {}", wrapper.getSchemaId(), schemasFileRepository);
            throw new PscSchemaException();
        } catch (IOException e) {
            log.error("IO Exception occurred when closing stream for {}", wrapper.getSchemaId());
            throw new RuntimeException(e);
        }
    }
}
