package com.tuorg.veterinaria.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Proveedor de tokens JWT.
 * 
 * Esta clase maneja la generación, validación y extracción de información
 * de los tokens JWT utilizados para autenticación.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Component
public class JwtTokenProvider {

    /**
     * Clave secreta para firmar los tokens JWT.
     * Se obtiene de la configuración de la aplicación.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Tiempo de expiración del token en milisegundos.
     * Se obtiene de la configuración de la aplicación.
     */
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Obtiene la clave secreta para firmar tokens.
     * 
     * @return SecretKey para firmar tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Extrae el nombre de usuario del token.
     * 
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token.
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token.
     * 
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token.
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token ha expirado.
     * 
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Genera un token JWT para un usuario.
     * 
     * @param userDetails Detalles del usuario
     * @return Token JWT
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea un token JWT con los claims especificados.
     * 
     * @param claims Claims a incluir en el token
     * @param subject Sujeto del token (usualmente el username)
     * @return Token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Valida un token JWT.
     * 
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

