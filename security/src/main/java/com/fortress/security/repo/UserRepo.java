package com.fortress.security.repo;

import com.fortress.security.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    List<User> findByUsernameStartsWith(String prefix);
}
