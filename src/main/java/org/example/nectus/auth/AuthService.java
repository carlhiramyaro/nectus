package org.example.nectus.auth;

import lombok.RequiredArgsConstructor;
import org.example.nectus.auth.dto.AuthResponse;
import org.example.nectus.auth.dto.LoginRequest;
import org.example.nectus.auth.dto.RefreshRequest;
import org.example.nectus.auth.dto.RegisterRequest;
import org.example.nectus.common.security.JwtUtil;
import org.example.nectus.user.User;
import org.example.nectus.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already in Use");
        }
        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user){
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    private String createRefreshToken(User user){
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public AuthResponse refresh(RefreshRequest request){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired, please log in again");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new AuthResponse(newAccessToken, refreshToken.getToken());
    }

    public void logout(RefreshRequest request){
        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }
}
