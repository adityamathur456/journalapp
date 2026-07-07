package com.springboot.journalapp.controller;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.JournalEntryService;
import com.springboot.journalapp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    private final JournalEntryService journalEntryService;
    private final UserService userService;

    public JournalEntryController(JournalEntryService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        UserEntity user = userService.findByUserName(userName);
        List<JournalEntry> allEntries = user.getJournalEntryList();

        if (allEntries != null && !allEntries.isEmpty()) {
            return ResponseEntity.ok(allEntries);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry journalEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        JournalEntry saved = journalEntryService.saveEntry(userName ,journalEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.findByUserName(authentication.getName());

        Optional<JournalEntry> journalEntry = user.getJournalEntryList()
                .stream()
                .filter(entry -> entry.getId().equals(id))
                .findFirst();

        return journalEntry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("id/{id}")
    public ResponseEntity<JournalEntry> updatePartialJournalEntryById(@PathVariable ObjectId id, @RequestBody JournalEntry newJournalEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.findByUserName(authentication.getName());
        boolean ownsEntry = user.getJournalEntryList()
                .stream()
                .anyMatch(entry -> entry.getId().equals(id));

        if (!ownsEntry) {
            return ResponseEntity.notFound().build();
        }

        JournalEntry journalEntry = journalEntryService.updateEntry(id, newJournalEntry);
        if (journalEntry != null) {
            return ResponseEntity.ok(journalEntry);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("id/{id}")
    public ResponseEntity<Void> removeJournalEntryById(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean deleted = journalEntryService.deleteById(id, userName);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
