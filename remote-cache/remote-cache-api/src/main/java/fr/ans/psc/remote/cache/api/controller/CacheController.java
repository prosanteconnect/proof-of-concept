package fr.ans.psc.remote.cache.api.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fr.ans.psc.remote.cache.api.exception.PscContextSharingException;
import fr.ans.psc.remote.cache.api.exception.PscMissingCacheKeyException;
import fr.ans.psc.remote.cache.api.model.DataWrapper;
import fr.ans.psc.remote.cache.api.service.CacheService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    private static String CACHE_KEY_HEADER= "X-CACHE-KEY";
    
    @GetMapping()
    public ResponseEntity<DataWrapper> getDataCached() {
        try {
            log.debug("Get context requested");
            String key = getKeyOfCache();
            log.debug("PS key is {}", key);
            DataWrapper pscContext = cacheService.getCachedData(key);
            return new ResponseEntity<>(pscContext, HttpStatus.OK);
        } catch (PscContextSharingException e) {
            return new ResponseEntity<>(e.getStatus());
        }
    }

    @PutMapping(consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<DataWrapper> putDataInCache(@RequestBody DataWrapper wrapper) {
        try {
            log.debug("Put cache requested");
            String key = getKeyOfCache();
            DataWrapper toStore = new DataWrapper(null, wrapper.getSchemaId(), wrapper.getBag());
            toStore.setKey(key);
            DataWrapper savedContext = cacheService.putInCache(toStore);
            return new ResponseEntity<>(savedContext, HttpStatus.OK);
        } catch (PscContextSharingException e) {
        	log.error("PscContextSharingException {} \n {} \n {}", e.getMessage(),e.getStackTrace(), e.getCause());
            return new ResponseEntity<>(e.getStatus());
        }
    }

    /**
     * Gets the accessToken
     *
     * @return the PSC accessToken
     */
    public String getKeyOfCache() throws PscMissingCacheKeyException {
        List<String> tmp = new ArrayList<String>();
        Enumeration<String> keys = getHttpRequest().getHeaders(CACHE_KEY_HEADER);
        while (keys.hasMoreElements()) {           
            String key = keys.nextElement();
            log.debug("At least one {} header has been found. Value: {} ", CACHE_KEY_HEADER, key);
            tmp.add(key);
        }
        if (tmp.size() !=1 ) {
            log.error("No key provided!");
            throw new PscMissingCacheKeyException(HttpStatus.BAD_REQUEST);
        }
        log.debug("getCacheKey returned with key: {}", tmp.get(0));

        return tmp.get(0);
    }

    /*
     * Gets the HttpServletRequest
     *
     * @return the HttpServletRequest
     */
    public HttpServletRequest getHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest();
    }
}
