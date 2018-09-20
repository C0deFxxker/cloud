package com.lyl.study.cloud.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.study.cloud.gateway.security.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.InvalidClassException;
import java.util.Date;
import java.util.Map;

/**
 * JWT签名处理器
 *
 * @author liyilin
 */
@Data
public class JwtSigner {
    // jwt secret
    private String secret = "ThisIsASecret";
    // Token过期时间(秒)
    private long expire = 7200L;
    // json serializer
    private ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    private Map<String, Object> serializeObject(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object deserializeObject(Class<?> clazz, Map<String, Object> map) {
        try {
            String json = objectMapper.writeValueAsString(map);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeToken(Object object) {
        Map<String, Object> claims = serializeObject(object);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expire * 1000L))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeToken(String token, Class<T> requireType) throws InvalidJwtException, ExpiredJwtException {
        try {
            // parse the token.
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            Map<String, Object> body = claimsJws.getBody();

            // deserialize object to cls instance
            return (T) deserializeObject(requireType, body);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid Token: " + e.getMessage());
        }
    }
}
