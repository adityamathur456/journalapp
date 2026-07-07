package sample;

import com.springboot.journalapp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal_sample")
public class JournalEntryControllerSample {
    private Map<ObjectId, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping
    public List<JournalEntry> getAllJournalEntries() {
        return new ArrayList<>(journalEntries.values());
    }

    @PostMapping
    public ResponseEntity<String> createEntry(@RequestBody JournalEntry journalEntry) {
        if (journalEntries.containsKey(journalEntry.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body("Journal Entry already exists");
        }

        journalEntries.put(journalEntry.getId(), journalEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body("Journal Entry Created Successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable Long id) {
        JournalEntry entry = journalEntries.get(id);

        if (entry == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(entry);
    }

    @PatchMapping("/{id}/title")
    public ResponseEntity<String> updateTitleOfJournalEntryById(@PathVariable Long id, @RequestBody JournalEntry journalEntry) {
        JournalEntry entry = journalEntries.get(id);

        if (entry == null)
            return ResponseEntity.notFound().build();

        entry.setTitle(journalEntry.getTitle());

        return ResponseEntity.ok("Title Updated Successfully");
    }

    @PatchMapping("/{id}/content")
    public ResponseEntity<String> updateContentOfJournalEntryById(@PathVariable Long id, @RequestBody JournalEntry journalEntry) {
        JournalEntry entry = journalEntries.get(id);

        if (entry == null)
            return ResponseEntity.notFound().build();

        entry.setContent(journalEntry.getContent());

        return ResponseEntity.ok("Content Updated Successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateJournalEntryById(@PathVariable ObjectId id, @RequestBody JournalEntry journalEntry) {
        if (!journalEntries.containsKey(id))
            return ResponseEntity.notFound().build();

        journalEntry.setId(id);
        journalEntries.put(id, journalEntry);

        return ResponseEntity.ok("Journal Entry Updated Successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeJournalEntryById(@PathVariable ObjectId id) {
        if (!journalEntries.containsKey(id))
            return ResponseEntity.notFound().build();

        journalEntries.remove(id);

        return ResponseEntity.ok("Journal Entry Deleted Successfully");
    }
}
