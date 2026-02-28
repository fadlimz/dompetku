package com.fadlimz.dompetku.master.user.services;

import com.fadlimz.dompetku.config.JwtUtil;
import com.fadlimz.dompetku.master.user.dtos.AuthResponseDto;
import com.fadlimz.dompetku.master.user.dtos.LoginDto;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponseDto login(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUserName(),
                        loginDto.getPassword()
                )
        );

        var userDetails = userDetailsService.loadUserByUsername(loginDto.getUserName());
        var jwtToken = jwtUtil.generateToken(userDetails);
        
        return new AuthResponseDto(jwtToken);
    }
}
