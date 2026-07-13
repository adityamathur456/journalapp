package com.springboot.journalapp.cache;

import com.springboot.journalapp.entity.ConfigJournalEntity;
import com.springboot.journalapp.enums.Keys;
import com.springboot.journalapp.repository.ConfigJournalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    private final ConfigJournalRepository configJournalRepository;

    public AppCache(ConfigJournalRepository configJournalRepository) {
        this.configJournalRepository = configJournalRepository;
    }

    private final Map<Keys, String> mapCahce = new EnumMap<>(Keys.class);

    @PostConstruct
    public void init() {
        mapCahce.clear();

        List<ConfigJournalEntity> all = configJournalRepository.findAll();

        for (ConfigJournalEntity configJournalEntity : all) {
            mapCahce.put(
                    Keys.valueOf(configJournalEntity.getKey()),
                    configJournalEntity.getValue()
            );
        }
    }

    public String getValue(Keys key) {
        return mapCahce.get(key);
    }

}
