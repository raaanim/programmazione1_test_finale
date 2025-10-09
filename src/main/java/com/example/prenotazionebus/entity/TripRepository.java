package com.example.prenotazionebus.entity;

import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends CrudRepository<Trip, Integer> {

    // esempio di query derivata: trova tutte le corse per una destinazione
    List<Trip> findByDestination(String destination);

    // trova tutte le corse dopo una certa data/ora
    List<Trip> findByDepartureTimeAfter(LocalDateTime dateTime);
}

