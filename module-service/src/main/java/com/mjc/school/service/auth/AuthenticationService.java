package com.mjc.school.service.auth;

import com.mjc.school.repository.UserRepository;
import com.mjc.school.repository.model.Role;
import com.mjc.school.repository.model.User;
import com.mjc.school.service.dto.AuthenticationRequest;
import com.mjc.school.service.dto.AuthenticationResponse;
import com.mjc.school.service.dto.RegisterRequest;
import com.mjc.school.service.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.USER_DOES_NOT_EXIST;

@Service
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        LOGGER.info("Registering new user {}", request.getUsername());
        var user = new User(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        LOGGER.info("Authenticating user {}", request.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    LOGGER.error("User {} not found. Authentication failed", request.getUsername());
                    return new NotFoundException(String.format(USER_DOES_NOT_EXIST.getErrorMessage(), request.getUsername()));
                });
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}
