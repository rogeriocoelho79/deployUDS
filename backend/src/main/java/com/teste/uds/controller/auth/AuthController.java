package com.teste.uds.controller.auth;

import com.teste.uds.config.security.TokenService;
import com.teste.uds.domain.entity.User;
import com.teste.uds.dto.request.LoginRequest;
import com.teste.uds.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest data) {
        System.out.println("Senha enviada: " + data.password());
        var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin123"));
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = this.authenticationManager.authenticate(authenticationToken);
            var user = (User) auth.getPrincipal();
            assert user != null;
            String token = tokenService.generateToken(user);

            return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole().name()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Falha na autenticação: " + e.getMessage());
        }
    }
}