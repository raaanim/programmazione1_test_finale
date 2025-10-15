package com.example.prenotazionebus.controller;


import com.example.prenotazionebus.entity.User;
import com.example.prenotazionebus.exceptions.UserNotFoundException;
import com.example.prenotazionebus.security.AuthResponse;
import com.example.prenotazionebus.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    //GET http://localhost:8083/users/all
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping(path = "/login")
    public @ResponseBody AuthResponse login(@RequestBody User user) {
        return userService.login(user);
    }

    @PostMapping(path = "/re-auth")
    public @ResponseBody AuthResponse reAuth(@RequestHeader String refreshToken) throws Exception {
        return userService.reAuth(refreshToken);
    }

    //POST http://localhost:8083/users/register
    @PostMapping(path = "/register")
    public @ResponseBody User register(@RequestBody User user) {
        return userService.addUser(user);
    }


    // GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /users/me
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String email = authentication.getName(); // prende l'email (o username) dal token
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    // PATCH http://localhost:8083/users/user/3/credit/toup?amount=12000

    @PatchMapping("/{id}/credit/toup")
    public ResponseEntity<User> addCreditInsecure(@PathVariable Integer id,
                                                  @RequestParam BigDecimal amount) {
        try {
            System.out.println("WARNING: Using INSECURE endpoint /toup-insecure");
            System.out.println("Ricarica per user ID: " + id + " - NESSUN CONTROLLO DI AUTORIZZAZIONE!");

            userService.addCredit(id, amount);
            User updatedUser = userService.getUserById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

            return ResponseEntity.ok(updatedUser);

        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH /users/me/credit/toup
    @PatchMapping("/me/credit/toup")
    public ResponseEntity<User> addCredit(@RequestParam BigDecimal amount,
                                          Authentication authentication) {
        try {
            // Estrai email dal token JWT
            String email = authentication.getName();

            System.out.println("DEBUG: Richiesta ricarica per: " + email);

            // Ricarica credito
            userService.addCreditByEmail(email, amount);

            // Ritorna utente aggiornato - GESTIONE CORRETTA DELL'OPTIONAL
            User updatedUser = userService.getByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found after update"));

            System.out.println("DEBUG: Nuovo credito: " + updatedUser.getCredit());

            return ResponseEntity.ok(updatedUser);

        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Invalid amount - " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UserNotFoundException e) {
            System.err.println("ERROR: User not found - " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}



