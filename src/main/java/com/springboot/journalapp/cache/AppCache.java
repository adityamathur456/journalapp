package com.springboot.journalapp.cache;

import com.springboot.journalapp.entity.ConfigJournalEntity;
import com.springboot.journalapp.enums.Keys;
import com.springboot.journalapp.repository.ConfigJournalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    @Autowired
    private ConfigJournalRepository configJournalRepository;

    private final Map<Keys, String> appCache = new EnumMap<>(Keys.class);

    @PostConstruct
    public void init() {
        appCache.clear();

        List<ConfigJournalEntity> all = configJournalRepository.findAll();

        for (ConfigJournalEntity configJournalEntity : all) {
            appCache.put(
                    Keys.valueOf(configJournalEntity.getKey()),
                    configJournalEntity.getValue()
            );
        }
    }

    public String getValue(Keys key) {
        return appCache.get(key);
    }

}
