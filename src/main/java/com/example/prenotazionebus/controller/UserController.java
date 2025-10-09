package com.example.prenotazionebus.controller;


import com.example.prenotazionebus.entity.User;
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
    public @ResponseBody AuthResponse reAuth (@RequestHeader String refreshToken) throws Exception {
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

        // PATCH /users/{id}/credit/toup
        @PatchMapping("/{id}/credit/toup")
        public ResponseEntity<User> addCredit(@PathVariable Integer id,
                                              @RequestParam BigDecimal amount) {
            User updated = userService.addCredit(id, amount);
            return ResponseEntity.ok(updated);
        }
    }



