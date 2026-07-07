package com.springboot.journalapp.repository;
import com.springboot.journalapp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JorunalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {

}