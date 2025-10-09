package com.example.prenotazionebus.service;

import com.example.prenotazionebus.entity.Trip;
import com.example.prenotazionebus.entity.TripRepository;
import com.example.prenotazionebus.entity.User;
import com.example.prenotazionebus.entity.UserRepository;
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

    // Acquisto corsa
    public String buyTrip(Integer userId, Integer tripId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        BigDecimal price = trip.getPrice();
        if (user.getCredit().compareTo(price) < 0) {
            throw new RuntimeException("Insufficient credit"); // mappera poi in 422
        }

        user.setCredit(user.getCredit().subtract(price));
        userRepository.save(user);

        return "Receipt: userId=" + userId +
                ", tripId=" + tripId +
                ", charged=" + price +
                ", remaining=" + user.getCredit();
    }

    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

}

