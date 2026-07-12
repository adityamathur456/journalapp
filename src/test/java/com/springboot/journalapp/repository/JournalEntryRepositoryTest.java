package com.springboot.journalapp.repository;

import com.springboot.journalapp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JournalEntryRepositoryTest {

    @Autowired
    private JorunalEntryRepository journalEntryRepository;

    @Test
    void testSaveAndFindById() {

        JournalEntry entry = new JournalEntry();
        entry.setTitle("JUnit Title");
        entry.setContent("JUnit Content");
        entry.setDate(LocalDateTime.now());

        JournalEntry saved = journalEntryRepository.save(entry);

        assertNotNull(saved);
        assertNotNull(saved.getId());

        Optional<JournalEntry> result =
                journalEntryRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("JUnit Title", result.get().getTitle());

        journalEntryRepository.deleteById(saved.getId());
    }

    @Test
    void testFindAll() {

        JournalEntry entry = new JournalEntry();
        entry.setTitle("FindAll");
        entry.setContent("Repository Test");
        entry.setDate(LocalDateTime.now());

        JournalEntry saved = journalEntryRepository.save(entry);

        List<JournalEntry> entries =
                journalEntryRepository.findAll();

        assertFalse(entries.isEmpty());

        assertTrue(
                entries.stream()
                        .anyMatch(e -> e.getId().equals(saved.getId()))
        );

        journalEntryRepository.deleteById(saved.getId());
    }

    @Test
    void testDeleteById() {

        JournalEntry entry = new JournalEntry();
        entry.setTitle("Delete");
        entry.setContent("Delete Test");
        entry.setDate(LocalDateTime.now());

        JournalEntry saved = journalEntryRepository.save(entry);

        ObjectId id = saved.getId();

        journalEntryRepository.deleteById(id);

        assertFalse(journalEntryRepository.findById(id).isPresent());
    }
}