package com.example.prenotazionebus.service;

import com.example.prenotazionebus.entity.Trip;
import com.example.prenotazionebus.entity.TripRepository;
import com.example.prenotazionebus.entity.User;
import com.example.prenotazionebus.entity.UserRepository;
import com.example.prenotazionebus.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    // Lista corse
    public List<Trip> getAllTrips() {
        return (List<Trip>) tripRepository.findAll();
    }


    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    // Acquisto corsa
    public String buyTripByEmail(String email, Integer tripId) {
        // Carica utente autenticato dall'email (estratta dal token)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // Carica corsa
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Verifica credito
        BigDecimal price = trip.getPrice();
        if (user.getCredit().compareTo(price) < 0) {
            throw new RuntimeException("Credito insufficiente");
        }

        // Addebita prezzo
        BigDecimal newCredit = user.getCredit().subtract(price);
        user.setCredit(newCredit);
        userRepository.save(user);

        // Genera ricevuta
        return String.format("Acquistato viaggio da %s a %s. Credito residuo: %.2f",
                trip.getOrigin(), trip.getDestination(), newCredit);
    }
}

