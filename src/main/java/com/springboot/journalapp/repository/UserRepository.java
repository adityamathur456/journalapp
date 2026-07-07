package com.springboot.journalapp.repository;

import com.springboot.journalapp.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
    UserEntity findByUserName(String username);

     long deleteByUserName(String username);
}
