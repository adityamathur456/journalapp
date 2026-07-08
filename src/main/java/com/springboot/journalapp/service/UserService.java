package com.springboot.journalapp.service;


import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity saveNewUser(UserEntity user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));

            UserEntity savedUser = userRepository.save(user);
            log.info("User {} successfully saved", user.getUserName());

            return savedUser;
        } catch (Exception e) {
            log.warn("Error for creating user {}, {}",user.getUserName(), e.getMessage());
            return null;
        }
    }

    public UserEntity saveNewUserAdmin(UserEntity admin) {
        admin.setPassword((passwordEncoder.encode(admin.getPassword())));
        admin.setRoles((List.of("USER","ADMIN")));
        return userRepository.save((admin));
    }

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public UserEntity findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    public boolean deleteUserById(ObjectId id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById((id));
            return true;
        }
        return false;
    }

    public UserEntity updateUser(String username, UserEntity user) {
        UserEntity userIndDB = findByUserName(username);

        if (userIndDB != null) {
            userIndDB.setUserName(user.getUserName());
            userIndDB.setPassword(user.getPassword());
            saveNewUser(userIndDB);
            return userIndDB;
        }

        return null;
    }

    public boolean deleteUser(String username) {
        return userRepository.deleteByUserName(username) > 0;
    }
}
