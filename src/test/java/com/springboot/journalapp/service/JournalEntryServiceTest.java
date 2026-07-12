package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.JorunalEntryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest {

    @Mock
    private JorunalEntryRepository journalEntryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private JournalEntryService journalEntryService;

    private UserEntity user;
    private JournalEntry entry;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUserName("aditya");
        user.setJournalEntryList(new ArrayList<>());

        entry = new JournalEntry();
        entry.setId(new ObjectId());
        entry.setTitle("Title");
        entry.setContent("Content");
    }

    @Test
    void saveEntry_ShouldSaveEntryAndUpdateUser() {

        when(userService.findByUserName("aditya")).thenReturn(user);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(entry);

        JournalEntry result =
                journalEntryService.saveEntry("aditya", entry);

        assertNotNull(result);
        assertEquals(1, user.getJournalEntryList().size());
        verify(userService).saveUser(user);
        verify(journalEntryRepository).save(any(JournalEntry.class));
    }

    @Test
    void getAllEntries_ShouldReturnEntries() {

        when(journalEntryRepository.findAll())
                .thenReturn(List.of(entry));

        List<JournalEntry> list =
                journalEntryService.getAllEntries();

        assertEquals(1, list.size());
    }

    @Test
    void findEntryById_ShouldReturnEntry() {

        when(journalEntryRepository.findById(entry.getId()))
                .thenReturn(Optional.of(entry));

        Optional<JournalEntry> result =
                journalEntryService.findEntryById(entry.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void deleteById_ShouldDeleteOwnedEntry() {

        user.getJournalEntryList().add(entry);

        when(userService.findByUserName("aditya"))
                .thenReturn(user);

        boolean deleted =
                journalEntryService.deleteById(entry.getId(), "aditya");

        assertTrue(deleted);

        verify(journalEntryRepository).deleteById(entry.getId());
        verify(userService).saveUser(user);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenEntryNotOwned() {

        when(userService.findByUserName("aditya"))
                .thenReturn(user);

        boolean deleted =
                journalEntryService.deleteById(new ObjectId(), "aditya");

        assertFalse(deleted);

        verify(journalEntryRepository, never())
                .deleteById(any());
    }

    @Test
    void updateEntry_ShouldUpdateExistingEntry() {

        when(journalEntryRepository.findById(entry.getId()))
                .thenReturn(Optional.of(entry));

        when(journalEntryRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        JournalEntry updated = new JournalEntry();
        updated.setTitle("Updated");
        updated.setContent("Updated Content");

        JournalEntry result =
                journalEntryService.updateEntry(entry.getId(), updated);

        assertEquals("Updated", result.getTitle());
        assertEquals("Updated Content", result.getContent());
    }

    @Test
    void updateEntry_ShouldReturnNull_WhenNotFound() {

        when(journalEntryRepository.findById(any()))
                .thenReturn(Optional.empty());

        JournalEntry result =
                journalEntryService.updateEntry(new ObjectId(), entry);

        assertNull(result);
    }
}