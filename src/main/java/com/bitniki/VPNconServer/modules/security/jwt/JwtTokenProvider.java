package com.bitniki.VPNconServer.modules.security.jwt;

import com.bitniki.VPNconServer.modules.security.exception.JwtAuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * Провайдер JWT-токенов, используемый для создания, проверки и извлечения информации из токенов.
 */
@Component
public class JwtTokenProvider{
    private final UserDetailsService userDetailsService;
    @Value("${jwt.header}")
    private String authHeader;
    @Value("${jwt.secret}")
    private String secretKey;
    private byte[] secretKeyInBytes;
    @Value("${jwt.expiration}")
    private long validityInMileSec;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Инициализация провайдера токенов.
     * Преобразует секретный ключ в массив байтов.
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        secretKeyInBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Создает JWT-токен на основе логина и роли пользователя.
     * @param login Логин пользователя.
     * @param role Роль пользователя.
     * @return Строка, представляющая JWT-токен.
     */
    public String createToken(String login, String role) {
        Claims claims = Jwts.claims().setSubject(login);
        claims.put("role", role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMileSec * 1000);

        return  Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKeyInBytes), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет валидность JWT-токена.
     * @param token JWT-токен для проверки.
     * @return true, если токен действителен, false в противном случае.
     * @throws JwtAuthException Если токен истек или недействителен.
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKeyInBytes).build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
                throw new JwtAuthException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
        }

    }

    /**
     * Получает аутентификацию на основе JWT-токена.
     * @param token JWT-токен.
     * @return Аутентификация пользователя.
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails,
                "", userDetails.getAuthorities());
    }

    /**
     * Получает логин пользователя из JWT-токена.
     * @param token JWT-токен.
     * @return Логин пользователя.
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKeyInBytes).build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     * Извлекает токен из HTTP-запроса.
     * @param request HTTP-запрос.
     * @return Токен в виде строки.
     * @throws AuthenticationCredentialsNotFoundException Если тип аутентификации неверен.
     */
    public String extractToken(HttpServletRequest request) {
        String requestAuthHeader = request.getHeader(authHeader);
        //check for null, if it null -- return;
        if(requestAuthHeader == null) {
            return null;
        }
        //else
        //Check for Bearer
        if (requestAuthHeader.startsWith("Bearer ")) {
            return requestAuthHeader.replaceFirst("^Bearer ", "");
        } else {
            throw new AuthenticationCredentialsNotFoundException("Bad authentication type");
        }
    }
}
