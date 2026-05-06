package com.teste.uds.service;

import com.teste.uds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        System.out.println(">>> Tentando carregar usuário: " + username); // LOG DE DEBUG
        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println(">>> Usuário encontrado no banco! Hash: " + user.getPassword());
                    return user;
                })
                .orElseThrow(() -> {
                    System.out.println(">>> Usuário NÃO encontrado!");
                    return new UsernameNotFoundException("User not found");
                });
    }
}
