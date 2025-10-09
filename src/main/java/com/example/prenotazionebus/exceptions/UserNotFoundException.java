package com.example.prenotazionebus.exceptions;

public class UserNotFoundException extends RuntimeException {

    // Costruttore che riceve l'id dell'utente e crea un messaggio chiaro
    public UserNotFoundException(Integer id) {
        super("Utente con ID " + id + " non trovato");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}

