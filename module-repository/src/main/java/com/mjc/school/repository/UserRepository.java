package com.mjc.school.repository;

import com.mjc.school.repository.model.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
