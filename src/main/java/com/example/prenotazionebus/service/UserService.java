package com.example.prenotazionebus.service;

import com.example.prenotazionebus.entity.User;
import com.example.prenotazionebus.entity.UserRepository;
import com.example.prenotazionebus.exceptions.UserNotFoundException;
import com.example.prenotazionebus.security.AuthResponse;
import com.example.prenotazionebus.security.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;


@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public User addUser(User user) {
        user.setRoles(Collections.singletonList("USER"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public Iterable<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(repository.findByEmail(email));
    }

    public AuthResponse login(User user) {
        User u = repository.findByEmail(user.getEmail());
        if (u != null) {
            if (passwordEncoder.matches(user.getPassword(), u.getPassword())) {
                AuthResponse authResponse = new AuthResponse(jwtUtils.generateJwtToken(u.getEmail()), jwtUtils.generateRefreshToken(u.getEmail()));
                return authResponse;
            }
        }
        return null;
    }

    public AuthResponse reAuth(String refreshToken) throws Exception {
        AuthResponse authResponse = new AuthResponse();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(refreshToken)
                    .getBody();
            User user = repository.findByEmail(claims.getSubject());
            if (user != null) {
                authResponse.setAccessToken(jwtUtils.generateJwtToken(user.getEmail()));
                authResponse.setRefreshToken(jwtUtils.generateRefreshToken(user.getEmail()));
            }
        } catch (Exception e) {
            // Gestisci eventuali errori durante la decodifica del token
            authResponse.setMsg(String.valueOf(e));
            throw new Exception("Errore durante la decodifica del token", e);
        } finally {
            return authResponse;
        }
    }


    public AuthResponse refresh(User user) {
        User u = repository.findByEmail(user.getEmail());
        if (u != null) {
            if (passwordEncoder.matches(user.getPassword(), u.getPassword())) {
                AuthResponse authResponse = new AuthResponse(jwtUtils.generateJwtToken(u.getEmail()), jwtUtils.generateRefreshToken(u.getEmail()));
                return authResponse;
            }
        }
        return null;
    }


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User addCredit(Integer id, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("L'importo deve essere maggiore di zero");
        }

        // Recupero utente
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Aggiungo il credito
        BigDecimal newCredit = user.getCredit().add(amount);
        user.setCredit(newCredit);

        // Salvo e ritorno utente aggiornato
        return userRepository.save(user);
    }

    // questo metodo crea una eccezione se non trova l'utente via email
    public User getUserByEmail(String email) {
        return Optional.ofNullable(repository.findByEmail(email))
                .orElseThrow(() -> new UserNotFoundException("Utente non trovato con email: " + email));
    }


    public void addCreditByEmail(String email, BigDecimal amount) {
        // Trova utente
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // Validazione importo
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive. Received: " + amount);
        }

        // Validazione limite massimo (opzionale, per sicurezza)
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new IllegalArgumentException("Amount too large. Maximum: 10000");
        }

        // Aggiungi credito
        BigDecimal oldCredit = user.getCredit();
        BigDecimal newCredit = oldCredit.add(amount);
        user.setCredit(newCredit);
        repository.save(user);

        System.out.println("DEBUG: Credit updated for user ID " + user.getId() +
                " from " + oldCredit + " to " + newCredit);
    }
}

