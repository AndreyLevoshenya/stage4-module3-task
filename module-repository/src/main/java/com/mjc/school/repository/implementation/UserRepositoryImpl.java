package com.mjc.school.repository.implementation;

import com.mjc.school.repository.UserRepository;
import com.mjc.school.repository.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User, Long> implements UserRepository {
    public Optional<User> findByUsername(String username){
        return Optional.ofNullable(
                entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getSingleResult());
    }

    @Override
    public void update(User entity, User newEntity) {
        entity.setUsername(newEntity.getUsername());
        entity.setPassword(newEntity.getPassword());
    }
}
