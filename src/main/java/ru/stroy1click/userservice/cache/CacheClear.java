package ru.stroy1click.userservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClear {

    private final CacheManager cacheManager;

    public void clearUserById(Long id){ //check usages
        log.info("clearUser");
        Cache cache = this.cacheManager.getCache("user");
        if(cache != null){
            cache.evict(id);
        }
    }

}
