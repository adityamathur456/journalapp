package com.springboot.journalapp.repository;

import com.springboot.journalapp.entity.ConfigJournalEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalRepository extends MongoRepository<ConfigJournalEntity, ObjectId> {

}
