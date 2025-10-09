package com.example.prenotazionebus.entity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface UserRepository extends CrudRepository<User, Integer> {
        User findByEmail(String email);
    }

