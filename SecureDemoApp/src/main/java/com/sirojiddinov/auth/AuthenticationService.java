package com.sirojiddinov.auth;

import com.sirojiddinov.config.JWTService;
import com.sirojiddinov.repository.UserRepository;
import com.sirojiddinov.user.Role;
import com.sirojiddinov.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JWTService service;
    private final AuthenticationManager manager;
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = UserModel.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(encoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwt = service.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()));
        var user = repository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        var jwt = service.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).build();

    }
}
