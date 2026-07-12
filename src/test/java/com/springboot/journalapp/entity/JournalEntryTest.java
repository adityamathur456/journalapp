package com.springboot.journalapp.entity;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JournalEntryTest {

    @Test
    void testNoArgsConstructorAndSetters() {

        JournalEntry entry = new JournalEntry();

        ObjectId id = new ObjectId();
        LocalDateTime now = LocalDateTime.now();

        entry.setId(id);
        entry.setTitle("My Title");
        entry.setContent("My Content");
        entry.setDate(now);

        assertEquals(id, entry.getId());
        assertEquals("My Title", entry.getTitle());
        assertEquals("My Content", entry.getContent());
        assertEquals(now, entry.getDate());
    }

    @Test
    void testAllArgsConstructor() {

        ObjectId id = new ObjectId();
        LocalDateTime now = LocalDateTime.now();

        JournalEntry entry = new JournalEntry(
                id,
                "Title",
                "Content",
                now
        );

        assertEquals(id, entry.getId());
        assertEquals("Title", entry.getTitle());
        assertEquals("Content", entry.getContent());
        assertEquals(now, entry.getDate());
    }

    @Test
    void testEqualsAndHashCode() {

        ObjectId id = new ObjectId();
        LocalDateTime now = LocalDateTime.now();

        JournalEntry entry1 = new JournalEntry(id, "Title", "Content", now);
        JournalEntry entry2 = new JournalEntry(id, "Title", "Content", now);

        assertEquals(entry1, entry2);
        assertEquals(entry1.hashCode(), entry2.hashCode());
    }

    @Test
    void testToString() {

        JournalEntry entry = new JournalEntry(
                new ObjectId(),
                "Title",
                "Content",
                LocalDateTime.now()
        );

        String result = entry.toString();

        assertNotNull(result);
        assertTrue(result.contains("Title"));
        assertTrue(result.contains("Content"));
    }
}