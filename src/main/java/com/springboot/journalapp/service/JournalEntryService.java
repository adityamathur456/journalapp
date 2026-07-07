package com.springboot.journalapp.service;


import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.JorunalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {
    private final JorunalEntryRepository journalEntryRepository;
    private final UserService userService;

    public JournalEntryService(JorunalEntryRepository jorunalEntryRepository, UserService userService) {
        this.journalEntryRepository = jorunalEntryRepository;
        this.userService = userService;
    }

    @Transactional
    public JournalEntry saveEntry(String userName, JournalEntry journalEntry) {
        UserEntity user = userService.findByUserName(userName);
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepository.save(journalEntry);
        user.getJournalEntryList().add(saved);
        userService.saveUser(user);
        return saved;
    }

    public JournalEntry saveEntry(JournalEntry journalEntry) {
        journalEntry.setDate(LocalDateTime.now());
        return journalEntryRepository.save(journalEntry);
    }


    public List<JournalEntry> getAllEntries() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName) {
        UserEntity user = userService.findByUserName(userName);

        boolean ownEntry = user.getJournalEntryList()
                .removeIf(x -> x.getId().equals(id));

        if (!ownEntry) {
            return false;
        }
        journalEntryRepository.deleteById(id);
        userService.saveUser(user);

        return true;
    }

    public JournalEntry updateEntry(ObjectId id,JournalEntry newJournalEntry) {
        JournalEntry oldJournalEntry = findEntryById(id).orElse(null);

        if (oldJournalEntry != null) {
            oldJournalEntry.setTitle(!newJournalEntry.getTitle().isEmpty() ? newJournalEntry.getTitle() : oldJournalEntry.getTitle());
            oldJournalEntry.setContent(newJournalEntry.getContent() != null && !newJournalEntry.getContent().isEmpty() ? newJournalEntry.getContent() : oldJournalEntry.getContent());
            return saveEntry(oldJournalEntry);
        }

        return null;
    }
}
