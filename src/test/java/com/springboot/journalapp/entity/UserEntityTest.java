package com.springboot.journalapp.entity;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testNoArgsConstructorAndSetters() {

        UserEntity user = new UserEntity();

        ObjectId id = new ObjectId();

        user.setId(id);
        user.setUserName("aditya");
        user.setEmail("aditya@gmail.com");
        user.setPassword("password");
        user.setSentimentAnalysis(true);
        user.setRoles(List.of("USER"));
        user.setJournalEntryList(new ArrayList<>());

        assertEquals(id, user.getId());
        assertEquals("aditya", user.getUserName());
        assertEquals("aditya@gmail.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isSentimentAnalysis());
        assertEquals(List.of("USER"), user.getRoles());
        assertTrue(user.getJournalEntryList().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {

        ObjectId id = new ObjectId();

        List<JournalEntry> entries = new ArrayList<>();
        List<String> roles = List.of("USER", "ADMIN");

        UserEntity user = new UserEntity(
                id,
                "aditya",
                "aditya@gmail.com",
                true,
                "password",
                entries,
                roles
        );

        assertEquals(id, user.getId());
        assertEquals("aditya", user.getUserName());
        assertEquals("aditya@gmail.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isSentimentAnalysis());
        assertEquals(entries, user.getJournalEntryList());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testBuilder() {

        UserEntity user = UserEntity.builder()
                .userName("roman")
                .email("roman@gmail.com")
                .password("secret")
                .sentimentAnalysis(false)
                .roles(List.of("USER"))
                .journalEntryList(new ArrayList<>())
                .build();

        assertEquals("roman", user.getUserName());
        assertEquals("roman@gmail.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertFalse(user.isSentimentAnalysis());
        assertEquals(List.of("USER"), user.getRoles());
        assertNotNull(user.getJournalEntryList());
    }

    @Test
    void testEqualsAndHashCode() {

        ObjectId id = new ObjectId();

        UserEntity user1 = UserEntity.builder()
                .id(id)
                .userName("aditya")
                .email("aditya@gmail.com")
                .password("password")
                .sentimentAnalysis(true)
                .roles(List.of("USER"))
                .journalEntryList(new ArrayList<>())
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(id)
                .userName("aditya")
                .email("aditya@gmail.com")
                .password("password")
                .sentimentAnalysis(true)
                .roles(List.of("USER"))
                .journalEntryList(new ArrayList<>())
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {

        UserEntity user = UserEntity.builder()
                .userName("aditya")
                .email("aditya@gmail.com")
                .password("password")
                .roles(List.of("USER"))
                .journalEntryList(new ArrayList<>())
                .build();

        String value = user.toString();

        assertNotNull(value);
        assertTrue(value.contains("aditya"));
        assertTrue(value.contains("aditya@gmail.com"));
    }

    @Test
    void testJournalEntryListCanBeModified() {

        UserEntity user = UserEntity.builder()
                .userName("aditya")
                .password("password")
                .journalEntryList(new ArrayList<>())
                .build();

        JournalEntry entry = new JournalEntry();
        entry.setTitle("JUnit");

        user.getJournalEntryList().add(entry);

        assertEquals(1, user.getJournalEntryList().size());
        assertEquals("JUnit", user.getJournalEntryList().get(0).getTitle());
    }
}