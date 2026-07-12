package com.springboot.journalapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.JournalEntryService;
import com.springboot.journalapp.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JournalEntryController.class)
class JournalEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JournalEntryService journalEntryService;

    @MockitoBean
    private UserService userService;

    private JournalEntry createEntry() {
        JournalEntry entry = new JournalEntry();
        entry.setId(new ObjectId());
        entry.setTitle("JUnit");
        entry.setContent("Testing Controller");
        entry.setDate(LocalDateTime.now());
        return entry;
    }

    private UserEntity createUser(JournalEntry entry) {

        UserEntity user = new UserEntity();
        user.setUserName("aditya");
        user.setPassword("password");

        List<JournalEntry> entries = new ArrayList<>();
        entries.add(entry);

        user.setJournalEntryList(entries);

        return user;
    }

    @Test
    @WithMockUser(username = "aditya")
    void getAllJournalEntries_ShouldReturn200() throws Exception {

        JournalEntry entry = createEntry();

        when(userService.findByUserName("aditya"))
                .thenReturn(createUser(entry));

        mockMvc.perform(get("/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("JUnit"))
                .andExpect(jsonPath("$[0].content").value("Testing Controller"));
    }

    @Test
    @WithMockUser(username = "aditya")
    void getAllJournalEntries_ShouldReturn404_WhenEmpty() throws Exception {

        UserEntity user = new UserEntity();
        user.setUserName("aditya");
        user.setJournalEntryList(new ArrayList<>());

        when(userService.findByUserName("aditya"))
                .thenReturn(user);

        mockMvc.perform(get("/journal"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "aditya")
    void createEntry_ShouldReturn201() throws Exception {

        JournalEntry entry = createEntry();

        when(journalEntryService.saveEntry(eq("aditya"), any(JournalEntry.class)))
                .thenReturn(entry);

        mockMvc.perform(post("/journal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("JUnit"))
                .andExpect(jsonPath("$.content").value("Testing Controller"));
    }

    @Test
    @WithMockUser(username = "aditya")
    void getJournalEntryById_ShouldReturn200() throws Exception {

        JournalEntry entry = createEntry();

        when(userService.findByUserName("aditya"))
                .thenReturn(createUser(entry));

        mockMvc.perform(get("/journal/id/" + entry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("JUnit"));
    }

    @Test
    @WithMockUser(username = "aditya")
    void getJournalEntryById_ShouldReturn404() throws Exception {

        UserEntity user = new UserEntity();
        user.setUserName("aditya");
        user.setJournalEntryList(new ArrayList<>());

        when(userService.findByUserName("aditya"))
                .thenReturn(user);

        mockMvc.perform(get("/journal/id/" + new ObjectId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "aditya")
    void updateJournalEntry_ShouldReturn200() throws Exception {

        JournalEntry existing = createEntry();

        JournalEntry updated = new JournalEntry();
        updated.setTitle("Updated");
        updated.setContent("Updated Content");

        when(userService.findByUserName("aditya"))
                .thenReturn(createUser(existing));

        when(journalEntryService.updateEntry(eq(existing.getId()), any(JournalEntry.class)))
                .thenReturn(updated);

        mockMvc.perform(
                        patch("/journal/id/" + existing.getId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    @WithMockUser(username = "aditya")
    void updateJournalEntry_ShouldReturn404_WhenEntryNotOwned() throws Exception {

        UserEntity user = new UserEntity();
        user.setUserName("aditya");
        user.setJournalEntryList(new ArrayList<>());

        JournalEntry updated = new JournalEntry();
        updated.setTitle("Updated");

        when(userService.findByUserName("aditya"))
                .thenReturn(user);

        mockMvc.perform(
                        patch("/journal/id/" + new ObjectId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "aditya")
    void deleteJournalEntry_ShouldReturn204() throws Exception {

        ObjectId id = new ObjectId();

        when(journalEntryService.deleteById(id, "aditya"))
                .thenReturn(true);

        mockMvc.perform(
                        delete("/journal/id/" + id)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "aditya")
    void deleteJournalEntry_ShouldReturn404() throws Exception {

        ObjectId id = new ObjectId();

        when(journalEntryService.deleteById(id, "aditya"))
                .thenReturn(false);

        mockMvc.perform(
                        delete("/journal/id/" + id)
                                .with(csrf())
                )
                .andExpect(status().isNotFound());
    }
}