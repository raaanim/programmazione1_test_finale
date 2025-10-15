package com.example.prenotazionebus.controller;


import com.example.prenotazionebus.entity.Trip;
import com.example.prenotazionebus.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    // POST http://localhost:8083/trips/trips
    @PostMapping("/trips")
    public Trip createTrip(@RequestBody Trip trip) {
        return tripService.saveTrip(trip);
    }

    // GET http://localhost:8083/trips/trips
    @GetMapping("/trips")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    // POST /trips/{tripId}/buy?userId=...
    @PostMapping("/{tripId}/buy")
    public ResponseEntity<String> buyTrip(@PathVariable Integer tripId,
                                          Authentication authentication) {
        try {
            // Estrai email dell'utente autenticato dal token JWT
            String email = authentication.getName();

            // Chiama un nuovo metodo del service che prende email invece di userId
            String receipt = tripService.buyTripByEmail(email, tripId);

            return ResponseEntity.ok(receipt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(422).body(e.getMessage());
        }
    }
}

